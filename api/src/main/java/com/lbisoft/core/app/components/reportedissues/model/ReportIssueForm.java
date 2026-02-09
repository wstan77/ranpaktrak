package com.lbisoft.core.app.components.reportedissues.model;

public class ReportIssueForm {
  private String issueTypeLk;
  private String description;
  private byte[] screenshot;
  private byte[] video;
  private String url;
  private String log;

  public String getIssueTypeLk() {
    return issueTypeLk;
  }

  public void setIssueTypeLk(String issueTypeLk) {
    this.issueTypeLk = issueTypeLk;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public byte[] getScreenshot() {
    return screenshot;
  }

  public void setScreenshot(byte[] screenshot) {
    this.screenshot = screenshot;
  }

  public byte[] getVideo() {
    return video;
  }

  public void setVideo(byte[] video) {
    this.video = video;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getLog() {
    return log;
  }

  public void setLog(String log) {
    this.log = log;
  }
}
