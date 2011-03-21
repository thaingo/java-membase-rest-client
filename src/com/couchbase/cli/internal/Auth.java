package com.couchbase.cli.internal;

public enum Auth {
	
	/**
	 * Specifies no authentication.
	 */
	NONE("none"),
	
	/**
	 * Specifies sasl authentication.
	 */
	SASL("sasl");
	
	/**
	 * Holds the authentication type.
	 */
	public String auth;
	
	/**
	 * Creates an Auth instance.
	 * @param auth
	 */
	Auth(String auth) {
		this.auth = auth;
	}
}
