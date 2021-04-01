package com.example.tradeRepo.provider;

import com.example.tradeRepo.dataobject.Trade;

import java.util.List;

public interface DataProvider {
    public List<Trade> getData();
    public void publishToDBQueue(Trade trade);
}
