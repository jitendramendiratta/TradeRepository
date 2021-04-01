package com.example.tradeRepo.scheduledTasks;

import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.example.tradeRepo.datafabric.TradeDataFabricLayer;
import com.example.tradeRepo.dataobject.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled Task that will mark matured trade as expired.
 */

@Component
public class ScheduleTaskMatureTrades {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());


    @Autowired
    public TradeDataFabricLayer tradeDataFabricLayer;

    /**
     * This method will run at scheduled time and will mark EXPIRED=TRUE on matured trades.
     * @return
     */
    @Scheduled(cron = "0 59 23 * * ?")
    public int matureTrades() {
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
        return counter;

    }
}
