package com.lbisoft.core.app.components.sugarcrm.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lbisoft.core.app.Constants;
import com.lbisoft.core.app.components.customer.service.CustomerService;
import com.lbisoft.core.app.components.machine.service.MachineService;
import com.lbisoft.core.app.components.scan.model.Scan;
import com.lbisoft.core.app.components.sugarcrm.model.AuthenticationRequest;
import com.lbisoft.core.app.components.sugarcrm.model.AuthenticationResult;
import com.lbisoft.core.app.components.sugarcrm.model.CustomerApiResponse;
import com.lbisoft.core.app.components.sugarcrm.model.MachineApiResponse;
import com.lbisoft.core.app.components.sugarcrm.model.SugarCustomer;
import com.lbisoft.core.app.components.sugarcrm.model.SugarMachine;
import com.lbisoft.core.app.components.sugarcrm.model.SugarScan;
import com.lbisoft.core.app.components.sugarcrm.provider.SugarCustomerProvider;
import com.lbisoft.core.app.components.sugarcrm.provider.SugarMachineProvider;
import com.lbisoft.core.app.components.sugarcrm.provider.SugarScanProvider;
import com.lbisoft.core.app.db.mapper.generated.JobLogDbMapper;
import com.lbisoft.core.app.db.model.generated.CustomerDb;
import com.lbisoft.core.app.db.model.generated.JobLogDb;
import com.lbisoft.core.app.db.model.generated.JobLogDbExample;
import com.lbisoft.core.app.db.model.generated.MachineDb;
import com.lbisoft.core.app.settings.SugarCRMSettings;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.service.CoreServices.CoreService;
import com.lbisoft.core.framework.utils.RestHelper;

