package com.lbisoft.core.app.components.sugarcrm.provider;

import org.springframework.stereotype.Component;

import com.lbisoft.core.app.components.scan.model.Scan;
import com.lbisoft.core.app.components.sugarcrm.model.SugarScan;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

@Component
public class SugarScanProvider extends AbstractProvider<Scan, SugarScan> {

    @Override
    protected void defineClassMap(ClassMapBuilder<Scan, SugarScan> classMapBuilder) {
        classMapBuilder
                .field("customer.customerKey", "account_number")
                .field("machine.serialNumber", "serial_number")
                .field("scanDateForSugar", "last_verification_date")
                .field("tagId", "tag_id");
    }

    @Override
    protected void defineChildren(ChildrenMap<SugarScan> childrenMap) {
    }

}
