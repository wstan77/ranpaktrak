package com.lbisoft.core.app.components.sugarcrm.model;

import java.util.List;

import lombok.Data;

@Data
public class MachineApiResponse {
    private List<SugarMachine> records;
    private Integer next_offset;
}
