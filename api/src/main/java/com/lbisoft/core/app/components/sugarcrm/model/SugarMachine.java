package com.lbisoft.core.app.components.sugarcrm.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Data;

@Data
public class SugarMachine {
    private String id;
    private String serial_number;
    private LocalDate last_verification_date;
    private String legacy_serial_number;
    private String account_number;
    private String distributor_number;
    private String account_region;

    // our DB is more exact than the sugar API and uses scan timestamps
    // convert scan times from sugar to noon on the day of the scan
    public LocalDateTime getLastVerificationDateAsTimestamp() {
        return last_verification_date != null ? LocalDateTime.of(last_verification_date, LocalTime.of(12, 00, 00))
                : null;

    }

}
