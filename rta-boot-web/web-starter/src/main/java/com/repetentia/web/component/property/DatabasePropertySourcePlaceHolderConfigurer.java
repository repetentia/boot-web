package com.repetentia.web.component.property;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

import com.repetentia.support.reload.ReLoadable;

public class DatabasePropertySourcePlaceHolderConfigurer extends PlaceholderConfigurerSupport implements EnvironmentAware, ReLoadable {
	private static final String id = "propertySource";
    public static final String LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME = "localProperties";
    public static final String ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME = "environmentProperties";
    
    @Nullable
    private MutablePropertySources propertySources;
    private Environment env;
    private DatabasePropertyLoader databasePropertyLoader; 
    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
        load();
    }

    private void load() {
    	if (databasePropertyLoader == null) databasePropertyLoader = new DatabasePropertyLoader(env);
    	databasePropertyLoader.loadDataBasePropertySource();
    }
    
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.propertySources == null) {
            this.propertySources = ((ConfigurableEnvironment) env).getPropertySources();
        }
        processProperties(beanFactory, new PropertySourcesPropertyResolver(this.propertySources));
    }

    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, final ConfigurablePropertyResolver propertyResolver) throws BeansException {

        propertyResolver.setPlaceholderPrefix(this.placeholderPrefix);
        propertyResolver.setPlaceholderSuffix(this.placeholderSuffix);
        propertyResolver.setValueSeparator(this.valueSeparator);

        StringValueResolver valueResolver = strVal -> {
            String resolved = (this.ignoreUnresolvablePlaceholders ? propertyResolver.resolvePlaceholders(strVal) : propertyResolver.resolveRequiredPlaceholders(strVal));
            if (this.trimValues) {
                resolved = resolved.trim();
            }
            return (resolved.equals(this.nullValue) ? null : resolved);
        };

        doProcessProperties(beanFactoryToProcess, valueResolver);
    }

    @Override
    @Deprecated
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) {
        throw new UnsupportedOperationException("Call processProperties(ConfigurableListableBeanFactory, ConfigurablePropertyResolver) instead");
    }

	@Override
	public void reload() {
		load();
	}

	@Override
	public String id() {
		return id;
	}

}
