package com.web.appleshop.service;

import com.web.appleshop.entity.Stock;

import java.util.Map;
import java.util.Set;

public interface StockService {
    Stock refundedStock(Integer stockId, Integer quantity);
    Set<Stock> refundedStocks(Map<Integer, Integer> stockIdQuantityMap);
}
