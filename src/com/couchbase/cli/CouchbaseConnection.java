package com.couchbase.cli;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.couchbase.cli.message.Request;

public class CouchbaseConnection {
	HttpClient client;
	
	public CouchbaseConnection() {
		client = new DefaultHttpClient();
	}
	
	public CouchbaseResponse sendRequest(Request msg) {
		HttpResponse response = null;
		try {
			response = client.execute(msg.getRequest());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new CouchbaseResponse(response);
	}
}

