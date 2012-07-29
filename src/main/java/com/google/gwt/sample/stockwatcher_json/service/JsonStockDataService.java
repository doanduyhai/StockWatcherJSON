package com.google.gwt.sample.stockwatcher_json.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gwt.sample.stockwatcher_json.domain.StockDataDto;

public class JsonStockDataService
{

	private static final double MAX_PRICE = 100.0; // $100.00
	private static final double MAX_PRICE_CHANGE = 0.02; // +/- 2%

	public List<StockDataDto> getStockDataFromSymboles(String rawSymbols)
	{
		String[] stockSymbols = rawSymbols.split(" ");
		Random rnd = new Random();
		List<StockDataDto> stockDatas = new ArrayList<StockDataDto>();
		for (String stockSymbol : stockSymbols)
		{
			double price = rnd.nextDouble() * MAX_PRICE;
			double change = price * MAX_PRICE_CHANGE * (rnd.nextDouble() * 2f - 1f);

			stockDatas.add(new StockDataDto(stockSymbol, price, change));
		}

		return stockDatas;
	}
}
