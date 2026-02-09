package com.lbisoft.core.app.configuration.database;

import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

// Set the data source to reader / writer based on @Transactional(readOnly = true)
// https://blog.pchudzik.com/201911/read-from-replica/
public class ReaderWriterTransactionManager implements PlatformTransactionManager {
  private final PlatformTransactionManager wrapped;

  ReaderWriterTransactionManager(PlatformTransactionManager wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException {
    // LOGGER.info(definition.isReadOnly() ? "read" : "write");
    if (definition != null) {
      ClientDataSourceRouter.setReadonlyDataSource(definition.isReadOnly());
    }
    return wrapped.getTransaction(definition);
  }

  @Override
  public void commit(TransactionStatus status) throws TransactionException {
    wrapped.commit(status);
  }

  @Override
  public void rollback(TransactionStatus status) throws TransactionException {
    wrapped.rollback(status);
  }
}
