package com.sunbooking.util;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility class for retrieving internationalized messages.
 * Provides convenient methods to get messages from resource bundles.
 */
@Component
public class MessageUtil {

    private final MessageSource messageSource;

    public MessageUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Get a message for the given key using the current locale.
     *
     * @param key the message key
     * @return the resolved message
     */
    public String getMessage(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, null, locale);
    }

    /**
     * Get a message for the given key and arguments using the current locale.
     *
     * @param key  the message key
     * @param args the message arguments
     * @return the resolved message with arguments
     */
    public String getMessage(String key, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }

    /**
     * Get a message for the given key and specific locale.
     *
     * @param key    the message key
     * @param locale the locale
     * @return the resolved message
     */
    public String getMessageWithLocale(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    /**
     * Get a message for the given key, arguments, and specific locale.
     *
     * @param key    the message key
     * @param args   the message arguments
     * @param locale the locale
     * @return the resolved message with arguments
     */
    public String getMessageWithLocale(String key, Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, locale);
    }

    /**
     * Get a message with a default value if the key is not found.
     *
     * @param key          the message key
     * @param defaultValue the default value
     * @return the resolved message or default value
     */
    public String getMessageOrDefault(String key, String defaultValue) {
        try {
            return getMessage(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
