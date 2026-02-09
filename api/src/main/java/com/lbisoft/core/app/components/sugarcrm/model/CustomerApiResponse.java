package com.lbisoft.core.app.components.sugarcrm.model;

import java.util.List;

import lombok.Data;

@Data
public class CustomerApiResponse {
    private List<SugarCustomer> records;
    private Integer next_offset;
}
