--------------------------------------------------------------------------------
                                  __       .__  .__ 
                                 |__| ____ |  | |__|
                                 |  |/ ___\|  | |  |
                                 |  \  \___|  |_|  |
                             /\__|  |\___  >____/__|
                             \______|    \/         

---------------------------------------------------------------------------------
                    A Java Rest Client for Couchbase Server
---------------------------------------------------------------------------------

# Building

  ant

A jar named jcli.jar will be created in the build directory.

# Documentation

  ant doc

Check out docs/javadocs/index.html

# Using

Just create a CouchbaseClient and call some of it's functions.

# Example

If I have just spun up 3 nodes (10.2.1.1, 10.2.1.2, and 10.2.1.3)
and I want to cluster them together and create the default bucket.

    String username = "Administrator";
    String password = "password";
    CouchbaseClient client = new CouchbaseClient("10.2.1.1", username, password);
    client.configureDataPath("/var/opt/membase/1.6.5/data/ns_1");
    client.configureClusterSize(1024);
    client.setCredentials(username, password);
    client.serverAdd("10.2.1.2", 8091, username, password);
    client.serverAdd("10.2.1.3", 8091, username, password);
    client.rebalance(null);
    while(client.isRebalancing());
    client.createMembaseBucket("bucket", 128, Auth.NONE, 1, 11212, null);