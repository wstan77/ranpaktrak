package com.lbisoft.core.app.configuration.database;

import java.io.Closeable;
import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.lbisoft.core.app.mybatis.StringArrayTypeHandler;
import com.lbisoft.core.app.settings.DatabaseSettings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@MapperScan("com.lbisoft.core.app.db.mapper")
public class SqlConfiguration implements Closeable {
  private HikariDataSource writer;
  private HikariDataSource reader;
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  @Bean
  public DataSource primaryDataSource(DatabaseSettings settings) {
    HikariConfig writerConfig = new HikariConfig();

    LOGGER.info(
        "\n========================"
            + "\nConnecting to: "
            + settings.getWriterUrl()
            + "\n========================");

    writerConfig.setDriverClassName(settings.getDriverClassName());
    writerConfig.setJdbcUrl(settings.getWriterUrl());
    writerConfig.setUsername(settings.getUsername());
    writerConfig.setPassword(settings.getPassword());
    writerConfig.setPoolName("writer");
    writerConfig.setReadOnly(false);
    this.writer = new HikariDataSource(writerConfig);

    HikariConfig readerConfig = new HikariConfig();
    LOGGER.info(
        "\n========================"
            + "\nConnecting to: "
            + settings.getReaderUrl()
            + "\n========================");
    readerConfig.setDriverClassName(settings.getDriverClassName());
    readerConfig.setJdbcUrl(settings.getReaderUrl());
    readerConfig.setUsername(settings.getUsername());
    readerConfig.setPassword(settings.getPassword());
    readerConfig.setPoolName("reader");
    readerConfig.setReadOnly(true);
    this.reader = new HikariDataSource(readerConfig);

    return new ClientDataSourceRouter(reader, writer);
  }

  @Bean
  public SqlSessionFactory sqlSessionFactory(DataSource dataSource, ApplicationContext ctx) throws Exception {
    SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    Resource[] locations = ctx.getResources("classpath*:com/lbisoft/core/app/db/mapper/**/*.xml");
    sessionFactory.setMapperLocations(locations);
    sessionFactory.setTypeHandlers(new StringArrayTypeHandler());
    return sessionFactory.getObject();
  }

  @Bean
  public PlatformTransactionManager transactionManager(DataSource dataSource) {
    return new ReaderWriterTransactionManager(new JdbcTransactionManager(dataSource));
  }

  @Override
  public void close() throws IOException {
    writer.close();
    reader.close();
  }
}
