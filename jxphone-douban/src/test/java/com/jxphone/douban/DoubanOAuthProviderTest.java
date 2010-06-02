package com.jxphone.douban;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthException;

import org.junit.Ignore;
import org.junit.Test;

public class DoubanOAuthProviderTest {
	static {
		System.setProperty("debug", "1");
	}
	
	String consumerKey = "042bc009d7d4a04d0c83401d877de0e7";
	String consumerSecret = "a9bb2d7f8cc00110";
	
	String token = "0306646daca492b609132d4905edb822";
	String tokenSecret = "22070cec426cb925";
	
	@Test
	public void testRetrieveRequestToken() throws OAuthException {
		OAuthConsumer consumer = new DoubanOAuthConsumer(consumerKey, consumerSecret);
		OAuthProvider provider = new DoubanOAuthProvider();
		String callbackUrl = "http://www.example.com";
		String res = provider.retrieveRequestToken(consumer, callbackUrl);
		String regex = "http://www.douban.com/service/auth/authorize\\?oauth_token=[0-9a-f]{32}&oauth_callback=http%3A%2F%2Fwww.example.com";
		if(!res.matches(regex)) {
			System.out.println(res);
			fail();
		}
	}
	
	/**
	 * TODO: how to test access token?
	 */
	@Ignore
	public void testRetrieveAccessToken() {
		//OAuthProvider provider = new DoubanOAuthProvider();
		//provider.retrieveAccessToken(consumer, oauthVerifier);
	}

	@Test
	public void testGet() throws OAuthException, IOException {
		OAuthConsumer consumer = new DoubanOAuthConsumer(consumerKey, consumerSecret);
		consumer.setTokenWithSecret(token, tokenSecret);
		
		String url = "http://api.douban.com/people/%40me";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(consumer.sign(url)).openStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = reader.readLine()) != null) {
			sb.append(line);
		}
		if(!sb.toString().contains("SJo1pHHJGmCx")) {
			System.out.println(sb.toString());
			fail();
		}
	}
	
	@Test
	public void testPost() throws IOException, OAuthException {
		OAuthConsumer consumer = new DoubanOAuthConsumer(consumerKey, consumerSecret);
		consumer.setTokenWithSecret(token, tokenSecret);
		
		String say = "<?xml version='1.0' encoding='UTF-8'?>" +
				"<entry xmlns:ns0=\"http://www.w3.org/2005/Atom\" xmlns:db=\"http://www.douban.com/xmlns/\">" +
				"<content>test from jxphone-douban</content>" +
				"</entry>";
		String url = "http://api.douban.com/miniblog/saying";
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod("POST");
		connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Length", Integer.toString(say.getBytes("UTF-8").length));
        connection.setRequestProperty("Content-Type", "application/atom+xml");
        
        
        consumer.sign(connection);
        connection.setDoOutput(true);
        connection.getOutputStream().write(say.getBytes("UTF-8"));
        connection.connect();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String line;
        StringBuilder sb = new StringBuilder();
		while((line = reader.readLine()) != null) {
			sb.append(line);
		}
		if(!sb.toString().contains("SJo1pHHJGmCx")) {
			System.out.println(sb.toString());
			fail();
		}
	}
	
}
