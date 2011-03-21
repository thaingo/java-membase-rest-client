package com.couchbase.cli.message;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Creates a Get message that can be sent to a Couchbase server.
 */
public class GetRequest implements Request {
	private HttpGet request;
	private String auth;
	
	/**
	 * Creates a get message with base64 authentication.
	 * @param host The server to send data to.
	 * @param url The url of REST call.
	 * @param username The user name for base64 authentication.
	 * @param password The password for base64 authentication.
	 */
	public GetRequest(String host, String url, String username, String password) {
		request = new HttpGet("http://" + host + ":8091" + url);
		auth = new String((byte[])((new Base64()).encode((username + ":" + password).getBytes())));
		while (auth.endsWith("\n") || auth.endsWith("\r"))
			auth = auth.substring(0, auth.length()-1);
		request.addHeader("Authorization", "Basic " + auth);
		request.addHeader("Accept", "application/json");
	}
	
	@Override
	public HttpUriRequest getRequest() {
		return request;
	}
}
