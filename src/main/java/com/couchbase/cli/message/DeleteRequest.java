package com.couchbase.cli.message;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Creates a Delete message that can be sent to a Couchbase server.
 */
public class DeleteRequest implements Request {
	private HttpDelete request;
	private String auth;
	
	/**
	 * Creates a delete message that doesn't use base64 authentication
	 * @param host The server to send data to.
	 * @param url The url of REST call.
	 */
	public DeleteRequest(String host, String url) {
		request = new HttpDelete("http://" + host + ":8091" + url);
		request.addHeader("Accept", "application/json");
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
	}
	
	/**
	 * Creates a delete message with base64 authentication.
	 * @param host The server to send data to.
	 * @param url The url of REST call.
	 * @param username The user name for base64 authentication.
	 * @param password The password for base64 authentication.
	 */
	public DeleteRequest(String host, String url, String username, String password) {
		request = new HttpDelete("http://" + host + ":8091" + url);
		auth = new String((byte[])((new Base64()).encode((username + ":" + password).getBytes())));
		while (auth.endsWith("\n") || auth.endsWith("\r"))
			auth = auth.substring(0, auth.length()-1);
		request.addHeader("Authorization", "Basic " + auth);
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
	}
	
	@Override
	public HttpUriRequest getRequest() {
		return request;
	}
}
