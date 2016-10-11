# mlog
Provides Monitoring Framework Log for Storing Monitoring Data from Kieker Framework

# CassandraToElastic (rbee_cte)
### About
The current release is a simple command-line based tool to quickly transfer data from Cassandra to Elasticsearch.
In a future update this will be transfered into a REST based microservice.
The Cassandra implementation is based on the Datastax Cassandra Driver and for Elasticsearch we used the standard Java API.

### Build
Simply use maven package. With this you build a runnable fat jar.

### Transfer Settings
The Cassandra Driver is configured to request 10.000 rows at a time.
The ElasticSearch API is configured to send a bulk request with 10.000 rows at a time, with three concurrent Connections, totaling a rate of 30.000. This is enough for a local ElasticSearch installation.

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

### Further Information
If you are interested in test data please use the following link: https://zenodo.org/record/61227#.V_zkRSQbPNE
