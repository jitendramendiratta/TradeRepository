package com.example.tradeRepo;

import com.example.tradeRepo.processor.TradeProcessor;
import com.example.tradeRepo.provider.FileDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.lang.invoke.MethodHandles;


@Configuration
public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

    @Autowired
    Environment environment;

    @Bean(name="fileDataProvider")
    public FileDataProvider getFileDataProvider()
    {
        FileDataProvider fdp = null;
        try {
            fdp = (FileDataProvider) Class.forName(environment.getProperty("source.provider")).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return  fdp;

    }

    @Bean(name="tradeProcessor")
    public TradeProcessor getTradeProcessor()
    {
        TradeProcessor tradeProcessor = null;
        try {
            tradeProcessor = (TradeProcessor) Class.forName(environment.getProperty("data.transformer")).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return  tradeProcessor;

    }
}
