package com.lbisoft.core.app.components.reportedissues.model;

import java.time.LocalDateTime;

public class ReportedIssue {
  private Integer reportedIssueId;
  private String issueStatusLk;
  private String issueTypeLk;
  private String description;
  private String screenshotPath;
  private String videoPath;
  private String url;
  private String log;
  private Integer createUserId;
  private LocalDateTime createDate;

  public Integer getReportedIssueId() {
    return this.reportedIssueId;
  }

  public void setReportedIssueId(Integer reportedIssueId) {
    this.reportedIssueId = reportedIssueId;
  }

  public String getIssueStatusLk() {
    return this.issueStatusLk;
  }

  public void setIssueStatusLk(String issueStatusLk) {
    this.issueStatusLk = issueStatusLk;
  }

  public String getIssueTypeLk() {
    return this.issueTypeLk;
  }

  public void setIssueTypeLk(String issueTypeLk) {
    this.issueTypeLk = issueTypeLk;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getScreenshotPath() {
    return this.screenshotPath;
  }

  public void setScreenshotPath(String screenshotPath) {
    this.screenshotPath = screenshotPath;
  }

  public String getVideoPath() {
    return this.videoPath;
  }

  public void setVideoPath(String videoPath) {
    this.videoPath = videoPath;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getLog() {
    return this.log;
  }

  public void setLog(String log) {
    this.log = log;
  }

  public Integer getCreateUserId() {
    return this.createUserId;
  }

  public void setCreateUserId(Integer createUserId) {
    this.createUserId = createUserId;
  }

  public LocalDateTime getCreateDate() {
    return this.createDate;
  }

  public void setCreateDate(LocalDateTime createDate) {
    this.createDate = createDate;
  }

}
