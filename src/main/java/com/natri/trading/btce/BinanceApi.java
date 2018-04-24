package com.natri.trading.btce;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.Trade;
import org.apache.log4j.Logger;

public class BinanceApi {

    final static Logger logger = Logger.getLogger(BinanceApi.class);
    public static BinanceApiRestClient api;

    public BinanceApi(String api_key, String api_secret)
    {
        return;
    }
    public static void start(String api_key, String api_secret)
    {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(api_key, api_secret);
        api = factory.newRestClient();
        logger.info("Test");

    }

}
