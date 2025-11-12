package com.sunbooking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 * Registers interceptors, resource handlers and other web-related
 * configurations
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;

    public WebMvcConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add logging interceptor for all paths
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/plugins/**",
                        "/admin/css/**",
                        "/admin/js/**",
                        "/admin/images/**",
                        "/admin/plugins/**",
                        "/webjars/**",
                        "/favicon.ico");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Admin static resources
        registry.addResourceHandler("/admin/css/**")
                .addResourceLocations("classpath:/static/admin/css/");

        registry.addResourceHandler("/admin/js/**")
                .addResourceLocations("classpath:/static/admin/js/");

        registry.addResourceHandler("/admin/images/**")
                .addResourceLocations("classpath:/static/admin/images/");

        registry.addResourceHandler("/admin/plugins/**")
                .addResourceLocations("classpath:/static/admin/plugins/");
    }
}
