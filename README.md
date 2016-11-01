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
 -c,--cassandra <arg>      IPs of the Cassandra cluster
 -d,--debug                Show Debug messages
 -e,--elastic <arg>        IP of the Elastic cluster
 -h,--help                 Print this help text
 -k,--keyspace <arg>       Keyspace of the Cassandra cluster
 -p,--elasticport <arg>   Port of the Elastic cluster
 -t,--table <arg>          Table to export to Elastic
```

!Important
Due to the update to Elasticsearch 5 you have to start the jar like this:
java -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
-Dlog4j2.disable.jmx=true -jar rbee_cte.jar %CMDAGRGS%

### Further Information
If you are interested in test data please use the following link: https://zenodo.org/record/61227#.V_zkRSQbPNE

