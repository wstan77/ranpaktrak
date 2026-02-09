package com.lbisoft.core.app.components.sugarcrm.model;

import lombok.Data;

@Data
public class SugarScan {
    private String account_number;
    private String serial_number;
    private String last_verification_date;
    private String tag_id;
}
