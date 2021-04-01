package com.example.tradeRepo.processor;

import com.example.tradeRepo.datafabric.TradeDataFabricLayer;
import com.example.tradeRepo.queue.TradeQueue;
import com.example.tradeRepo.dataobject.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.Optional;


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
                    if (trade != null && trade.getTradeId() != 0) {
                        logger.info("Processing Trade - {}",trade.getTradeId());
                        Optional<Trade> exisingTrade = tradeDataFabricLayer.findbyid(trade.getTradeId());

                        if (!isValidMaturityDate(trade)) {
                            // bad maturity date
                            logger.info("Skipping Trade - {} . Invalid maturity Date - {} ",trade.getTradeId(),trade.getMaturityDate());
                            continue;
                        }
                        if (!exisingTrade.isPresent()) {
                            // insert case
                            logger.info("New Trade Received. Inserting Trade - {}",trade.getTradeId());
                            returnValue = tradeDataFabricLayer.insert(trade);
                        } else {
                            if (isSameVersion(exisingTrade.get(), trade)) {
                                logger.info("Trade amendment received for existing version. Updating Trade - {}, version -{}",trade.getTradeId(),trade.getVersion());
                                tradeDataFabricLayer.update(trade);
                                continue;
                            }
                            if (isNewerVersion(exisingTrade.get(), trade)) {
                                // here means - its higher version and  need to insert
                                logger.info("Trade amendment received with newer version. Inserting Trade - {}, version -{}",trade.getTradeId(),trade.getVersion());
                                returnValue = tradeDataFabricLayer.insert(trade);
                                continue;
                            } else {
                                // bad out of sequence
                                // log and skip
                                logger.info(" Out of sequence or Invalid trade received. Skipping Trade - {}, version -{}",trade.getTradeId(),trade.getVersion());
                            }
                        }

                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }


    public boolean isNewerVersion(Trade oldTrade, Trade newTrade) {

        if(oldTrade.getVersion()<newTrade.getVersion())
            return true;
        else
            return false;
    }



    public boolean isSameVersion(Trade oldTrade, Trade newTrade) {
        if(oldTrade.getVersion()==newTrade.getVersion())
            return true;
        else
            return false;
    }

    public boolean isValidMaturityDate(Trade newTrade) {

        if( newTrade.getMaturityDate().before(new Date()))
            return false;
        else
            return true;
    }
}
