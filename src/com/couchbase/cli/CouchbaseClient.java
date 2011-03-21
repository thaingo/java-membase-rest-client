package com.couchbase.cli;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.cli.internal.Auth;
import com.couchbase.cli.message.DeleteRequest;
import com.couchbase.cli.message.GetRequest;
import com.couchbase.cli.message.PostRequest;
import com.freebase.json.JSON;

public class CouchbaseClient {
	private static final Logger LOG = LoggerFactory.getLogger(CouchbaseClient.class);
	private CouchbaseConnection conn;
	private String hostname;
	private String username;
	private String password;
	
	public CouchbaseClient(String hostname, String username, String password) {
		conn = new CouchbaseConnection();
		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Gets the default pool for the Membase Rest API
	 * @return Ann http response from the cluster.
	 */
	public String pools() {
		GetRequest message = new GetRequest(hostname, "/pools", username, password);
		CouchbaseResponse response = conn.sendRequest(message);
		return response.getBody();
	}
	
	/**
	 * Describes the cluster in detail in json format.
	 * @return The http response from the cluster.
	 */
	public CouchbaseResponse poolsDetails() {
		GetRequest message = new GetRequest(hostname, "/pools/default", username, password);
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 * Prints out a comma separated list of servers.
	 * @return A comma seperated list of servers.
	 */
	public String listServers() {
		String servers = "";
		CouchbaseResponse response = poolsDetails();
		
		if (response.getReturnCode() != 200)
			return "Error getting server list";
		
		try {
			JSON nodes = JSON.parse(response.getBody()).get("nodes");
			
			int index = 0;
			while (nodes.has(index) || index == 0) {
				if (index == 0)
					servers += nodes.get(index).get("otpNode").value();
				else
					servers += "," + nodes.get(index).get("otpNode").value();
				index++;
			}
		} catch (ParseException e) {
			LOG.error("Error parsing server list");
			return "Error parsing server list";
		}
		return servers;
	}
	
	/**
	 * Gets an http response that contains a json string with the servers info and
	 * configuration.
	 * @return The http response from the cluster.
	 */
	public CouchbaseResponse serverInfo() {
		GetRequest message = new GetRequest(hostname, "/nodes/self", username, password);
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 * Adds a server to an existing cluster. Note that the server used in start the add must be
	 * part of a cluster, or be a cluster of one node. If you want to join a single node to a
	 * cluster from the single node see the serverJoin function.
	 * @param host The hostname or ip address of the node to add to the cluster.
	 * @param port The port of the server to add to the cluster.
	 * @param user The username of the node to add to the cluster.
	 * @param pass The password of the node to add to the cluster.
	 * @return The http response from the cluster.
	 */
	public CouchbaseResponse serverAdd(String host, int port, String user, String pass) {
		PostRequest message = new PostRequest(hostname, "/controller/addNode", username, password);
		message.addParam("password", pass);
		message.addParam("hostname", (host + ":" + port));
		message.addParam("user", user);
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 *  Adds a server to an existing cluster. Note that this rest call must be sent to a single server
	 *  cluster that wants to be joined to a multi-server cluster. Sending a join to a server in a
	 *  multi-server cluster will fail.
	 * @param host The hostname of a server in the multi server cluster
	 * @param port The port number of the admin REST interface for the host
	 * @param user The username for the host
	 * @param pass The password for the host
	 * @return The http response from the cluster.
	 */
	public CouchbaseResponse serverJoin(String host, int port, String user, String pass) {
		PostRequest message = new PostRequest(hostname, "/node/controller/doJoinCluster", username, password);
		message.addParam("clusterMemberHostIp", host);
		message.addParam("clusterMemberPort", port + "");
		message.addParam("user", user);
		message.addParam("password", pass);
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 * Re-adds a server back to the cluster that was previously failed over.
	 * @param host The hostname of the server.
	 * @return A http response from the cluster.
	 */
	public CouchbaseResponse serverReadd(String host) {
		PostRequest message = new PostRequest(hostname, "/controller/reAddNode", username, password);
		message.addParam("otpNode", ("ns_1@" + host));
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 * Rebalances the cluster. This function will automatically detect the nodes to be rebalanced into
	 * the cluster, but will not detect nodes that need to be removed. These must be specified with the
	 * ejectedNodes parameter.
	 * @param ejectNodes A comma seperated list of nodes to be ejected. Each node must be prefixed with
	 * "ns_1@" in order for the rebalance to function correctly.
	 * @return An http response from the server.
	 */
	public CouchbaseResponse rebalance(String ejectNodes) { 
		PostRequest message = new PostRequest(hostname, "/controller/rebalance", username, password);
		message.addParam("knownNodes", listServers());
		if (ejectNodes == null) {
			message.addParam("ejectedNodes", "");
		} else {
			message.addParam("ejectedNodes", ejectNodes);
		}
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 * Stops rebalance.
	 * @return An http response from the cluster.
	 */
	public CouchbaseResponse rebalanceStop() {
		PostRequest message = new PostRequest(hostname, "/controller/stopRebalance", username, password);
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 * Checks to see whether the cluster is currently rebalancing nodes
	 * @return true when the cluster is rebalancing, false otherwise.
	 */
	public boolean isRebalancing() {
		GetRequest message = new GetRequest(hostname, "/pools/default/rebalanceProgress", username, password);
		CouchbaseResponse response = conn.sendRequest(message);
			
		try {
			JSON nodes = JSON.parse(response.getBody());
			if (((String) nodes.get("status").value()).equals("running"))
					return true;
		} catch (ParseException e) {
			LOG.error("Error getting rebalance status");
		}
		return false;
	}
	
	/**
	 * Fails over a node in the cluster.
	 * @param host The node to fail over.
	 * @return An http response from the cluster.
	 */
	public CouchbaseResponse failover(String host) {
		PostRequest message = new PostRequest(hostname, "/controller/failOver", username, password);
		message.addParam("otpNode", ("ns_1@" + host));
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 * Configures the data path for a node.
	 * @param path The path of the data directory.
	 * @return The http response from the cluster.
	 */
	public CouchbaseResponse configureDataPath(String path) {
		PostRequest message = new PostRequest(hostname, "/nodes/self/controller/settings", username, password);
		message.addParam("path", path);
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 * Configures the memory quota for the cluster.
	 * @param memoryMB The memory quota size in megabytes.
	 * @return The http response from the cluster.
	 */
	public CouchbaseResponse configureClusterSize(int memoryMB) {
		PostRequest message = new PostRequest(hostname, "/pools/default", username, password);
		message.addParam("memoryQuota", (memoryMB + ""));
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 * Sets or changes the administrator credential's of the server.
	 * @param newusername The new username.
	 * @param newpassword The new password.
	 * @return The http response from the cluster.
	 */
	public CouchbaseResponse setCredentials(String newusername, String newpassword) {
		PostRequest message = new PostRequest(hostname, "/settings/web", username, password);
		message.addParam("port", "SAME");
		message.addParam("username", newusername);
		message.addParam("password", newpassword);
		CouchbaseResponse response = conn.sendRequest(message);
		if (response.getReturnCode() == 200) {
			username = newusername;
			password = newpassword;
		}
		return response;
	}
	
	/**
	 * Gets a list of all buckets in the cluster as well as bucket specific information.
	 * @return An http response from the cluster.
	 */
	public CouchbaseResponse listBuckets() {
		GetRequest message = new GetRequest(hostname, "/pools/default/buckets", username, password);
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 * Gets bucket specific information for a given bucket.
	 * @param bucket The bucket to get information for.
	 * @return An http response from the cluster.
	 */
	public CouchbaseResponse getBucketInfo(String bucket) {
		GetRequest message = new GetRequest(hostname, ("/pools/default/buckets/" + bucket), username, password);
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 * Creates a new Membase bucket.
	 * @param name The name of the bucket.
	 * @param memorySizeMB The size of the bucket in megabytes.
	 * @param authType "none" for no authentication, "sasl" for authentication.
	 * @param replicas The number of replicas.
	 * @param port The port to put the bucket on.
	 * @param password The password for the bucket.
	 * @return An http response from the cluster.
	 */
	public CouchbaseResponse createMembaseBucket(String name, int memorySizeMB, Auth authType, int replicas, int port, String password) {
		PostRequest message = new PostRequest(hostname, "/pools/default/buckets", username, password);
		message.addParam("name", name);
		message.addParam("ramQuotaMB", (memorySizeMB + ""));
		message.addParam("authType", authType.auth);
		message.addParam("replicaNumber", (replicas + ""));
		message.addParam("proxyPort", (port + ""));
		if (authType.equals("sasl"))
			message.addParam("saslpassword", password);
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 * Creates a Memcached bucket.
	 * @param name The name of the bucket.
	 * @param memorySizeMB The size of the bucket in megabytes.
	 * @param authType "none" for no authentication, "sasl" for authentication.
	 * @param port The port number for the server to listen on.
	 * @param password The password for the bucket.
	 * @return An http response from the server.
	 */
	public CouchbaseResponse createMemcachedBucket(String name, int memorySizeMB, Auth authType, int port, String password) {
		PostRequest message = new PostRequest(hostname, "/pools/default/buckets", username, password);
		message.addParam("name", name);
		message.addParam("ramQuotaMB", (memorySizeMB + ""));
		message.addParam("authType", authType.auth);
		message.addParam("proxyPort", (port + ""));
		message.addParam("bucketType", "memcached");
		if (authType.equals("sasl"))
			message.addParam("saslpassword", password);
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	public void bucketEdit() {
		
	}
	
	/**
	 * Deletes a bucket.
	 * @param bucketName The name of the bucket to delete.
	 * @return The http response from the cluster.
	 */
	public CouchbaseResponse bucketDelete(String bucketName) {
		DeleteRequest message = new DeleteRequest(hostname, "/pools/default/buckets/" + bucketName, username, password);
		CouchbaseResponse response = conn.sendRequest(message);
		return response;
	}
	
	/**
	 * Currently not implemented in Membase server.
	 * @return An error message.
	 */
	public String bucketFlush() {
		return "Not implemented by Couchbase REST API";
	}
	
	public static void main(String args[]) {
		String username = "Administrator";
		String password = "password";
		CouchbaseClient client = new CouchbaseClient("10.2.1.16", username, password);
		
		//System.out.println(client.configureDataPath("/var/opt/membase/1.6.5/data/ns_1").getBody());
		//System.out.println(client.configureClusterSize(1024).getBody());
		//System.out.println(client.setCredentials(username, password).getBody());
		//System.out.println(client.listServers());
		//System.out.println(client.createMembaseBucket("bucket3", 128, Auth.NONE, 1, 11214, "password").getBody());
		//System.out.println(client.createMemcachedBucket("bucket4", 128, Auth.NONE, 11221, "password").getBody());
		
		//CouchbaseResponse r = client.getBucketInfo("bucket3");
		//System.out.println(r.getReturnCode());
		//System.out.println(r.getBody());
		
		//System.out.println(client.bucketDelete("bucket").getReturnCode());
		/*System.out.println(client.failover("10.2.1.54").getBody());
		System.out.println(client.serverReadd("10.2.1.54").getBody());
		System.out.println(client.rebalance(null).getBody());
		System.out.println(client.rebalance(null).getBody());
		
		while (client.isRebalancing()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Rebalancing");
		}*/
		//System.out.println(client.pools());
		//System.out.println("\n\n" + client.poolsDetails());
		//System.out.println("\n\n" + client.bucket_list());
		//System.out.println("\n\n" + client.getBucketInfo("default"));
	}
}
