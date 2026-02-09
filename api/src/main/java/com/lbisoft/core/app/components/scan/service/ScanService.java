package com.lbisoft.core.app.components.scan.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.Constants;
import com.lbisoft.core.app.components.customer.model.Customer;
import com.lbisoft.core.app.components.customer.service.CustomerService;
import com.lbisoft.core.app.components.freshdesk.service.FreshdeskService;
import com.lbisoft.core.app.components.machine.model.Machine;
import com.lbisoft.core.app.components.machine.service.MachineService;
import com.lbisoft.core.app.components.scan.model.Scan;
import com.lbisoft.core.app.components.scan.model.ScanResult;
import com.lbisoft.core.app.components.scan.model.ScanUploadResultExistingAccount;
import com.lbisoft.core.app.components.scan.model.ScanUploadResultNewAccount;
import com.lbisoft.core.app.components.scan.model.ScanUploadResults;
import com.lbisoft.core.app.components.sugarcrm.service.SugarService;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.ScanDbMapper;
import com.lbisoft.core.app.db.mapper.generated.ScanResultDbMapper;
import com.lbisoft.core.app.db.model.generated.ScanDb;
import com.lbisoft.core.app.db.model.generated.ScanResultDb;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class ScanService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final Dao<ScanDb, Integer> dao;
    private final Dao<ScanResultDb, Integer> resultDao;
    private final ScanDbMapper mapper;
    private final ScanResultDbMapper resultMapper;
    private final AbstractProvider<ScanDb, Scan> provider;
    private final AbstractProvider<ScanResultDb, ScanResult> resultProvider;
    private final FreshdeskService freshdeskService;
    private final MachineService machineService;
    private final SugarService sugarService;
    private final CustomerService customerService;
    private final ScanReportService reportService;

    private String EMAIL_DISCLAIMER = "This is an automated notification from the Ranpaktrak Asset Tracking Application.\n\n";

    @Autowired
    public ScanService(ScanDbMapper mapper, ScanResultDbMapper resultMapper, FreshdeskService freshdeskService,
            MachineService machineService, SugarService sugarService, CustomerService customerService,
            ScanReportService reportService) {
        this.mapper = mapper;
        this.resultMapper = resultMapper;
        this.provider = ProviderBuilder.simple(ScanDb.class, Scan.class);
        this.resultProvider = ProviderBuilder.simple(ScanResultDb.class, ScanResult.class);
        dao = Dao.getDao(mapper, ScanDb::getScanId);
        resultDao = Dao.getDao(resultMapper, ScanResultDb::getScanResultId);
        this.freshdeskService = freshdeskService;
        this.machineService = machineService;
        this.sugarService = sugarService;
        this.customerService = customerService;
        this.reportService = reportService;
    }

    /**
     * Process a set of scans from the mobile application
     * Save scans to the DB
     * Send scans to SugarCRM (if appropriate)
     * Send emails to HelpDesk (if appropriate)
     * Write scan results to the DB
     */
    @Transactional
    public ScanUploadResults processScans(List<Scan> scans, String regionLk, AppUser currentUser) {
        if (scans == null || scans.isEmpty()) {
            return new ScanUploadResults();
        }
        // setup object to store scan results
        ScanUploadResults r = new ScanUploadResults();
        // generate a UUID for all scans being saved with this transaction
        String uuid = UUID.randomUUID().toString();
        // setup some buckets for holding scans of different types that will be
        // processed differently later
        Map<String, ArrayList<Scan>> manualAccountScans = new HashMap<String, ArrayList<Scan>>();
        Map<Integer, ArrayList<Scan>> scansByCustomer = new HashMap<Integer, ArrayList<Scan>>();
        List<Scan> scansToVerifyInSugar = new ArrayList<Scan>();
        // loop over scans to set UUID and process appropriately
        // this step will organize scans into buckets by customer for processing later
        scans.forEach(s -> {
            Machine existingMachine = null;
            // setup a result object and set the UUID
            s.setBatchUuid(uuid);
            ScanResult sr = new ScanResult();
            // handle manual scans
            if (s.getCustomerId() == null || s.getCustomerId().intValue() == 0) {
                if (manualAccountScans.containsKey(s.getManualAccountName())) {
                    manualAccountScans.get(s.getManualAccountName()).add(s);
                } else {
                    ArrayList<Scan> t = new ArrayList<Scan>();
                    t.add(s);
                    manualAccountScans.put(s.getManualAccountName(), t);
                }
                sr.setScanResultTypeLk(Constants.ScanResultTypes.CUSTOMER_NOT_IN_SUGAR);
            }
            // process machines for valid customers
            else {
                // get customer record
                Customer customer = customerService.getCustomerById(s.getCustomerId());
                if (customer == null) {
                    return;
                }
                s.setCustomer(customer);
                if (customer.getDistributorCustomerId() != null) {
                    customer.setDistributor(customerService.getCustomerById(customer.getDistributorCustomerId()));
                }
                existingMachine = machineService.getMachineBySerialNumber(s.getSerialNumber());
                // machine exists
                if (existingMachine != null) {
                    s.setMachine(existingMachine);
                    // check if customer matches between scan and existing record
                    if (existingMachine.getCustomerId() != null
                            && existingMachine.getCustomerId().equals(s.getCustomerId())) {
                        // check if existing machine has a newer scan already registered
                        if (existingMachine.getScanTmstp() != null
                                && existingMachine.getScanTmstp().isAfter(s.getScanDate())) {
                            sr.setScanResultTypeLk(Constants.ScanResultTypes.LATER_SCAN_IN_SUGAR);
                        }
                        // send scan to SugarCRM
                        else {
                            sr.setScanResultTypeLk(Constants.ScanResultTypes.SCAN_SENT_TO_SUGAR);
                            ScanUploadResultExistingAccount sur = r.getExistingAccount(customer.getAccountName());
                            sur.getVerified().add(existingMachine.getSerialNumber());
                            scansToVerifyInSugar.add(s);
                        }
                    }
                    // machine has changed locations
                    else {
                        sr.setScanResultTypeLk(Constants.ScanResultTypes.MACHINE_AT_NEW_LOCATION);
                    }
                    // set tag ID on machine
                    existingMachine.setTagId(s.getTagId());
                }
                // machine doesn't exist
                else {
                    sr.setScanResultTypeLk(Constants.ScanResultTypes.MACHINE_NOT_IN_SUGAR);
                }
                // put scan into bucket by customer
                if (scansByCustomer.containsKey(s.getCustomerId())) {
                    scansByCustomer.get(s.getCustomerId()).add(s);
                } else {
                    ArrayList<Scan> t = new ArrayList<Scan>();
                    t.add(s);
                    scansByCustomer.put(s.getCustomerId(), t);
                }
            }
            // save the scan and the scan result
            ScanDb sDb = provider.reverseMap(s);
            dao.insert(sDb, currentUser);
            s.setScanId(sDb.getScanId());
            sr.setScanId(sDb.getScanId());
            s.setScanResult(sr);
            resultDao.insert(resultProvider.reverseMap(sr), currentUser);
            // save the machine, if we found one
            if (existingMachine != null) {
                existingMachine.setScanId(sDb.getScanId());
                existingMachine.setScanTmstp(sDb.getCreateDate());
                machineService.updateMachine(existingMachine, currentUser);
            }
        });
        // process the different buckets of scans appropriately for any post processing
        // tasks such as sending to SugarCRM or FreshDesk emails
        processScansForSugar(scansToVerifyInSugar, uuid);
        processScansByCustomer(scansByCustomer, r, currentUser, regionLk, uuid);
        processScansWithoutCustomer(manualAccountScans, r, currentUser, regionLk);
        // return scan results object
        return r;
    }

    /**
     * Send verified scans to SugarCRM
     */
    private void processScansForSugar(List<Scan> scans, String uuid) {
        sugarService.sendScanToSugarCRM(scans, uuid);
    }

    /**
     * Process scans by customer
     * A single email will get sent for all possible error conditions for each
     * customer
     */
    private void processScansByCustomer(Map<Integer, ArrayList<Scan>> scans, ScanUploadResults sr,
            AppUser currentUser, String regionLk, String uuid) {
        if (scans == null || scans.keySet().isEmpty()) {
            return;
        }
        // loop over each customer
        // one email will be generated for each
        scans.keySet().forEach(c -> {
            if (scans.get(c) == null || scans.get(c).isEmpty()) {
                return;
            }
            // get customer record
            Customer customer = customerService.getCustomerById(c);
            if (customer == null) {
                return;
            }
            // setup object for tracking results for API response
            ScanUploadResultExistingAccount sur = sr.getExistingAccount(customer.getAccountName());
            // get list of all machines known to be at this location that were not scanned
            // as part of this batch
            List<Machine> unscannedMachines = machineService.getUnscannedMachines(c, uuid);
            String unscannedSns = null;
            if (unscannedMachines != null && !unscannedMachines.isEmpty()) {
                unscannedSns = unscannedMachines.stream()
                        .map(Machine::getMachineSnDescription)
                        .collect(Collectors.joining("\n"));
            }
            // collect serial numbers for scans in the various error conditions
            List<String> newLocationSns = scans.get(c).stream()
                    .filter(s -> Constants.ScanResultTypes.MACHINE_AT_NEW_LOCATION
                            .equals(s.getScanResult().getScanResultTypeLk()))
                    .map(Scan::getSerialNumber)
                    .collect(Collectors.toList());
            List<String> machineNotFoundSns = scans.get(c).stream()
                    .filter(s -> Constants.ScanResultTypes.MACHINE_NOT_IN_SUGAR
                            .equals(s.getScanResult().getScanResultTypeLk()))
                    .map(Scan::getSerialNumber)
                    .collect(Collectors.toList());
            // send an email if any scans for this customer had one of the above error
            // conditions
            if ((unscannedSns != null && unscannedSns.length() > 0)
                    || (newLocationSns != null && newLocationSns.size() > 0)
                    || (machineNotFoundSns != null && machineNotFoundSns.size() > 0)) {
                String emailSubject = "Ranpaktrak - Scan(s) Completed with Errors - Account: "
                        + customer.getAccountName();
                String emailBody = EMAIL_DISCLAIMER +
                        "User " + currentUser.getUsername() + " completed scan(s) for account "
                        + customer.getAccountName() +
                        " which resulted in errors.  See below for a breakdown of those errors:\n\n" +
                        ((newLocationSns != null && newLocationSns.size() > 0)
                                ? "The following machines were scanned but are not known to be at the Account/Location in SugarCRM:\n"
                                        + newLocationSns.stream().collect(Collectors.joining("\n")) + "\n\n"
                                : "")
                        +
                        ((machineNotFoundSns != null && machineNotFoundSns.size() > 0)
                                ? "The following machines were scanned but their serial numbers were not found in SugarCRM:\n"
                                        + machineNotFoundSns.stream().collect(Collectors.joining("\n")) + "\n\n"
                                : "")
                        +
                        ((unscannedSns != null && unscannedSns.length() > 0)
                                ? "The following machines are at the location according to the latest data in SugarCRM but were not scanned:\n"
                                        + unscannedSns + "\n\n"
                                : "");
                String emailCc = currentUser.isAnonymousFlg() || "admin".equalsIgnoreCase(currentUser.getUsername())
                        ? null
                        : currentUser.getUsername();
                try {
                    freshdeskService.createFreshdeskTicketViaEmail(emailSubject, emailBody, emailCc, regionLk,
                            getImagesFromScans(scans.get(c), true), reportService.createScanDataCSV(scans.get(c)));
                } catch (Exception e) {
                    LOGGER.error(
                            "Exception sending email to FreshDesk to create tickets for Unknown Account Scans by User: "
                                    + currentUser.getUsername(),
                            e);
                }
                // add scan results
                sur.setNotFound(machineNotFoundSns);
                sur.setMoved(newLocationSns);
            }
        });
    }

    /**
     * Process a list of scans that do not have a customer (manually entered
     * customer name)
     * These will all go in a single email to the HelpDesk
     */
    private void processScansWithoutCustomer(Map<String, ArrayList<Scan>> scans, ScanUploadResults sr,
            AppUser currentUser, String regionLk) {
        if (scans == null || scans.keySet().isEmpty()) {
            return;
        }
        // loop over each unique manually entered account
        // one email will be generated for each
        scans.keySet().forEach(c -> {
            if (scans.get(c) == null || scans.get(c).isEmpty()) {
                return;
            }
            List<Scan> scansByAccount = scans.get(c);
            // generate email details
            String emailSubject = "Ranpaktrak - Scan(s) Completed for Unknown Account: " + c;
            String emailBody = EMAIL_DISCLAIMER +
                    "User " + currentUser.getUsername() + " completed scan(s) for account " + c +
                    ", which is not a known account in SugarCRM.\n\n" +
                    "The following serial numbers were scanned:\n" +
                    scansByAccount.stream().map(Scan::getSerialNumber).collect(Collectors.joining("\n"));
            String emailCc = currentUser.isAnonymousFlg() || "admin".equalsIgnoreCase(currentUser.getUsername()) ? null
                    : currentUser.getUsername();
            try {
                freshdeskService.createFreshdeskTicketViaEmail(emailSubject, emailBody, emailCc, regionLk,
                        getImagesFromScans(scansByAccount, false), reportService.createScanDataCSV(scansByAccount));
            } catch (Exception e) {
                LOGGER.error(
                        "Exception sending email to FreshDesk to create tickets for Unknown Account Scans by User: "
                                + currentUser.getUsername(),
                        e);
            }
            // add to scan results
            ScanUploadResultNewAccount sur = new ScanUploadResultNewAccount(c, scansByAccount.size());
            sr.getNewAccounts().add(sur);
        });
    }

    /**
     * Collect any images from any of the scans to attach as files
     */
    private List<InputStreamSource> getImagesFromScans(List<Scan> scans, boolean errorsOnly) {
        List<InputStreamSource> images = new ArrayList<InputStreamSource>();
        for (Scan s : scans) {
            if (s.getScanImage() != null && s.getScanImage().length > 0 && (!errorsOnly || (s.getScanResult() != null
                    && !Constants.ScanResultTypes.SCAN_SENT_TO_SUGAR
                            .equals(s.getScanResult().getScanResultTypeLk())))) {
                images.add(new ByteArrayResource(s.getScanImage()));
            }
        }
        return images;
    }

}
