package com.example.tradeRepo.processor;

import com.example.tradeRepo.datafabric.TradeDataFabricLayer;
import com.example.tradeRepo.queue.TradeQueue;
import com.example.tradeRepo.dataobject.Trade;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.Optional;


/**
 * This class is to process the incoming trades from Trade Queue.
 */
@Getter
@Setter
public class TradeProcessor extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

    @Autowired
    TradeQueue tradeQueue;

    @Autowired
    TradeDataFabricLayer tradeDataFabricLayer;

    @Override
    public void run(){
        boolean fileProcessed = false;
        while(true)
        {
            try {
                while (true) {
                    int returnValue = 0;
                    Trade trade = tradeQueue.take();
                    processTrade(trade);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    public void processTrade(Trade trade) {
        int returnValue;
        if (trade != null && trade.getTradeId() != 0) {
            logger.info("Processing Trade - {}",trade.getTradeId());
            Optional<Trade> exisingTrade = getTradeDataFabricLayer().findbyid(trade.getTradeId());

            if (!isValidMaturityDate(trade)) {
                // bad maturity date
                logger.info("Skipping Trade - {} . Invalid maturity Date - {} ",trade.getTradeId(),trade.getMaturityDate());
                return;
            }
            if (!exisingTrade.isPresent()) {
                // insert case
                logger.info("New Trade Received. Inserting Trade - {}",trade.getTradeId());
                returnValue = getTradeDataFabricLayer().insert(trade);
            } else {
                if (isSameVersion(exisingTrade.get(), trade)) {
                    logger.info("Trade amendment received for existing version. Updating Trade - {}, version -{}",trade.getTradeId(),trade.getVersion());
                    getTradeDataFabricLayer().update(trade);
                    return;
                }
                if (isNewerVersion(exisingTrade.get(), trade)) {
                    // here means - its higher version and  need to insert
                    logger.info("Trade amendment received with newer version. Inserting Trade - {}, version -{}",trade.getTradeId(),trade.getVersion());
                    returnValue = getTradeDataFabricLayer().insert(trade);
                    return;
                } else {
                    // bad out of sequence
                    // log and skip
                    logger.info(" Out of sequence or Invalid trade received. Skipping Trade - {}, version -{}",trade.getTradeId(),trade.getVersion());
                }
            }

        }
    }

    /**
     * this methods checks if newTrade has a higher version than oldTrade
     * @param oldTrade
     * @param newTrade
     * @return
     */

    public boolean isNewerVersion(Trade oldTrade, Trade newTrade) {

        if(oldTrade.getVersion()<newTrade.getVersion())
            return true;
        else
            return false;
    }


    /**
     * This method checks if old and new trades have same version
     * @param oldTrade
     * @param newTrade
     * @return
     */
    public boolean isSameVersion(Trade oldTrade, Trade newTrade) {
        if(oldTrade.getVersion()==newTrade.getVersion())
            return true;
        else
            return false;
    }

    /**
     * This method checks if trade has a valid maturity date
     * @param newTrade
     * @return
     */
    public boolean isValidMaturityDate(Trade newTrade) {

        if( newTrade.getMaturityDate().before(new Date()))
            return false;
        else
            return true;
    }

}
