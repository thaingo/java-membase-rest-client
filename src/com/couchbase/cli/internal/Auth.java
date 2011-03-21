package com.couchbase.cli.internal;

public enum Auth {
	NONE("none"),
	SASL("sasl");
	
	public String auth;
	
	Auth(String auth) {
		this.auth = auth;
	}
}
