cd ~/tools/zookeeper-3.6.3/
./bin/zkServer.sh start

cd ~/tools/hbase-2.4.2/
./bin/start-hbase.sh

cd ~/tools/redis-6.2.6
./src/redis-server ./redis.conf

cd ~/tools/kafka_2.12-2.8.0/
./bin/kafka-server-start.sh config/server.properties

