package com.web.appleshop.service.impl;

import com.web.appleshop.entity.Stock;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.StockRepository;
import com.web.appleshop.service.StockService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;

    StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public Stock refundedStock(Integer stockId, Integer quantity) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(
                () -> new NotFoundException("Không tìm thấy phiên bản sản phẩm nào có id: " + stockId)
        );
        stock.setQuantity(stock.getQuantity() + quantity);
        return stockRepository.save(stock);
    }

    @Override
    public Set<Stock> refundedStocks(Map<Integer, Integer> stockIdQuantityMap) {
        List<Stock> stocks = stockRepository.findAllById(stockIdQuantityMap.keySet());
        stocks.forEach(stock -> stock.setQuantity(stock.getQuantity() + stockIdQuantityMap.get(stock.getId())));
        return Set.copyOf(stockRepository.saveAll(stocks));
    }
}
