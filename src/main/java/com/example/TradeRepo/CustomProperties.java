package com.example.TradeRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

public class CustomProperties {

    private final Map<String, String> properties = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    // @PostConstruct
    public void init() {
        AutowireCapableBeanFactory beanFactory = this.applicationContext.getAutowireCapableBeanFactory();
        // iterate over properties and register new beans
    }

}