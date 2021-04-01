package com.example.tradeRepo.queue;

import com.example.tradeRepo.dataobject.Trade;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Component
public class TradeQueue extends LinkedBlockingQueue<Trade> {
}
