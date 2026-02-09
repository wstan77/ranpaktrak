package com.lbisoft.core.app.components.reportedissues.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.reportedissues.model.ReportIssueForm;
import com.lbisoft.core.app.components.reportedissues.model.ReportedIssue;
import com.lbisoft.core.app.components.reportedissues.service.ReportedIssueService;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.response.ResponseObject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/reported-issues")
@Tag(name = "Reported Issues")
public class ReportedIssueController {

  @Autowired
  private ReportedIssueService reportedIssueService;
  @Autowired
  private UserActivitiesService userActivitiesService;

  @PostMapping("/report")
  @Operation(summary = "Report an issue")
  public ResponseObject reportIssue(@RequestBody ReportIssueForm reportedIssue,
      HttpServletRequest request) {
    reportedIssueService.reportIssue(reportedIssue, request);
    return new ResponseObject().addResponseInfo("Successfully reported issue");
  }

  @GetMapping("/{reportedIssueId}")
  @Operation(summary = "Get a reported issue")
  @PreAuthorize("hasAuthority('CAN_READ_REPORTED_ISSUES')")
  public ReportedIssue getReportedIssue(@PathVariable("reportedIssueId") Integer reportedIssueId,
      HttpServletRequest request) {
    var issue = reportedIssueService.getReportedIssue(reportedIssueId)
        .orElseThrow(() -> new ResourceNotFoundException());
    userActivitiesService.logActivityById(ActivityTypes.LOAD_REPORTED_ISSUE,
        reportedIssueId, request);
    return issue;
  }

  @PostMapping("/{reportedIssueId}")
  @Operation(summary = "Update a reported issue")
  @PreAuthorize("hasAuthority('CAN_READ_REPORTED_ISSUES')")
  public ReportedIssue updateReportedIssue(@PathVariable("reportedIssueId") Integer reportedIssueId,
      @RequestBody ReportedIssue reportedIssue, HttpServletRequest request) {
    return reportedIssueService.updateIssue(reportedIssue, request);
  }

  @GetMapping("/all")
  @Operation(summary = "Get all reported issues")
  @PreAuthorize("hasAuthority('CAN_READ_REPORTED_ISSUES')")
  public List<ReportedIssue> getAllReportedIssues(HttpServletRequest request) {
    return reportedIssueService.getReportedIssues();
  }
}
