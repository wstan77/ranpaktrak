package com.lbisoft.core.app.components.sugarcrm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lbisoft.core.app.components.sugarcrm.service.SugarService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/sugarcrm/")
@Tag(name = "SugarCRM")
public class SugarController {

    private static final String CUSTOMER = "Customer";
    private static final String SUGARCRM = "SugarCRM";

    @Autowired
    SugarService service;

    @GetMapping("/customers/")
    @Operation(summary = "Get Updated Customers from SugarCRM", tags = { CUSTOMER, SUGARCRM })
    @PreAuthorize("hasAuthority('CAN_READ_MANAGE_SYSTEM')")
    public void getUpdatedCustomersFromSugarCRM(HttpServletRequest request) throws JsonProcessingException {
        service.getUpdatedCustomersFromSugarCRM();
    }

    @GetMapping("/machines/")
    @Operation(summary = "Get Updated Machines from SugarCRM", tags = { CUSTOMER, SUGARCRM })
    @PreAuthorize("hasAuthority('CAN_READ_MANAGE_SYSTEM')")
    public void getUpdatedMachinesFromSugarCRM(HttpServletRequest request) throws JsonProcessingException {
        service.getUpdatedMachinesFromSugarCRM();
    }

}
