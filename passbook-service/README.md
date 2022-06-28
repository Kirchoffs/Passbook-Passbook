# Note

## Database setup
Start & Stop ZooKeeper
> ./bin/zkServer.sh start  
> ./bin/zkServer.sh stop

Start Zookeeper CLI client
> ./bin/zkCli.sh

Start & Stop Kafka
> ./bin/kafka-server-start.sh config/server.properties

Start & Stop HBase
> ./bin/start-hbase.sh  
> ./bin/stop-hbase.sh

Start Redis
> ./src/redis-server ./redis.conf

Operation on HBase
> ./bin/hbase shell  
> create_namespace 'pb'  
> list_namespace_tables 'pb'

Operation on Redis
> ./src/redis-cli  
> keys *
> smembers 'key-id'
> quit

## Config
### Config for HBase
#### hbase-site.xml  
• hbase.rootdir  
The directory shared by region servers and into which HBase persists. 
The URL should be 'fully-qualified' to include the filesystem scheme. 
For example, to specify the HDFS directory '/hbase' 
where the HDFS instance's namenode is running at namenode.example.org on port 9000, 
set this value to: hdfs://namenode.example.org:9000/hbase. 
By default, we write to whatever ${hbase.tmp.dir} is set too -- usually /tmp -- 
so change this configuration or else all data will be lost on machine restart.

• hbase.zookeeper.property.dataDir  
Property from ZooKeeper's config zoo.cfg. 
The directory where the snapshot is stored.

```
<property>
  <name>hbase.rootdir</name>
  <value>hdfs://localhost:8020/hbase</value>
</property>
```
## Issue
### How to design row key for HBase?
```
inverse(userId) + (Long.MAX_VALUE - currentTime)
```
## Knowledge
• HttpServletRequest  
HTTP request will be encapsulated to HttpServletRequest by the container.

• HBase CLI  
truncate 'pb:user' is equal to 3 commands:  
disable 'pb:user'  
drop 'pb:user'  
create 'pb:user'
