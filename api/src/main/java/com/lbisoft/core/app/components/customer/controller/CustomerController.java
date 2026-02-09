package com.lbisoft.core.app.components.customer.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.customer.model.Customer;
import com.lbisoft.core.app.components.customer.service.CustomerService;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/customers/")
@Tag(name = "Customers")
public class CustomerController {

    private static final String CUSTOMER = "1 - Customers";

    @Autowired
    CustomerService service;

    @Autowired
    private UserActivitiesService activitiesService;

    @GetMapping("/{regionLk}/")
    @Operation(summary = "Get Updated Customers for the Given Region", tags = { CUSTOMER })
    @PreAuthorize("hasAuthority('CAN_READ_CUSTOMERS')")
    public List<Customer> getUpdatedCustomersByRegion(@PathVariable String regionLk,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastSyncTimestamp,
            HttpServletRequest request) {
        activitiesService.logActivity(
                new UserActivityBuilder(ActivityTypes.SYNC_CUSTOMERS).relatedString(regionLk)
                        .relatedDate(lastSyncTimestamp).build(),
                request);
        AppUser currentUser = RequestHelper.getUser(request);
        return service.getUpdatedCustomersByRegion(regionLk, lastSyncTimestamp, currentUser);
    }

}
