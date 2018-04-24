package com.natri.data.btce;

import org.json.JSONObject;

import com.natri.data.IQuoteDataContainer;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 * Takes the data from a btce api call for one quote
 * and implments appropriate getters + setters and parsing +
 * type inference of the data from API.
 *
 * Types should be cast to match the interface signature.
 *
 * @author ard
 *
 */
public class BtceQuote implements IQuoteDataContainer {

	JSONObject data;
	String pair;
	String start;
	String end;

	Double bid;
	Double ask;
	Double last;
	Long timestamp;

	public void setCurrencyPair(String pair) {
		this.pair = pair;

		String pattern = "(.*)(BTC|ETH|USDT|BNB)";
		Pattern re = Pattern.compile(pattern);
		Matcher matches = re.matcher(pair);

		if(matches.find()){
			this.end = matches.group(2);
			this.start = matches.group(1);
		}
		else
		{
			this.end="NULL";
			this.start="NULL";
		}
		//If you were to change the currency pair master
		//start = BtceCurrencyProps.props.getProperty(pair + ".start");
		//end = BtceCurrencyProps.props.getProperty(pair + ".end");
	}

	@Override
	public void setData(JSONObject jsonData) throws Exception{
		this.data = jsonData;
		this.ask = jsonData.getDouble("askPrice");
		this.bid = jsonData.getDouble("bidPrice");
		this.last = jsonData.getDouble("lastPrice");
		String tm = jsonData.getString("closeTime");
		String ts = tm.replaceAll("[^a-z0-9A-Z]","");
		ts = ts.replaceAll("T","");
		this.timestamp = Long.parseLong(ts);
	}
	@Override
	public String getCurrencyPairCode() {
		return pair;
	}

	@Override
	public Double getLast() {
		return last;
	}

	@Override
	public Long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public Double getBid() {
		return bid;
	}

	@Override
	public Double getAsk() {
		return ask;
	}

	@Override
	public String getStartCurrency() {
		// take the currency pair and return the correct start currency
		// e.g. pair = "BTC_USD"  --> USD
		return start;
	}

	@Override
	public String getEndCurrency() {
		return end;
	}

}
