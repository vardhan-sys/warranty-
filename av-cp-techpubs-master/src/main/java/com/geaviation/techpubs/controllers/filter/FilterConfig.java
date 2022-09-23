package com.geaviation.techpubs.controllers.filter;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class FilterConfig implements ApplicationContextAware {

        private static ApplicationContext context;

        public static ApplicationContext getApplicationContext() {
            return context;
        }

        public static <T> T getBean(String name, Class<T> clazz){
            return context.getBean(name, clazz);
        }

        @Override
        public void setApplicationContext(ApplicationContext appContext) throws BeansException {
            context = appContext;
        }
}
