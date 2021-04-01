package com.example.TradeRepo;

import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class TradeQueue extends LinkedBlockingQueue<Trade> {
}
