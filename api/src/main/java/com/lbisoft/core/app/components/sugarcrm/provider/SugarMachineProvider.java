package com.lbisoft.core.app.components.sugarcrm.provider;

import org.springframework.stereotype.Component;

import com.lbisoft.core.app.components.sugarcrm.model.SugarMachine;
import com.lbisoft.core.app.db.model.generated.MachineDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

@Component
public class SugarMachineProvider extends AbstractProvider<MachineDb, SugarMachine> {

    @Override
    protected void defineClassMap(ClassMapBuilder<MachineDb, SugarMachine> classMapBuilder) {
        classMapBuilder
                .field("serialNumber", "serial_number")
                .field("scanTmstp", "lastVerificationDateAsTimestamp")
                .field("legacySerialNumber", "legacy_serial_number")
                .field("regionLk", "account_region");
    }

    @Override
    protected void defineChildren(ChildrenMap<SugarMachine> childrenMap) {
        // TODO Auto-generated method stub
    }

}
