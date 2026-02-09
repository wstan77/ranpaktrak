package com.lbisoft.core.app.components.customer.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.Constants;
import com.lbisoft.core.app.components.customer.model.Customer;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.custom.CustomerExtendedMapper;
import com.lbisoft.core.app.db.mapper.generated.CustomerDbMapper;
import com.lbisoft.core.app.db.model.generated.CustomerDb;
import com.lbisoft.core.app.db.model.generated.CustomerDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class CustomerService {

    private final Dao<CustomerDb, Integer> dao;
    private final CustomerDbMapper mapper;
    private final CustomerExtendedMapper extendedMapper;
    private final AbstractProvider<CustomerDb, Customer> provider;

    @Autowired
    public CustomerService(CustomerDbMapper mapper, CustomerExtendedMapper extendedMapper) {
        this.mapper = mapper;
        this.extendedMapper = extendedMapper;
        this.provider = ProviderBuilder.simple(CustomerDb.class, Customer.class);
        dao = Dao.getDao(mapper, CustomerDb::getCustomerId);
    }

    @Transactional(readOnly = true)
    public List<Customer> getUpdatedCustomersByRegion(String regionLk, LocalDateTime lastSyncTimestamp,
            AppUser user) {
        CustomerDbExample ex = new CustomerDbExample();
        var criteria = ex.createCriteria().andRegionLkEqualTo(regionLk);
        if (lastSyncTimestamp != null) {
            criteria.andLastChangeDateGreaterThan(lastSyncTimestamp);
        }
        if (user.isDistributorFlg()) {
            // if no distributor customer ID is set, don't return anything
            if (user.getDistributorCustomerId() == null) {
                return Collections.emptyList();
            }
            // load only customers associated with the distributor
            criteria.andDistributorCustomerIdEqualTo(user.getDistributorCustomerId());
        }
        return provider.mapAll(dao.select(ex));
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(Integer customerId) {
        return provider.map(dao.select(customerId).orElse(null));
    }

    /**
     * Handles inserting or updating a customer based of whether the customerkey
     * exists
     * Customerkey is guaranteed unique via a DB constraint - this is the key field
     * from Ranpak source data
     * Return number of records upserted
     */
    @Transactional
    public int upsertCustomersByCustomerKey(List<CustomerDb> customerDbs) {
        if (customerDbs != null && !customerDbs.isEmpty()) {
            customerDbs.forEach(c -> {
                CustomerDbExample ex = new CustomerDbExample();
                ex.createCriteria().andCustomerKeyEqualTo(c.getCustomerKey());
                Optional<CustomerDb> existingCustomer = dao.selectOne(ex);
                if (!existingCustomer.isPresent()) {
                    dao.insert(c, Constants.SYSTEM_USER);
                } else {
                    c.setCustomerId(existingCustomer.get().getCustomerId());
                    dao.update(c, Constants.SYSTEM_USER);
                }
            });
            return customerDbs.size();
        }
        return 0;
    }

    public Integer getCustomerIdByCustomerKey(String customerKey) {
        if (customerKey == null) {
            return null;
        }
        CustomerDbExample ex = new CustomerDbExample();
        ex.createCriteria().andCustomerKeyEqualTo(customerKey);
        return dao.selectOne(ex).map(CustomerDb::getCustomerId).orElse(null);
    }

}
