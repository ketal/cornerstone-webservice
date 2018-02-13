package com.github.ketal.cornerstone.webservice.configuration.injection;

import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.inject.Named;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;

import com.github.ketal.cornerstone.webservice.configuration.BaseWebserviceConfig;

public class ConfigInjectionResolver implements InjectionResolver<Config> {

    @Inject
    @Named(InjectionResolver.SYSTEM_RESOLVER_NAME)
    InjectionResolver<Inject> systemInjectionResolver;

    @Override
    public Object resolve(Injectee injectee, ServiceHandle<?> handle) {
        try {
            Type injecteeRequiredType = injectee.getRequiredType();
            if (BaseWebserviceConfig.class == injecteeRequiredType || BaseWebserviceConfig.class.isAssignableFrom(getClass(injecteeRequiredType))) {
                return systemInjectionResolver.resolve(injectee, handle);
            }
        } catch (ClassNotFoundException e) {
            // Nothing to do, just return null
        }
        
        return null;
    }
    
    @Override
    public boolean isConstructorParameterIndicator() {
        return true;
    }

    @Override
    public boolean isMethodParameterIndicator() {
        return true;
    }

    private Class<?> getClass(Type type) throws ClassNotFoundException {
//        return (Class<?>) type;
        String className = getClassName(type);
        if (className == null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }

    private static final String TYPE_NAME_PREFIX = "class ";

    private String getClassName(Type type) {
        if (type == null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_NAME_PREFIX)) {
            className = className.substring(TYPE_NAME_PREFIX.length());
        }
        return className;
    }

}
