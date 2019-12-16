package com.chopsticks.ehub.sdk.spring;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

import com.chopsticks.common.utils.Reflect;
import com.chopsticks.ehub.sdk.exception.SdkException;
import com.chopsticks.ehub.sdk.impl.DefaultSdkClient;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.primitives.Primitives;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@EnableConfigurationProperties(EhubClientHelperStarter.class)
@ConditionalOnProperty(prefix = "ehub", name = "enable", havingValue = "true")
public class EhubSpringBootStarter implements EnvironmentAware, BeanFactoryPostProcessor, BeanPostProcessor, ApplicationListener<ContextRefreshedEvent>{
	
	private Environment env;
	private Map<String, DefaultSdkClient> clients = Maps.newHashMap();
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		List<String> groupNames = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(env.getRequiredProperty("ehub.groupNames"));
		String serverPath = env.getProperty("ehub.serverPath");
		String namesrvAddr = env.getProperty("ehub.namesrvAddr");
		log.info("enable ehub, groupNames : {}, serverPath : {}, namesrvAddr : {}", groupNames, serverPath, namesrvAddr);
		for(String groupName : groupNames) {
			DefaultSdkClient client = new DefaultSdkClient(groupName);
			client.setServerPath(serverPath);
			client.setNamesrvAddr(namesrvAddr);
			clients.put(groupName, client);
			beanFactory.registerSingleton(groupName, client);
		}
	}
	
	@Override
	public void setEnvironment(Environment env) {
		this.env = env;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(!clients.isEmpty()) {
			Object obj = bean;
			if(bean instanceof Advised) {
				try {
					obj = ((Advised)bean).getTargetSource().getTarget();
				} catch(Throwable e) {
					throw new SdkException(e);
				}
			}
			EhubService ehubService = obj.getClass().getAnnotation(EhubService.class);
			if(ehubService != null) {
				DefaultSdkClient client = clients.get(ehubService.value());
				if(client == null && Strings.isNullOrEmpty(ehubService.value()) && clients.size() == 1) {
					client = clients.values().iterator().next();
				}
				if(client == null) {
					throw new SdkException();
				}
				
				
				Map<Class<?>, Object> services = client.getServices();
				if(services == null) {
					services = Maps.newHashMap();
					client.setServices(services);
				}
				if(ehubService.inter().isInterface()) {
					services.put(ehubService.inter(), bean);
				}else {
					Class<?>[] classes = obj.getClass().getInterfaces();
					if(classes != null && classes.length == 1) {
						services.put(classes[0], bean);
					}else {
						throw new SdkException();
					}
				}
			}
		}
		return bean;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext().getParent() == null) {
		    EhubClientHelperStarter ehubClientHelper = event.getApplicationContext().getBean(EhubClientHelperStarter.class);
			String sdkCacheKey = env.getProperty("ehub.sdkCache");
			if(!Strings.isNullOrEmpty(sdkCacheKey)) {
				SdkCache cache = event.getApplicationContext().getBean(sdkCacheKey, SdkCache.class);
				ehubClientHelper.setCache(cache);
			}
			for(Entry<String, DefaultSdkClient> entry : clients.entrySet()) {
				String groupName = entry.getKey();
				DefaultSdkClient client = entry.getValue();
				buildClient(client, groupName);
				client.setClientProxy(ehubClientHelper);
				client.setTransactionChecker(ehubClientHelper);
				client.start();
			}
		}
	}

	private void buildClient(DefaultSdkClient client, String groupName) {
		for(Method method : client.getClass().getMethods()) {
			if(method.getName().startsWith("set") && method.getParameterCount() == 1) {
				String name = method.getName().substring(3);
				name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
				Class<?> type = method.getParameterTypes()[0];
				String paramValue = env.getProperty(String.format("ehub.%s.%s", groupName, name));
				if(!Strings.isNullOrEmpty(paramValue)){
					if(type == String.class) {
						Reflect.on(client).call(method.getName(), paramValue);
					}else if(type.isPrimitive()) {
						Object value = null;
						Class<?> warpType = Primitives.wrap(type);
						if(warpType.getSimpleName().equals("Integer")) {
							value = Reflect.on(warpType).call("parseInt", paramValue).get();
						}else {
							value = Reflect.on(warpType).call("parse" + warpType.getSimpleName(), paramValue).get();
						}
						Reflect.on(client).call(method.getName(), value);
						
					}
				}
			}
		}
	}
}