@CoreService
public class SugarService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private SugarCRMSettings sugarSettings;

    private final Dao<JobLogDb, Integer> jobLogDao;
    private final JobLogDbMapper jobLogMapper;
    private final SugarCustomerProvider customerProvider;
    private final CustomerService customerService;
    private final SugarMachineProvider machineProvider;
    private final MachineService machineService;
    private final SugarScanProvider scanProvider;

    public SugarService(JobLogDbMapper jobLogMapper, CustomerService customerService,
            SugarCustomerProvider customerProvider, SugarMachineProvider machineProvider,
            MachineService machineService, SugarScanProvider scanProvider) {
        this.jobLogMapper = jobLogMapper;
        jobLogDao = Dao.getDao(jobLogMapper, JobLogDb::getJobLogId);
        this.customerService = customerService;
        this.customerProvider = customerProvider;
        this.machineProvider = machineProvider;
        this.machineService = machineService;
        this.scanProvider = scanProvider;
    }

    private RestTemplate getRestTemplate() {
        return RestHelper.getRestTemplate(sugarSettings.getTimeoutInMilliseconds());
    }

    private AuthenticationResult authenticate(RestTemplate rt) {
        AuthenticationRequest authRequest = new AuthenticationRequest(sugarSettings.getUsername(),
                sugarSettings.getPassword());
        return rt.postForObject(sugarSettings.getUrl() + "authenticate/", authRequest,
                AuthenticationResult.class);
    }

    /**
     * Sends a scan to SugarCRM
     */
    public void sendScanToSugarCRM(List<Scan> scans, String uuid) {
        if (scans == null || scans.isEmpty()) {
            return;
        }
        LOGGER.info("Sending Scans to SugarCRM - Batch UUID: " + uuid);
        // create the scan objects to send to Sugar
        List<SugarScan> sugarScans = scanProvider.mapAll(scans);
        // create RestTemplate for us in making SugarCRM API calls
        RestTemplate rt = getRestTemplate();
        // authenticate to SugarCRM
        AuthenticationResult auth = authenticate(rt);
        // setup request to post scans with headers for authorization
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + auth.getToken());
        HttpEntity<List<SugarScan>> entity = new HttpEntity<>(sugarScans, headers);
        try {
            ResponseEntity<Object> resp = rt.exchange((sugarSettings.getUrl() + "powerautomate/machine"),
                    HttpMethod.POST,
                    entity, Object.class);
            if (resp.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("SugarCRM Updated Successfully");
            } else {
                throw new Exception(resp.toString());
            }
        } catch (Exception e) {
            LOGGER.error("Exception Sending Scans for Batch UUID " + uuid + " to SugarCRM.", e);
        }
    }

    /**
     * Call SugarCRM API to retrieve any customer records updated since the last
     * time
     * records were successfully sync'd from SugarCRM
     * Write new and updated records to the application database
     * Runs every hour on the hour
     */
    @Scheduled(cron = "0 0 * ? * *")
    @Transactional
    public void getUpdatedCustomersFromSugarCRM() throws JsonProcessingException {
        LOGGER.info("Initiating Sync of Customers from SugarCRM");
        // create variable for start time of sync - will be used to log the job
        // execution
        LocalDateTime jobStartTime = LocalDateTime.now(ZoneOffset.UTC);
        // get the time of the most recent sync - if this is the first sync, assume data
        // was pre-loaded and go back a month just in case
        JobLogDbExample jlEx = new JobLogDbExample();
        jlEx.createCriteria().andJobTypeLkEqualTo(Constants.JobTypes.GETCUST);
        jlEx.setOrderByClause("job_execution_tmstp desc");
        Optional<JobLogDb> lastRunJobLog = jobLogDao.selectOne(jlEx);
        LocalDateTime lastRunTimestamp = lastRunJobLog.isPresent() ? lastRunJobLog.get().getJobExecutionTmstp()
                : LocalDateTime.now().minus(1, ChronoUnit.MONTHS);
        // create RestTemplate for us in making SugarCRM API calls
        RestTemplate rt = getRestTemplate();
        // authenticate to SugarCRM
        AuthenticationResult auth = authenticate(rt);
        // process in chunks based on offset value - if the offset is > 0, there are
        // more records to return
        int totalCustomers = 0;
        int nextOffset = 0;
        boolean success = true;
        while (nextOffset >= 0) {
            try {
                // call SugarCRM api for customers
                CustomerApiResponse response = getCustomersFromSugar(lastRunTimestamp, nextOffset, auth, rt);
                LOGGER.info("Processing chunk of " + response.getRecords().size() + " customers.");
                // insert/update customers in app DB
                totalCustomers += processSugarCustomersToDB(response.getRecords());
                // update offset for next chunk
                nextOffset = response.getNext_offset() != null && response.getNext_offset() > 0
                        ? response.getNext_offset()
                        : -1;
            } catch (Exception e) {
                LOGGER.error("Exception updating customers from SugarCRM: " + e.getMessage(), e);
                nextOffset = -1;
                success = false;
            }
        }
        // write job log record if job succeeded fully - if not, will need to rerun to
        // pick up any customers from prior run
        if (success) {
            JobLogDb jobLog = new JobLogDb();
            jobLog.setJobTypeLk(Constants.JobTypes.GETCUST);
            jobLog.setJobExecutionTmstp(jobStartTime);
            jobLogDao.insert(jobLog, null);
        }
        LOGGER.info("Completed Sync of Customers from SugarCRM - Customers Synced: " + totalCustomers);
    }

    /**
     * Call SugarCRM API to retrieve any machine records updated since the last
     * time
     * records were successfully sync'd from SugarCRM
     * Write new and updated records to the application database
     * Runs every hour on the half hour
     */
    @Scheduled(cron = "0 30 * ? * *")
    @Transactional
    public void getUpdatedMachinesFromSugarCRM() throws JsonProcessingException {
        LOGGER.info("Initiating Sync of Machines from SugarCRM");
        // create variable for start time of sync - will be used to log the job
        // execution
        LocalDateTime jobStartTime = LocalDateTime.now(ZoneOffset.UTC);
        // get the time of the most recent sync - if this is the first sync, assume data
        // was pre-loaded and go back a month just in case
        JobLogDbExample jlEx = new JobLogDbExample();
        jlEx.createCriteria().andJobTypeLkEqualTo(Constants.JobTypes.GETMACHINE);
        jlEx.setOrderByClause("job_execution_tmstp desc");
        Optional<JobLogDb> lastRunJobLog = jobLogDao.selectOne(jlEx);
        LocalDateTime lastRunTimestamp = lastRunJobLog.isPresent() ? lastRunJobLog.get().getJobExecutionTmstp()
                : LocalDateTime.now().minus(1, ChronoUnit.MONTHS);
        // create RestTemplate for us in making SugarCRM API calls
        RestTemplate rt = getRestTemplate();
        // authenticate to SugarCRM
        AuthenticationResult auth = authenticate(rt);
        // process in chunks based on offset value - if the offset is > 0, there are
        // more records to return
        int totalMachines = 0;
        int nextOffset = 0;
        boolean success = true;
        while (nextOffset >= 0) {
            try {
                // call SugarCRM api for machines
                MachineApiResponse response = getMachinesFromSugar(lastRunTimestamp, nextOffset, auth, rt);
                LOGGER.info("Processing chunk of " + response.getRecords().size() + " machines.");
                // insert/update customers in app DB
                totalMachines += processSugarMachinesToDB(response.getRecords());
                // update offset for next chunk
                nextOffset = response.getNext_offset() != null && response.getNext_offset() > 0
                        ? response.getNext_offset()
                        : -1;
            } catch (Exception e) {
                LOGGER.error("Exception updating machines from SugarCRM: " + e.getMessage(), e);
                nextOffset = -1;
                success = false;
            }
        }
        // write job log record if job succeeded fully - if not, will need to rerun to
        // pick up any machines from prior run
        if (success) {
            JobLogDb jobLog = new JobLogDb();
            jobLog.setJobTypeLk(Constants.JobTypes.GETMACHINE);
            jobLog.setJobExecutionTmstp(jobStartTime);
            jobLogDao.insert(jobLog, null);
        }
        LOGGER.info("Completed Sync of Machines from SugarCRM - Machines Synced: " + totalMachines);
    }

    /**
     * Get customers from SugarCRM
     * API takes a timestamp - retrieves records updated since the timestamp
     * API takes an offset - it will only return 1000 records at a time - use the
     * offset if there are more records to retrieve
     */
    private CustomerApiResponse getCustomersFromSugar(LocalDateTime lastRunTimestamp, long offset,
            AuthenticationResult auth, RestTemplate rt) {
        // setup request to get customers with headers for authorization
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + auth.getToken());
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        // setup URL with query parameters
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(
                sugarSettings.getUrl() + "powerautomate/businesspartner")
                .queryParam("date_modified", lastRunTimestamp.format(formatter))
                .queryParam("offset", offset);
        return rt
                .exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.GET, entity,
                        CustomerApiResponse.class)
                .getBody();
    }

    /**
     * Get machines from SugarCRM
     * API takes a timestamp - retrieves records updated since the timestamp
     * API takes an offset - it will only return 1000 records at a time - use the
     * offset if there are more records to retrieve
     */
    private MachineApiResponse getMachinesFromSugar(LocalDateTime lastRunTimestamp, long offset,
            AuthenticationResult auth, RestTemplate rt) {
        // setup request to get customers with headers for authorization
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + auth.getToken());
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        // setup URL with query parameters
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(
                sugarSettings.getUrl() + "powerautomate/machine")
                .queryParam("date_modified", lastRunTimestamp.format(formatter))
                .queryParam("offset", offset);
        return rt
                .exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.GET, entity,
                        MachineApiResponse.class)
                .getBody();
    }

    /**
     * Store a list of customers from SugarCRM to the application DB
     * Return a number of records updated
     */
    private int processSugarCustomersToDB(List<SugarCustomer> sugarCustomers) {
        if (sugarCustomers != null && sugarCustomers.size() > 0) {
            // map beans from API to our beans for customers
            // filter out any records without a customer key or a name
            List<CustomerDb> customerDbs = customerProvider.reverseMapAll(sugarCustomers).stream()
                    .filter(c -> c.getCustomerKey() != null && !"".equals(c.getCustomerKey())
                            && c.getAccountName() != null
                            && !"".equals(c.getAccountName()))
                    .collect(Collectors.toList());
            // insert or update customers in database
            // customerkey guaranteed unique but not our PK - so must check for record first
            return customerService.upsertCustomersByCustomerKey(customerDbs);
        }
        return 0;
    }

    /**
     * Store a list of machines from SugarCRM to the application DB
     * Return a number of records updated
     */
    private int processSugarMachinesToDB(List<SugarMachine> sugarMachines) {
        if (sugarMachines != null && sugarMachines.size() > 0) {
            // map beans from API to our beans for machines
            // filter out any records without a serial number
            // translate customer keys from sugar to our unique IDs for customer and
            // distributor
            List<MachineDb> machineDbs = new ArrayList<MachineDb>();
            sugarMachines.forEach(m -> {
                if (m.getSerial_number() != null && !"".equals(m.getSerial_number())) {
                    MachineDb mDb = machineProvider.reverseMap(m);
                    mDb.setCustomerId(customerService.getCustomerIdByCustomerKey(m.getAccount_number()));
                    mDb.setDistributorCustomerId(customerService.getCustomerIdByCustomerKey(m.getDistributor_number()));
                    machineDbs.add(mDb);
                }
            });
            // insert or update machines in database
            // serial number guaranteed unique but not our PK - so must check for record
            // first
            return machineService.upsertMachinesBySerialNumber(machineDbs);
        }
        return 0;
    }

}
