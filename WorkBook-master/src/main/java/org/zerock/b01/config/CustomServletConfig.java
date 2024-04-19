package org.zerock.b01.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CustomServletConfig implements WebMvcConfigurer {

    @Override // static 폴더 제외 처리
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // /js/** 경로에 해당하는 정적 파일은 classpath:/static/js/에서 로드
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        // /fonts/** 경로에 해당하는 정적 파일은 classpath:/static/fonts/에서 로드
        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("classpath:/static/fonts/");

        // /css/** 경로에 해당하는 정적 파일은 classpath:/static/css/에서 로드
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // /assets/** 경로에 해당하는 정적 파일은 classpath:/static/assets/에서 로드
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");

    }

}