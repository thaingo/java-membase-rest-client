package com.couchbase.cli.message;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;

public class DeleteRequest implements Request {
	private HttpDelete request;
	private String auth;
	
	public DeleteRequest(String host, String url) {
		request = new HttpDelete("http://" + host + ":8091" + url);
		request.addHeader("Accept", "application/json");
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
	}
	
	public DeleteRequest(String host, String url, String username, String password) {
		request = new HttpDelete("http://" + host + ":8091" + url);
		auth = new String((byte[])((new Base64()).encode((username + ":" + password).getBytes())));
		while (auth.endsWith("\n") || auth.endsWith("\r"))
			auth = auth.substring(0, auth.length()-1);
		request.addHeader("Authorization", "Basic " + auth);
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
	}
	
	public HttpUriRequest getRequest() {
		return request;
	}
}
