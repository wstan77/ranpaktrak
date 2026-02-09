package com.lbisoft.core.app.components.reportedissues.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lbisoft.core.app.components.reportedissues.model.ReportIssueForm;
import com.lbisoft.core.app.components.reportedissues.model.ReportedIssue;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.app.db.mapper.generated.ReportedIssueDbMapper;
import com.lbisoft.core.app.db.model.generated.ReportedIssueDb;
import com.lbisoft.core.app.db.model.generated.ReportedIssueDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

import jakarta.servlet.http.HttpServletRequest;

public abstract class AbstractReportedIssueService {

  private final AbstractProvider<ReportedIssueDb, ReportedIssue> provider = ProviderBuilder
      .simple(ReportedIssueDb.class, ReportedIssue.class);
  private final Dao<ReportedIssueDb, Integer> dao;
  private final UserActivitiesService activitiesService;

  public AbstractReportedIssueService(ReportedIssueDbMapper mapper,
      UserActivitiesService activitiesService) {
    this.dao = Dao.getDao(mapper, ReportedIssueDb::getReportedIssueId);
    this.activitiesService = activitiesService;
  }

  protected abstract String uploadScreenshot(String uuid, ReportIssueForm form);

  protected abstract String uploadVideoFile(String uuid, ReportIssueForm form);

  protected abstract void sendNotification(ReportedIssue issue, AppUser user);

  protected abstract String getScreenshotPath(ReportedIssue issue);

  protected abstract String getVideoPath(ReportedIssue issue);

  public ReportedIssue reportIssue(ReportIssueForm form, HttpServletRequest request) {
    AppUser user = RequestHelper.getUser(request);

    ReportedIssue issue = new ReportedIssue();
    issue.setIssueTypeLk(form.getIssueTypeLk());
    issue.setDescription(form.getDescription());
    issue.setUrl(form.getUrl());
    issue.setLog(form.getLog());

    String uuid = UUID.randomUUID().toString();
    if (form.getScreenshot() != null) {
      String screenshotFileName = uploadScreenshot(uuid, form);
      issue.setScreenshotPath(screenshotFileName);
    }
    if (form.getVideo() != null) {
      String videoFileName = uploadVideoFile(uuid, form);
      issue.setVideoPath(videoFileName);
    }

    ReportedIssueDb db = provider.reverseMap(issue);
    dao.insert(db, user);

    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.REPORT_ISSUE).relatedObject(issue).build(),
        request);

    var reportedIssue = provider.map(db);
    sendNotification(reportedIssue, user);

    return reportedIssue;
  }

  public ReportedIssue updateIssue(ReportedIssue issue, HttpServletRequest request) {
    AppUser user = RequestHelper.getUser(request);

    ReportedIssueDb db = provider.reverseMap(issue);
    db.setScreenshotPathModified(false);
    db.setVideoPathModified(false);

    dao.update(db, user);

    activitiesService.logActivity(
        new UserActivityBuilder(ActivityTypes.SAVE_REPORTED_ISSUE).relatedObject(issue).build(),
        request);

    return getReportedIssue(db.getReportedIssueId()).orElseThrow(() -> new ResourceNotFoundException());
  }

  public Optional<ReportedIssue> getReportedIssue(Integer reportedIssueId) {
    Optional<ReportedIssueDb> db = dao.select(reportedIssueId);
    return db.map(raw -> {
      ReportedIssue issue = provider.map(raw);
      if (issue.getScreenshotPath() != null) {
        issue.setScreenshotPath(getScreenshotPath(issue));
      }
      if (issue.getVideoPath() != null) {
        issue.setVideoPath(getVideoPath(issue));
      }
      return issue;
    });
  }

  public List<ReportedIssue> getReportedIssues() {
    var dbs = dao.select(new ReportedIssueDbExample());
    return provider.mapAll(dbs);
  }
}
