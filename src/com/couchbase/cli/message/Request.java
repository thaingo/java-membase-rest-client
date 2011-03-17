package com.couchbase.cli.message;

import org.apache.http.client.methods.HttpUriRequest;

public interface Request {
	public HttpUriRequest getRequest();
}
