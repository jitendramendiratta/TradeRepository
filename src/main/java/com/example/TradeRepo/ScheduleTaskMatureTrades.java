package com.example.TradeRepo;

import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTaskMatureTrades {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());


    @Autowired
    TradeDataFabricLayer tradeDataFabricLayer;

    @Scheduled(cron = "0 50 22 * * ?")
    public void matureTrades() {
        logger.info("Starting job to mature trades");
        int counter = 0;

        Optional<List<Trade>> tradeList = tradeDataFabricLayer.findMatureTrades();
        if (tradeList.isPresent()) {
            Iterator<Trade> it = tradeList.get().iterator();
            while (it.hasNext()) {
                Trade trade = it.next();
                trade.setExpired(true);
                tradeDataFabricLayer.update(trade);
                logger.info("Marked trade {} as expired.",trade.getTradeId());
                counter++;
            }
            logger.info("Eligible trades to be set as Expired - {}",tradeList.get().size());
            logger.info("Trades set as Expired - {}",counter);

        }else{
            logger.info("No trades received to be updated");
        }
    }
}
