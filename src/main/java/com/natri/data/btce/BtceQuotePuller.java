package com.natri.data.btce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.natri.core.bot.BtceBFBot;
import com.natri.data.IQuoteDataContainer;
import com.natri.data.IQuotePuller;
import com.natri.data.JsonReader;


/**
 * Implementation class that pulls quotes for BTCe and
 * creates quote data containers.
 *
 *
 * @author ard
 *
 */
public class BtceQuotePuller implements IQuotePuller {

	private Long msThrottleBetweenCalls = 500L;
	private Map<String, String> pairs = new HashMap<String, String>();
	private String masterPairUrl = null;
	private Set<String> pairsInMaster = null;
	final static Logger logger = Logger.getLogger(BtceQuotePuller.class);


	private JSONObject getJsonFromURL(String url) throws Exception {
		JsonReader jr = new JsonReader();
		return jr.readJsonFromUrl(url);
	}

	public IQuoteDataContainer transformAPIResponse(JSONObject json) throws Exception {
		// parse + validate json.
		BtceQuote quote = new BtceQuote();
		quote.setData(json);
		return null;
	}

	@Override
	public void setCurrencyPairEndpoint(String pairCode, String url) {
		pairs.put(pairCode, url);
	}

	public void setMasterUrl(String url, Set<String> pairsAtEndpoint) {
		this.masterPairUrl = url;
		this.pairsInMaster = pairsAtEndpoint;
	}

	@Override
	public IQuoteDataContainer getDataForPair(String pairCode) throws Exception{
		if(pairs.containsKey(pairCode) == false)
			throw new Exception("Unkown currency pair: " + pairCode);
		JSONObject res = getJsonFromURL(pairs.get(pairCode));
		IQuoteDataContainer quote = transformAPIResponse(res);
		return quote;
	}

	@Override
	public List<IQuoteDataContainer> getData() throws Exception{
		List<IQuoteDataContainer> quotes = new ArrayList<IQuoteDataContainer>();
		if(masterPairUrl == null || pairsInMaster == null )
			throw new Exception("Master pair list not set");

		logger.debug("Pulling URL: " + masterPairUrl);

		JSONObject obj = getJsonFromURL(masterPairUrl);
		JSONArray res = obj.optJSONArray("result");

		for(int index = 0; index < res.length(); index++)
		{
			JSONObject quoteData = res.getJSONObject(index);
			String key = quoteData.getString("MarketName");
			BtceQuote quote = new BtceQuote();
			quote.setCurrencyPair(key);
			quote.setData(quoteData);
			quotes.add(quote);
			if(quote.getLast() == null) {
				throw new Exception("Quote last set incorrectly set for pair: " + "");
			}
		}
		return quotes;
	}
}
