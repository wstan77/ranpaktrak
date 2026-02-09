package com.lbisoft.core.app.components.scan.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lbisoft.core.app.components.scan.model.Scan;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class ScanReportService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Create a CSV file with scan data
     * 
     * @throws IOException
     */
    public File createScanDataCSV(List<Scan> scans) throws IOException {
        if (scans == null && scans.isEmpty()) {
            return null;
        }
        // define columns to include
        String[] HEADERS = { "Scan ID", "Scan Date", "Customer Key", "Customer Name", "Serial Number", "Tag ID",
                "Scan Type", "Region", "Distributor Key", "Distributor Name", "GPS Latitude", "GPS Longitude",
                "GPS Accuracy", "Scan Batch ID", "Notes" };
        // setup some utility objections
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .build();
        try {
            File file = File.createTempFile("scan-data", ".csv");
            CSVPrinter printer = csvFormat.print(file, StandardCharsets.UTF_8);
            for (Scan s : scans) {
                printer.printRecord(
                        s.getScanId(),
                        s.getScanDate() != null
                                ? s.getScanDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:MM:ss"))
                                : "",
                        s.getCustomer() != null ? s.getCustomer().getCustomerKey() : "",
                        s.getCustomer() != null ? s.getCustomer().getAccountName() : s.getManualAccountName(),
                        s.getSerialNumber(),
                        s.getTagId(),
                        s.getScanTypeLk(),
                        s.getRegionLk(),
                        s.getCustomer() != null && s.getCustomer().getDistributor() != null
                                ? s.getCustomer().getDistributor().getCustomerKey()
                                : "",
                        s.getCustomer() != null && s.getCustomer().getDistributor() != null
                                ? s.getCustomer().getDistributor().getAccountName()
                                : "",
                        s.getScanGpsLatitude(),
                        s.getScanGpsLongitude(),
                        s.getScanGpsAccuracy(),
                        s.getBatchUuid(),
                        s.getNotes());
            }
            printer.flush();
            return file;
        } catch (Exception e) {
            LOGGER.error("Exception generating CSV file attachment for HelpDesk Email.", e);
        }
        return null;
    }

}
