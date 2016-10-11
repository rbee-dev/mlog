# mlog
Provides Monitoring Framework Log for Storing Monitoring Data from Kieker Framework

# CassandraToElastic (rbee_cte)
### About
The current release is a simple command-line based tool to quickly transfer data from Cassandra to Elasticsearch.
In a future update this will be transfered into a REST based microservice.
The Cassandra implementation is based on the Datastax Cassandra Driver and for Elasticsearch we used the standard Java API.

### Build
Simply use maven package. With this you build a runnable fat jar.

### How to Use
```sh
 -c,--cassandra <arg>   IPs of the Cassandra cluster
 -d,--debug             Show Debug messages
 -e,--elastic <arg>     IP of the Elastic cluster
 -h,--help              Print this help text
 -k,--keyspace <arg>    Keyspace of the Cassandra cluster
 -t,--table <arg>       Table to export to Elastic
```
Currently only data transfer via port 9300 to Elasticsearch is supported. If you have any custom ports, please fork this respository or make a pull request.
