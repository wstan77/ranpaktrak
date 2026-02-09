package com.lbisoft.core.app.components.reportedissues.service;

import com.lbisoft.core.app.components.reportedissues.model.ReportIssueForm;
import com.lbisoft.core.app.components.reportedissues.model.ReportedIssue;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.app.db.mapper.generated.ReportedIssueDbMapper;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class ReportedIssueService extends AbstractReportedIssueService {

    public ReportedIssueService(ReportedIssueDbMapper mapper,
            UserActivitiesService activitiesService) {
        super(mapper, activitiesService);
    }

    @Override
    protected String uploadScreenshot(String uuid, ReportIssueForm form) {
        throw new UnsupportedOperationException("Reported Issues have not been implemented");
    }

    @Override
    protected String uploadVideoFile(String uuid, ReportIssueForm form) {
        throw new UnsupportedOperationException("Reported Issues have not been implemented");
    }

    @Override
    protected void sendNotification(ReportedIssue issue, AppUser user) {
        throw new UnsupportedOperationException("Reported Issues have not been implemented");
    }

    @Override
    protected String getScreenshotPath(ReportedIssue issue) {
        throw new UnsupportedOperationException("Reported Issues have not been implemented");
    }

    @Override
    protected String getVideoPath(ReportedIssue issue) {
        throw new UnsupportedOperationException("Reported Issues have not been implemented");
    }
}
