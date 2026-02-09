package com.lbisoft.core.app.configuration.database;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

// route to either reader or writer data source by calling setReadonlyDataSource(isReadOnly)
public class ClientDataSourceRouter extends AbstractRoutingDataSource {

  // https://www.baeldung.com/java-threadlocal
  // It's critically important to use ThreadLocal here so that the context
  // is bound to the currently executing thread.
  private final static ThreadLocal<ClientDatabase> CURRENT_DATA_SOURCE = new ThreadLocal<>();

  ClientDataSourceRouter(DataSource readerDb, DataSource writerDb) {
    Map<Object, Object> dataSources = new HashMap<>();
    dataSources.put(ClientDatabase.READER, readerDb);
    dataSources.put(ClientDatabase.WRITER, writerDb);

    super.setTargetDataSources(dataSources);
    super.setDefaultTargetDataSource(writerDb);
  }

  public static void setReadonlyDataSource(boolean isReadonly) {
    CURRENT_DATA_SOURCE.set(isReadonly ? ClientDatabase.READER : ClientDatabase.WRITER);
  }

  @Override
  protected Object determineCurrentLookupKey() {
    return CURRENT_DATA_SOURCE.get();
  }

  public enum ClientDatabase {
    READER, WRITER
  }
}
