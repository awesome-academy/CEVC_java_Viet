package com.sunbooking.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

/**
 * Configuration for internationalization (i18n) support.
 * Enables multi-language support for the application with English (default) and
 * Vietnamese.
 * 
 * The application locale is configured via the APP_LOCALE environment variable
 * or
 * application.properties.
 * 
 * Examples:
 * - APP_LOCALE=en (English)
 * 
 * This is a backend-driven approach where the entire application uses a single
 * language
 * configured at the system level.
 */
@Configuration
public class I18nConfig {

    @Value("${app.locale:en}")
    private String appLocale;

    /**
     * Configure the MessageSource bean for loading message properties.
     * Messages are loaded from messages.properties and messages_vi.properties
     * files.
     *
     * @return configured MessageSource
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // Cache for 1 hour
        messageSource.setDefaultLocale(getConfiguredLocale());
        return messageSource;
    }

    /**
     * Configure the LocaleResolver to use a fixed locale from configuration.
     * The locale is determined by the APP_LOCALE environment variable.
     * 
     * This means the entire application runs in a single language, configured at
     * deployment time.
     *
     * @return configured LocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new FixedLocaleResolver(getConfiguredLocale());
    }

    /**
     * Get the configured locale from the app.locale property.
     * 
     * @return the Locale object based on configuration
     */
    private Locale getConfiguredLocale() {
        // if ("vi".equalsIgnoreCase(appLocale)) {
        // return new Locale("vi");
        // }
        return Locale.ENGLISH; // Default to English
    }
}
