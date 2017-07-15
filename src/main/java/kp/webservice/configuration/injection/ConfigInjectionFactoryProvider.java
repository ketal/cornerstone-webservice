package kp.webservice.configuration.injection;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;

import kp.webservice.configuration.WebserviceConfiguration;

@Singleton
public class ConfigInjectionFactoryProvider<T extends WebserviceConfiguration> extends AbstractValueFactoryProvider {

    @Inject
    private WebserviceConfiguration config;
    
    @Inject
    public ConfigInjectionFactoryProvider(MultivaluedParameterExtractorProvider mpep, ServiceLocator locator) {
        super(mpep, locator, Parameter.Source.UNKNOWN);
    }

    @Override
    protected Factory<?> createValueFactory(final Parameter parameter) {
        final Class<?> classType = parameter.getRawType();

        final Config configAnnotation = parameter.getAnnotation(Config.class);
        if (configAnnotation == null) {
            return null;
        }
        
//        if(WebserviceConfiguration.class.isAssignableFrom(classType)) {
//            return new AbstractContainerRequestValueFactory<Object>() {
//                
//                @Override
//                public Object provide() {
//                    return config;
//                }
//
//            };
//        }

        if(WebserviceConfiguration.class.isAssignableFrom(classType)) {
            return new AbstractContainerRequestValueFactory<T>() {
                
                @SuppressWarnings("unchecked")
                @Override
                public T provide() {
                    return (T) config;
                }

            };
        }
        
        return null;
    }
    
    public static class ConfigInjectionResolver extends ParamInjectionResolver<Config> {
        public ConfigInjectionResolver() {
            super(ConfigInjectionFactoryProvider.class);
        }
    }
}
