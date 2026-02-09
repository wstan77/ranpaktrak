package com.lbisoft.core.app.components.scan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.scan.model.Scan;
import com.lbisoft.core.app.components.scan.model.ScanUploadResults;
import com.lbisoft.core.app.components.scan.service.ScanService;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/scans/")
@Tag(name = "Scans")
public class ScanController {

    private static final String SCAN = "3 - Scans";

    @Autowired
    private UserActivitiesService activitiesService;

    @Autowired
    ScanService service;

    @PostMapping("/{regionLk}/")
    @Operation(summary = "Submit a set of scans", tags = { SCAN })
    @PreAuthorize("hasAuthority('CAN_READ_SCANS')")
    public ScanUploadResults submitScans(@RequestBody List<Scan> scans, @PathVariable String regionLk,
            HttpServletRequest request) {
        activitiesService.logActivity(
                new UserActivityBuilder(ActivityTypes.UPLOAD_SCANS).relatedString(regionLk)
                        .relatedObject(scans).build(),
                request);
        AppUser currentUser = RequestHelper.getUser(request);
        return service.processScans(scans, regionLk, currentUser);
    }

}
