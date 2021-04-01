package com.example.TradeRepo;

import java.util.List;

public interface DataProvider {
    public List<Trade> getData();
    public void publishToDBQueue(Trade trade);
}
