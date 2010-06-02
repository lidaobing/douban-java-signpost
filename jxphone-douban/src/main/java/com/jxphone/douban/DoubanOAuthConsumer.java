package com.jxphone.douban;

import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.http.HttpParameters;

public class DoubanOAuthConsumer extends DefaultOAuthConsumer {
	
	private static final long serialVersionUID = 1L;
	
	public DoubanOAuthConsumer(String consumerKey, String consumerSecret) {
		super(consumerKey, consumerSecret);
		HttpParameters parameters = new HttpParameters();
		parameters.put("realm", "");
		setAdditionalParameters(parameters);
	}
}
