package com.example.tradeRepo.app;

import com.example.tradeRepo.processor.TradeProcessor;
import com.example.tradeRepo.provider.FileDataProvider;
import com.example.tradeRepo.queue.TradeQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.invoke.MethodHandles;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.example.tradeRepo.*"})
public class TradeRepoApplication implements CommandLineRunner{

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

	@Autowired
	private ApplicationContext context;

	@Autowired
	TradeQueue tradeQueue;

	public static void main(String[] args) {
		SpringApplication.run(TradeRepoApplication.class, args);
	}

	/**
	 * This method creates the Provider thread to read trade data from csv file and
	 * it also create processor thread to process the trades from the incoming queue.
	 * Provider is configurable and we can plug in any new provider from properties file.
	 * Similarly processor is also configurable so that it can be changed from properties file.
	 *
	 * @param args
	 * @throws Exception
	 */
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

