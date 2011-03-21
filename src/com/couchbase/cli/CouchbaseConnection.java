package com.couchbase.cli;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.cli.message.Request;

/**
 * Creates an http connection to a Couchbase server.
 */
public class CouchbaseConnection {
	private static final Logger LOG = LoggerFactory.getLogger(CouchbaseConnection.class);
	private HttpClient client;
	
	/**
	 * Creates a connection to a Couchbase server.
	 */
	public CouchbaseConnection() {
		client = new DefaultHttpClient();
	}
	
	/**
	 * Sends a REST request to the Couchbase server.
	 * @param msg The REST message.
	 * @return The response from the server.
	 */
	public CouchbaseResponse sendRequest(Request msg) {
		HttpResponse response = null;
		try {
			response = client.execute(msg.getRequest());
		} catch (ClientProtocolException e) {
			LOG.error("Protocol error while sending request");
		} catch (IOException e) {
			LOG.error("Connection error while sending request");
		}
		return new CouchbaseResponse(response);
	}
}

