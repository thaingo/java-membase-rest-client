package com.couchbase.cli.message;

import org.apache.http.client.methods.HttpUriRequest;

/**
 * Defiens a Request message.
 */
public interface Request {
	
	/**
	 * Returns the request created by this object.
	 * @return An http request that can be sent to the server.
	 */
	public HttpUriRequest getRequest();
}
