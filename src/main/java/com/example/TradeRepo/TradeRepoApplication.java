package com.example.TradeRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.invoke.MethodHandles;

@SpringBootApplication
@EnableScheduling
public class TradeRepoApplication implements CommandLineRunner{

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

	@Autowired
	private ApplicationContext context;

	public static void main(String[] args) {
		SpringApplication.run(TradeRepoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Starting Trade Repo application....");

		FileDataProvider fdp = (FileDataProvider)context.getBean("fileDataProvider");
		Thread providerThread = new Thread(fdp);
		providerThread.start();

		TradeProcessor trdProcessor = (TradeProcessor)context.getBean("tradeProcessor");
		Thread processorThread = new Thread(trdProcessor);
		processorThread.start();

		// wait for processor to finish.
		processorThread.join();

		logger.info("finished processing");

	}

}

