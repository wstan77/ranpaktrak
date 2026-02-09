package com.lbisoft.core.app.components.machine.controller;

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

import com.lbisoft.core.app.components.machine.model.Machine;
import com.lbisoft.core.app.components.machine.service.MachineService;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/machines/")
@Tag(name = "Machines")
public class MachineController {

    private static final String MACHINE = "2 - Machines";

    @Autowired
    MachineService service;

    @Autowired
    private UserActivitiesService activitiesService;

    @GetMapping("/{regionLk}/")
    @Operation(summary = "Get Updated Machines for the Given Region", tags = { MACHINE })
    @PreAuthorize("hasAuthority('CAN_READ_MACHINES')")
    public List<Machine> getUpdatedMachinesByRegion(@PathVariable String regionLk,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastSyncTimestamp,
            HttpServletRequest request) {
        activitiesService.logActivity(
                new UserActivityBuilder(ActivityTypes.SYNC_MACHINES).relatedString(regionLk)
                        .relatedDate(lastSyncTimestamp).build(),
                request);
        AppUser currentUser = RequestHelper.getUser(request);
        return service.getUpdatedMachinesByRegion(regionLk, lastSyncTimestamp, currentUser);
    }

}
