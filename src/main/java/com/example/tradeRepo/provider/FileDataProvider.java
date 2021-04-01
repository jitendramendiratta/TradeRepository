package com.example.tradeRepo.provider;

import com.example.tradeRepo.dataobject.Trade;
import com.example.tradeRepo.queue.TradeQueue;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is to read the trade data from a csv file and publish to Trade queue
 * which will be processed by Trade processor.
 */


public class FileDataProvider extends Thread implements DataProvider {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());
    @Autowired
    public ResourceLoader resourceLoader;
    @Autowired
    public TradeQueue tradeQueue;
    @Autowired
    public Environment env;

    @Override
    public void run() {
        List<Trade> tradeList = getData();
        if (tradeList != null && !tradeList.isEmpty()) {
            for (Trade trade : tradeList) {
                publishToDBQueue(trade);
            }
        }
    }

    @Override
    public List<Trade> getData() {
        String fileName = env.getProperty("source.file");
        return readDataLineByLine(fileName);
    }

    public List<Trade> readDataLineByLine(String file) {
        List<Trade> tradeList = new ArrayList<Trade>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd");
        DateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Resource resource = resourceLoader.getResource("classpath:" + file);
            InputStream input = resource.getInputStream();
            File inputfile = resource.getFile();
            parseCSVToReadTrades(tradeList, dateTimeFormatter, dateformatter, inputfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tradeList;
    }

    public static void parseCSVToReadTrades(List<Trade> tradeList, DateTimeFormatter dateTimeFormatter, DateFormat dateformatter, File inputfile) throws IOException, ParseException {
        FileReader reader = new FileReader(inputfile);
        CSVReader csvReader = new CSVReader(reader);
        String[] nextRecord;
        while ((nextRecord = csvReader.readNext()) != null) {
            Trade trade = new Trade();
            for (int i = 0; i < nextRecord.length; i++) {
                switch (i) {
                    case 0:
                        trade.setTradeId(Integer.parseInt(nextRecord[i]));
                        break;
                    case 1:
                        trade.setVersion(Integer.parseInt(nextRecord[i]));
                        break;
                    case 2:
                        trade.setCounterPartyId(nextRecord[i]);
                        break;

                    case 3:
                        trade.setBookId(nextRecord[i]);
                        break;
                    case 4:
                        //trade.setMaturityDate(Date.of(LocalDateTime.of(LocalDate.parse(nextRecord[i], formatter), LocalTime.MAX), ZoneId.systemDefault()));
                        trade.setMaturityDate(dateformatter.parse(nextRecord[i]));
                        break;
                    case 5:
                        trade.setCreatedDate(ZonedDateTime.of(LocalDateTime.of(LocalDate.parse(nextRecord[i], dateTimeFormatter), LocalTime.now()), ZoneId.systemDefault()));
                        break;
                    case 6:
                        trade.setExpired(Boolean.parseBoolean(nextRecord[i]));
                        break;
                    default:
                        logger.error("Invalid token");
                }
            }
            tradeList.add(trade);
        }
    }

    @Override
    public void publishToDBQueue(Trade trade) {
        if (trade != null) {
            try {
                tradeQueue.put(trade);
                logger.info("Published to DB queue - {}", trade.getTradeId());
            } catch (InterruptedException e) {
                logger.error("Error in publishing to DB queue - {}", trade.getTradeId());
            }
        }
    }

}
