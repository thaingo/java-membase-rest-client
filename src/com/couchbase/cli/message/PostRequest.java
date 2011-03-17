package com.couchbase.cli.message;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostRequest implements Request {
	private static final Logger LOG = LoggerFactory.getLogger(PostRequest.class);

	private HttpPost request;
	private String auth;
	private String body = "";
	
	public PostRequest(String host, String url) {
		request = new HttpPost("http://" + host + ":8091" + url);
		request.addHeader("Accept", "application/json");
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
	}
	
	public PostRequest(String host, String url, String username, String password) {
		request = new HttpPost("http://" + host + ":8091" + url);
		auth = new String((byte[])((new Base64()).encode((username + ":" + password).getBytes())));
		while (auth.endsWith("\n") || auth.endsWith("\r"))
			auth = auth.substring(0, auth.length()-1);
		request.addHeader("Authorization", "Basic " + auth);
		request.addHeader("Accept", "application/json");
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
	}
	
	public void addParam(String key, String value) {
		if (body.length() != 0) {
			body = body + "&";
		}
		body = body + key + "=" + value;
	}
	
	public HttpUriRequest getRequest() {
		try {
			request.setEntity(new StringEntity(body));
		} catch (UnsupportedEncodingException e) {
			LOG.error("Couldn't add body to POST message");
		}
		return request;
	}
}
