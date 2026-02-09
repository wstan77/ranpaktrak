package com.lbisoft.core.app;

import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.user.model.UserInfo;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionLevel;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionTarget;
import com.lbisoft.core.framework.permissions.model.UserPermissions;

public class Constants {
    public static final Integer DEFAULT_QUERY_LIMIT = 200;
    public static final Integer ADV_SEARCH_RESULTS_LIMIT = 1000;

    public static final Integer SYSTEM_USER_ID = 0;
    public static final String SYSTEM_LEAGUE = "NWSL";

    public static final AppUser SYSTEM_USER;

    static {
        SYSTEM_USER = new AppUser();
        SYSTEM_USER.setFirstName("");
        SYSTEM_USER.setLastName("System");
        SYSTEM_USER.setUserId(SYSTEM_USER_ID);
        SYSTEM_USER.setRecordStatusLk(RecordStatusLks.INACTIVE);
        SYSTEM_USER.setPermissions(new UserPermissions() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean contains(PermissionLevel level, PermissionTarget target, String type) {
                return true;
            }
        });
    }

    public static final UserInfo UNKNOWN_USER;
    static {
        UNKNOWN_USER = new UserInfo();
        UNKNOWN_USER.setFirstName("");
        UNKNOWN_USER.setLastName("Archived");
        UNKNOWN_USER.setUserId(-1);
        UNKNOWN_USER.setRecordStatusLk(RecordStatusLks.DELETED);
    }

    public static class Warnings {
        public static final String TOO_MANY_RESULTS = "Result set has been truncated to %d results. Please refine your search criteria";
    }

    public static class SystemValueLks {
        public static final String CURRENT_YEAR = "YEAR";
    }

    public static class PermissionLks {
        public static final String NOTES = "NOTES";
        public static final String MANAGE_SYSTEM = "MANAGE_SYSTEM";
        public static final String THREADS = "THREADS";
        public static final String TASKS = "TASKS";
    }

    public static class RecordStatusLks {
        public static final String DRAFT = "DR";
        public static final String PUBLISHED = "PB";
        public static final String APPROVED = "AP";
        public static final String DELETED = "DL";
        public static final String UNSAVED = "UN";
        public static final String INACTIVE = "IN";
    }

    public static enum UploadTypes {
        pdf, txt, rtf, doc, docx, csv, xls, xlsx, jpeg, jpg, png, gif, bmp, tif, tiff
    }

    public enum AuthMethod {
        LDAP, DUMMY
    }

    public static class ReferenceTypeLks {
        public static final String PERSON = "PERSON";
        public static final String USER = "USER";
        public static final String USER_GROUP = "USER_GROUP";
    }

    public static class JobTypes {
        public static final String GETCUST = "GETCUST";
        public static final String GETMACHINE = "GETMACHINE";
        public static final String NEWSCAN = "NEWSCAN";
    }

    public static class ScanResultTypes {
        public static final String MACHINE_AT_NEW_LOCATION = "NEWLOC";
        public static final String CUSTOMER_NOT_IN_SUGAR = "NOCUSTOMER";
        public static final String MACHINE_NOT_IN_SUGAR = "NOMACHINE";
        public static final String LATER_SCAN_IN_SUGAR = "OUTOFDATE";
        public static final String SCAN_SENT_TO_SUGAR = "SUGAR";
    }

    public static class Regions {
        public static final String APAC = "APAC";
        public static final String EMEA = "EMEA";
        public static final String NOAM = "NOAM";
    }

    public static class Roles {
        public static final String RANPAKUSER = "RANPAKUSER";
        public static final String DISTRIBUTOR = "DISTRIBUTOR";
    }

}
