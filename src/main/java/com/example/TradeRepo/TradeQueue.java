package com.example.TradeRepo;

import com.example.TradeRepo.dataobject.Trade;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Component
public class TradeQueue extends LinkedBlockingQueue<Trade> {
}
