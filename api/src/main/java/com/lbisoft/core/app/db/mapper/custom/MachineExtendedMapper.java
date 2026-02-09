package com.lbisoft.core.app.db.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.lbisoft.core.app.db.model.generated.MachineDb;

@Mapper
public interface MachineExtendedMapper {

    MachineDb getMachineBySerialNumber(@Param("sn") String sn);

    List<MachineDb> getUnscannedMachines(@Param("customerId") Integer customerId, @Param("batchUUID") String batchUUID);

}