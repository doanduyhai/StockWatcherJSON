package com.google.gwt.sample.stockwatcher_json.service;

import java.util.Random;

public class JsonRandomizeService
{
	public Integer getRandomNumber()
	{
		Random rand = new Random();
		return rand.nextInt();
	}
}
