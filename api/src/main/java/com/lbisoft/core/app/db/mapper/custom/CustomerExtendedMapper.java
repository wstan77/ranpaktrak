package com.lbisoft.core.app.db.mapper.custom;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.lbisoft.core.app.db.model.generated.CustomerDb;

@Mapper
public interface CustomerExtendedMapper {

  List<CustomerDb> getUpdatedCustomersByRegion(@Param("regionLk") String regionLk,
      @Param("lastSyncTimestamp") LocalDateTime lastSyncTimestamp);

}
