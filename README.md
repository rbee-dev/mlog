# mlog
Provides Monitoring Framework Log for Storing Monitoring Data from Kieker Framework

# CassandraToElastic

## About
CassandraToElastic is a simple Commandline Tool to transfer data from a Cassandra database into a Elasticsearch instance. It uses for Cassandra the Datastax Java Driver and for Elasticsearch the standard JAVA API. The data is transfered via BulkInserts.

## Usage

```
usage: CassandraToElastic
 -c,--cassandra <arg>      IPs of the Cassandra cluster
 -d,--debug                Show Debug messages
 -e,--elastic <arg>        IP of the Elastic cluster
 -h,--help                 Print this help text
 -k,--keyspace <arg>       Keyspace of the Cassandra cluster
 -p,--elasticiport <arg>   Port of the Elastic cluster
 -t,--table <arg>          Table to export to Elastic
```

## Further Information
This tool was created for http://rbee.io and is part of a paper for the Symposium on Softwareperformace. Under the following link you can find some test result: https://zenodo.org/record/61227#.WBhyACTNzNF
