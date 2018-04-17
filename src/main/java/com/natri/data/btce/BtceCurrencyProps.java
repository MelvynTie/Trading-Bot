package com.natri.data.btce;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.natri.core.bot.BtceBFBot;

public class BtceCurrencyProps {

	final static Logger logger = Logger.getLogger(BtceCurrencyProps.class);

	public static final Properties props = new Properties();

	static {
		try{
			props.load(new FileInputStream(new File("/home/melvyn/Desktop/bot/res/btce.properties")));
		}catch( Exception e) {
			System.out.println("Wrong filepath of btce.properties");
			logger.error(e.getMessage());
		}
	}
}
