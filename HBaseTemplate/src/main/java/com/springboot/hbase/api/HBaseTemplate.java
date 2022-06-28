package com.springboot.hbase.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HBaseTemplate implements HBaseOperations {
    private Configuration configuration;
    private volatile Connection connection;

    public HBaseTemplate(Configuration configuration) {
        this.configuration = configuration;
        Assert.notNull(configuration, "A valid configuration is required");
    }

    @Override
    public <T> T execute(String tableName, TableCallback<T> action) {
        Assert.notNull(tableName, "Table is required");
        Assert.notNull(action, "Callback object is required");

        StopWatch sw = new StopWatch();
        sw.start();
        Table table = null;

        try {
            table = this.getConnection().getTable(TableName.valueOf(tableName));
            return action.doInTable(table);
        } catch (Throwable throwable) {
            throw new HBaseSystemException(throwable);
        } finally {
            if (null != table) {
                try {
                    table.close();
                    sw.stop();
                } catch (IOException exception) {
                    log.error("Failed to release HBase table resource");
                }
            }
        }
    }

    @Override
    public <T> List<T> find(String tableName, String family, RowMapper<T> mapper) {
        Scan scan = new Scan();
        scan.setCaching(5000);
        scan.addFamily(Bytes.toBytes(family));
        return this.find(tableName, scan, mapper);
    }

    @Override
    public <T> List<T> find(String tableName, String family, String qualifier, RowMapper<T> mapper) {
        Scan scan = new Scan();
        scan.setCaching(5000);
        scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        return this.find(tableName, scan, mapper);
    }

    @Override
    public <T> List<T> find(String tableName, Scan scan, RowMapper<T> mapper) {
        return this.execute(tableName, new TableCallback<List<T>>() {
            @Override
            public List<T> doInTable(Table table) throws Throwable {
                int caching = scan.getCaching();
                if (caching == 1) {
                    scan.setCaching(5000);
                }

                try (ResultScanner scanner = table.getScanner(scan)) {
                    List<T> rs = new ArrayList<>();
                    int rowNum = 0;
                    for (Result result: scanner) {
                        rs.add(mapper.mapRow(result, rowNum++));
                    }

                    return rs;
                }
            }
        });
    }

    @Override
    public <T> T get(String tableName, String rowName, RowMapper<T> mapper) {
        return this.get(tableName, rowName, null, null, mapper);
    }

    @Override
    public <T> T get(String tableName, String rowName, String familyName, RowMapper<T> mapper) {
        return this.get(tableName, rowName, familyName, null, mapper);
    }

    @Override
    public <T> T get(String tableName, String rowName, String familyName, String qualifier, RowMapper<T> mapper) {
        return this.execute(tableName, new TableCallback<T>() {
            @Override
            public T doInTable(Table table) throws Throwable {
                Get get = new Get(Bytes.toBytes(rowName));
                if (StringUtils.isNotBlank(familyName)) {
                    if (StringUtils.isNotBlank(qualifier)) {
                        get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(qualifier));
                    } else {
                        get.addFamily(Bytes.toBytes(familyName));
                    }
                }

                Result result = table.get(get);
                return mapper.mapRow(result, 0);
            }
        });
    }

    @Override
    public void execute(String tableName, MutatorCallback action) {
        Assert.notNull(tableName, "Table is required");
        Assert.notNull(action, "Callback object is required");

        StopWatch sw = new StopWatch();
        sw.start();

        BufferedMutator mutator = null;
        try {
            BufferedMutatorParams mutatorParams = new BufferedMutatorParams(
                TableName.valueOf(tableName)
            );

            mutator = this.getConnection().getBufferedMutator(
                mutatorParams.writeBufferSize(3 * 1024 * 1024)
            );

            action.doInMutator(mutator);
        } catch (Throwable throwable) {
            sw.stop();
            throw new HBaseSystemException(throwable);
        } finally {
            if (mutator != null) {
                try {
                    mutator.flush();
                    mutator.close();
                    sw.stop();
                } catch (IOException exception) {
                    log.error("Failed to release HBase table resource");
                }
            }
        }
    }

    @Override
    public void saveOrUpdate(String tableName, Mutation mutation) {
        this.execute(tableName, new MutatorCallback() {
            @Override
            public void doInMutator(BufferedMutator mutator) throws Throwable {
                mutator.mutate(mutation);
            }
        });
    }

    @Override
    public void saveOrUpdate(String tableName, List<Mutation> mutations) {
        this.execute(tableName, new MutatorCallback() {
            @Override
            public void doInMutator(BufferedMutator mutator) throws Throwable {
                mutator.mutate(mutations);
            }
        });
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        if (connection == null) {
            synchronized (this) {
                if (connection == null) {
                    try {
                        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                            200,
                            Integer.MAX_VALUE,
                            60L,
                            TimeUnit.SECONDS,
                            new SynchronousQueue<>()
                        );

                        poolExecutor.prestartCoreThread();
                        connection = ConnectionFactory.createConnection(
                            configuration,
                            poolExecutor
                        );
                    } catch (IOException exception) {
                        log.error("Failed to create HBase connection pool");
                    }
                }
            }
        }
        return connection;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
