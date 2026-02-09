package com.lbisoft.core.app.components.sugarcrm.provider;

import org.springframework.stereotype.Component;

import com.lbisoft.core.app.components.sugarcrm.model.SugarCustomer;
import com.lbisoft.core.app.db.model.generated.CustomerDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

@Component
public class SugarCustomerProvider extends AbstractProvider<CustomerDb, SugarCustomer> {

    @Override
    protected void defineClassMap(ClassMapBuilder<CustomerDb, SugarCustomer> classMapBuilder) {
        classMapBuilder
                .field("customerKey", "account_number")
                .field("accountName", "name")
                .field("city", "city")
                .field("state", "state")
                .field("country", "country")
                .field("district", "district")
                .field("regionLk", "region");
    }

    @Override
    protected void defineChildren(ChildrenMap<SugarCustomer> childrenMap) {
        // TODO Auto-generated method stub
    }

}
