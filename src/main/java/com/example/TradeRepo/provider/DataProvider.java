package com.example.TradeRepo.provider;

import com.example.TradeRepo.dataobject.Trade;

import java.util.List;

public interface DataProvider {
    public List<Trade> getData();
    public void publishToDBQueue(Trade trade);
}
