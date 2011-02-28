package com.couchbase.cli;

public class CouchbaseClient {
	private String username;
	private String password;
	
	public CouchbaseClient(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String listServers() {
		
		return "Server List";
	}
	
	public String serverInfo() {
		
		return "Server Info";
	}
	
	public boolean serverAdd() {
		
		return true;
	}
	
	public boolean serverReadd() {
		
		return true;
	}
	
	public void rebalance() { 
		
	}
	
	public boolean rebalanceStop() {
		
		return true;
	}
	
	public String rebalanceStatus() {
		
		return "Status";
	}
	
	public void failover() {
		
	}
	
	public void clusterInit() {
		
	}
	
	public void nodeInit() {
		
	}
	
	public void bucket_list() {
		
	}
	
	public void bucketCreate() {
		
	}
	
	public void bucketEdit() {
		
	}
	
	public String bucketDelete() {
		return "Not Implemented";
	}
	
	public void bucketFlush() {
		
	}
}
