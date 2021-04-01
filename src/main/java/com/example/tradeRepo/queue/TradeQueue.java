package com.example.tradeRepo.queue;

import com.example.tradeRepo.dataobject.Trade;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Queue to pass on trade objects between threads.
 */

@Component
public class TradeQueue extends LinkedBlockingQueue<Trade> {
}
