package com.lbisoft.core.app.components.machine.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.Constants;
import com.lbisoft.core.app.components.machine.model.Machine;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.custom.MachineExtendedMapper;
import com.lbisoft.core.app.db.mapper.generated.MachineDbMapper;
import com.lbisoft.core.app.db.model.generated.MachineDb;
import com.lbisoft.core.app.db.model.generated.MachineDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class MachineService {

    private final Dao<MachineDb, Integer> dao;
    private final MachineDbMapper mapper;
    private final AbstractProvider<MachineDb, Machine> provider;
    private final MachineExtendedMapper extendedMapper;

    @Autowired
    public MachineService(MachineDbMapper mapper, MachineExtendedMapper extendedMapper) {
        this.mapper = mapper;
        this.provider = ProviderBuilder.simple(MachineDb.class, Machine.class);
        dao = Dao.getDao(mapper, MachineDb::getMachineId);
        this.extendedMapper = extendedMapper;
    }

    @Transactional(readOnly = true)
    public List<Machine> getUpdatedMachinesByRegion(String regionLk, LocalDateTime lastSyncTimestamp,
            AppUser user) {
        MachineDbExample ex = new MachineDbExample();
        var criteria = ex.createCriteria().andRegionLkEqualTo(regionLk);
        if (lastSyncTimestamp != null) {
            criteria.andLastChangeDateGreaterThan(lastSyncTimestamp);
        }
        if (user.isDistributorFlg()) {
            // if no distributor customer ID is set, don't return anything
            if (user.getDistributorCustomerId() == null) {
                return Collections.emptyList();
            }
            // load only machines associated with the distributor
            criteria.andDistributorCustomerIdEqualTo(user.getDistributorCustomerId());
        }
        return provider.mapAll(dao.select(ex));
    }

    /**
     * Handles inserting or updating a machine based of whether the serial number
     * exists
     * Serial number is guaranteed unique via a DB constraint - this is the key
     * field
     * from Ranpak source data
     * Return number of records upserted
     */
    @Transactional
    public int upsertMachinesBySerialNumber(List<MachineDb> machineDbs) {
        if (machineDbs != null && !machineDbs.isEmpty()) {
            machineDbs.forEach(m -> {
                MachineDbExample ex = new MachineDbExample();
                ex.createCriteria().andSerialNumberEqualTo(m.getSerialNumber());
                Optional<MachineDb> existingMachine = dao.selectOne(ex);
                if (!existingMachine.isPresent()) {
                    dao.insert(m, Constants.SYSTEM_USER);
                } else {
                    m.setMachineId(existingMachine.get().getMachineId());
                    dao.update(m, Constants.SYSTEM_USER);
                }
            });
            return machineDbs.size();
        }
        return 0;
    }

    /**
     * Returns a machine given a serial number or legacy serial number
     */
    public Machine getMachineBySerialNumber(String sn) {
        MachineDb mDb = extendedMapper.getMachineBySerialNumber(sn);
        if (mDb != null) {
            return provider.map(mDb);
        }
        return null;
    }

    /**
     * Update a machine record
     */
    public void updateMachine(Machine m, AppUser user) {
        dao.update(provider.reverseMap(m), user);
    }

    /**
     * Returns a list of all machines for a customer that were not scanned as part
     * of a scan batch
     */
    public List<Machine> getUnscannedMachines(Integer customerId, String batchUUID) {
        return provider.mapAll(extendedMapper.getUnscannedMachines(customerId, batchUUID));
    }

}
