package com.electricity.service;

import com.electricity.Exceptions.BeanInitializationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BeanFactory {

    private static final String PROPERTIES_FILE = "application.properties";

    private static final Map<String, Object> beans = new HashMap<>();

    static {
        loadBeans();
    }

    private static void loadBeans() {
        Properties properties = new Properties();
        try (InputStream inputStream = BeanFactory.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                throw new IOException("Properties file not found: " + PROPERTIES_FILE);
            }
            properties.load(inputStream);
            for (String key : properties.stringPropertyNames()) {
                String className = properties.getProperty(key);
                try {
                    Object bean = Class.forName(className).newInstance();
                    beans.put(key, bean);
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    throw new BeanInitializationException("Error initializing bean for class: " + className, e);
                }
            }
        } catch (IOException e) {
            throw new BeanInitializationException("Error loading properties file: " + PROPERTIES_FILE, e);
        }
    }
    public static Object getBean(String beanName) {
        return beans.get(beanName);
    }
}
