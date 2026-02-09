package com.lbisoft.core.app.components.machine.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Machine {

    private Integer machineId;
    private String serialNumber;
    private String legacySerialNumber;
    private String machineTypeLk;
    private Integer customerId;
    private Integer distributorCustomerId;
    private String regionLk;
    private String tagId;
    private Integer scanId;
    private LocalDateTime scanTmstp;

    public String getMachineSnDescription() {
        return "SAP# " + serialNumber
                + ((legacySerialNumber != null && !"".equals(legacySerialNumber)) ? (" - Legacy# " + legacySerialNumber)
                        : "");
    }

}
