package com.natri.core.bot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.net.MalformedURLException;

import javax.swing.text.MaskFormatter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.lang.System;
import java.lang.Runtime;

import com.natri.core.BFAlgorithm;
import com.natri.core.GraphFactory;
import com.natri.core.dto.CurrencyGraph;
import com.natri.core.tradebeans.CurrencyCycle;
import com.natri.data.IQuoteDataContainer;
import com.natri.data.btce.BtceCurrencyProps;
import com.natri.data.btce.BtceQuotePuller;
import com.natri.trading.btce.BtceMultiStepTradeExecutor;
import com.natri.trading.btce.MultiStepCurrencyTrade;
import com.natri.trading.btce.BinanceApi;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

import org.junit.Assert;

public class BtceBFBot {
	final static Logger logger = Logger.getLogger(BtceBFBot.class);
	public static final Properties props = new Properties();

	public CurrencyCycle runAlgo() throws Exception, MalformedURLException{
		String allPairsUrl = "https://api.binance.com//api/v1/ticker/24hr";

		//BTC, ETH, LTC
		String [] pairsInMaster = {"USDT-BTC","BTC-ETH", "BTC-LTC","ETH-LTC"};
		List<String> pairs = Arrays.asList(pairsInMaster);
		Set<String> pairsSet = new HashSet<>(pairs);

		BtceQuotePuller puller = new BtceQuotePuller();
		puller.setMasterUrl(allPairsUrl, pairsSet);
		List<IQuoteDataContainer> quotes = puller.getData();

		CurrencyGraph graph = GraphFactory.buildUndirectedCurrencyGraph(quotes);
		// Add to algorithm
		BFAlgorithm algo = new BFAlgorithm(graph);
		//algo.setProperties(BtceCurrencyProps.props);
		CurrencyCycle cycle = algo.bellmanFord("ETH","BTC");
		if(cycle != null) {
			if(cycle.getPotentialProfitPercentage() > 0) {
				cycle.logTrades();
				cycle.logExampleProfit();
			} else {
				cycle.logExampleProfit();
			}
		}

		puller = null;
		algo = null;
		graph = null;
		return cycle;
	}

	// This is an example, the implementation is not complete.
	public void runTradeExecutor(CurrencyCycle cycle) throws Exception{
		MultiStepCurrencyTrade trades = new MultiStepCurrencyTrade("USD", 100, cycle);
		BtceMultiStepTradeExecutor executor = new BtceMultiStepTradeExecutor(trades);
		executor.executeTrades();
	}


	public static void main(String[] args) throws Exception{
		//Catch the Keyboard Interrupt
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run(){ System.out.println("Exit");}});

		props.load(new FileInputStream(new File("./res/log4j.properties")));
		PropertyConfigurator.configure(props);
        // Input api keys here
        BinanceApi.start("", "");
		logger.info("Starting bot...");

		while(true) {
			Long start = System.currentTimeMillis();
			BtceBFBot bot = new BtceBFBot();
			CurrencyCycle cycle = bot.runAlgo();
			if(cycle != null && cycle.getPotentialProfitMinusFees() > 0.2 && cycle.getTrades().size() == 3) {
				logger.error("Profitable trade: " + cycle.getTradesString());

				// If you were to run the executor - here is where you would do it.
				//bot.runTradeExecutor(cycle);
			}
			bot = null;
			System.out.println();
			System.out.println();
			System.out.println();
			Long end = System.currentTimeMillis();
			System.out.println("Took : " + ((end - start))+"ms");
			// Btc-e has a 2 second cache.
			Thread.sleep(2000);
		}
	}
}
