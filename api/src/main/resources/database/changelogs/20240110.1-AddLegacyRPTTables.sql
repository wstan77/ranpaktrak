--liquibase formatted sql
--changeset tieds:AddLegacyRPTTables endDelimiter:go

/* 
 * table: customers 
 */

create table customers(
    customer_id     			int                 identity(1,1),
	customer_key    			varchar(10)         null,
    account_name    			varchar(256)        not null,
	city					    varchar(256)		null,
	state_lk					varchar(10)			null,
	country_lk					varchar(10) 		null,    
    region_lk       			varchar(10)         null,
	distributor_flg				bit					null,
    create_date                 datetime2(7)    	default getdate() not null,
    create_user_id              int             	default 0 not null,
    last_change_date            datetime2(7)    	default getdate() not null,
    last_change_user_id         int             	default 0 not null,
    system_last_change_date     datetime2(7)    	default getdate() not null,
    constraint pk_customers primary key nonclustered (customer_id)
)
go


if object_id('customers') is not null
    print '<<< created table customers >>>'
else
    print '<<< failed creating table customers >>>'
go

/* 
 * table: job_logs 
 */

create table job_logs(
    job_log_id             		int             identity(1,1),
    job_type_lk            		varchar(10)     not null,
    job_execution_tmstp    		datetime2(7)    null,
	create_date                 datetime2(7)    default getdate() not null,
    create_user_id              int             default 0 not null,
    last_change_date            datetime2(7)    default getdate() not null,
    last_change_user_id         int             default 0 not null,
    system_last_change_date     datetime2(7)    default getdate() not null,
    constraint pk_job_logs primary key clustered (job_log_id)
)
go



if object_id('job_logs') is not null
    print '<<< created table job_logs >>>'
else
    print '<<< failed creating table job_logs >>>'
go

/* 
 * table: lk_job_types 
 */

create table lk_job_types(
    job_type_lk           		varchar(10)     not null,
    description			  		varchar(100)    not null,
    short_description           varchar(20)     not null,
	active_flg					BIT				NOT NULL,
    create_date                 datetime2(7)    default getdate() not null,
    create_user_id              int             default 0 not null,
    last_change_date            datetime2(7)    default getdate() not null,
    last_change_user_id         int             default 0 not null,
    system_last_change_date     datetime2(7)    default getdate() not null,
    constraint pk_lk_job_types primary key clustered (job_type_lk)
)
go



if object_id('lk_job_types') is not null
    print '<<< created table lk_job_types >>>'
else
    print '<<< failed creating table lk_job_types >>>'
go
insert into lk_job_types (job_type_lk, short_description, description, active_flg) values('GETCUST', 'GETCUST', 'Get Customers from SugarCRM',1);
insert into lk_job_types (job_type_lk, short_description, description, active_flg) values('GETMACHINE', 'GETMACHINE', 'Get Machines from SugarCRM',1);
insert into lk_job_types (job_type_lk, short_description, description, active_flg) values('NEWSCAN', 'NEWSCAN', 'Process New Scan',1);


/* 
 * table: lk_regions 
 */

create table lk_regions(
    region_lk          			varchar(10)          not null,
    description			  		varchar(100)    not null,
    short_description           varchar(20)     not null,
	active_flg					BIT				NOT NULL,
    create_date                 datetime2(7)    default getdate() not null,
    create_user_id              int             default 0 not null,
    last_change_date            datetime2(7)    default getdate() not null,
    last_change_user_id         int             default 0 not null,
    system_last_change_date     datetime2(7)    default getdate() not null,
    constraint pk_lk_regions primary key clustered (region_lk)
)
go



if object_id('lk_regions') is not null
    print '<<< created table lk_regions >>>'
else
    print '<<< failed creating table lk_regions >>>'
go
insert into lk_regions (region_lk, short_description, description, active_flg) values('NOAM', 'NOAM', 'North America',1);
insert into lk_regions (region_lk, short_description, description, active_flg) values('EMEA', 'EMEA', 'Europe and Middle East',1);
insert into lk_regions (region_lk, short_description, description, active_flg) values('APAC', 'APAC', 'Asia Pacific',1);


/* 
 * table: lk_scan_result_types
 */

create table lk_scan_result_types(
    scan_result_type_lk         varchar(10)          not null,
    description			  		varchar(100)    not null,
    short_description           varchar(20)     not null,
	active_flg					BIT				NOT NULL,
    create_date                 datetime2(7)    default getdate() not null,
    create_user_id              int             default 0 not null,
    last_change_date            datetime2(7)    default getdate() not null,
    last_change_user_id         int             default 0 not null,
    system_last_change_date     datetime2(7)    default getdate() not null,
    constraint pk_lk_scan_result_types primary key clustered (scan_result_type_lk)
)
go



if object_id('lk_scan_result_types') is not null
    print '<<< created table lk_scan_result_types >>>'
else
    print '<<< failed creating table lk_scan_result_types >>>'
go
insert into lk_scan_result_types (scan_result_type_lk, short_description, description, active_flg) values('SUGAR', 'SugarCRM', 'Sent Scan to SugarCRM', 1);
insert into lk_scan_result_types (scan_result_type_lk, short_description, description, active_flg) values('OUTOFDATE', 'Later Scan Exists', 'Later Scan Previously Registered', 1);
insert into lk_scan_result_types (scan_result_type_lk, short_description, description, active_flg) values('NOCUSTOMER', 'No Customer', 'Customer Does Not Exist', 1);
insert into lk_scan_result_types (scan_result_type_lk, short_description, description, active_flg) values('NEWLOC', 'New Location', 'Machine Registered at a Different Location', 1);
insert into lk_scan_result_types (scan_result_type_lk, short_description, description, active_flg) values('NOMACHINE', 'No Machine', 'Machine Serial Number Not Found', 1);

/* 
 * table: lk_scan_types 
 */

create table lk_scan_types(
    scan_type_lk           varchar(10)          not null,
    description			  		varchar(100)    not null,
    short_description           varchar(20)     not null,
	active_flg					BIT				NOT NULL,
    create_date                 datetime2(7)    default getdate() not null,
    create_user_id              int             default 0 not null,
    last_change_date            datetime2(7)    default getdate() not null,
    last_change_user_id         int             default 0 not null,
    system_last_change_date     datetime2(7)    default getdate() not null,
    constraint pk_lk_scan_types primary key nonclustered (scan_type_lk)
)
go



if object_id('lk_scan_types') is not null
    print '<<< created table lk_scan_types >>>'
else
    print '<<< failed creating table lk_scan_types >>>'
go
insert into lk_scan_types (scan_type_lk, short_description, description, active_flg) values('QR', 'QR Code', 'QR Code', 1);
insert into lk_scan_types (scan_type_lk, short_description, description, active_flg) values('NFC', 'NFC Scan', 'NFC Scan', 1);
insert into lk_scan_types (scan_type_lk, short_description, description, active_flg) values('MANUAL', 'Manual Entry', 'Manual Entry', 1);

/* 
 * table: lk_machine_types 
 */

create table lk_machine_types(
    machine_type_lk	            varchar(10)          not null,
    description			  		varchar(100)    not null,
    short_description           varchar(20)     not null,
	active_flg					BIT				NOT NULL,
    create_date                 datetime2(7)    default getdate() not null,
    create_user_id              int             default 0 not null,
    last_change_date            datetime2(7)    default getdate() not null,
    last_change_user_id         int             default 0 not null,
    system_last_change_date     datetime2(7)    default getdate() not null,
    constraint pk_lk_machine_types primary key nonclustered (machine_type_lk)
)
go



if object_id('lk_scan_types') is not null
    print '<<< created table lk_scan_types >>>'
else
    print '<<< failed creating table lk_scan_types >>>'
go

/* 
 * table: machines
 */

create table machines(
    machine_id       		int                  identity(1,1),
    serial_number    		varchar(128)         not null,
	machine_type_lk			varchar(10)			 null,
	legacy_serial_number    varchar(128)  		 null,
    customer_id      		int                  null,
	distributor_customer_id int 				 null,
    region_lk        		varchar(10)          null,
    tag_id           		varchar(128)         null,
    scan_id          		int                  null,
    scan_tmstp       		datetime2(7)    	 null,	
    create_date                 datetime2(7)    default getdate() not null,
    create_user_id              int             default 0 not null,
    last_change_date            datetime2(7)    default getdate() not null,
    last_change_user_id         int             default 0 not null,
    system_last_change_date     datetime2(7)    default getdate() not null,
    constraint pk_machines primary key clustered (machine_id)
)
go



if object_id('machine') is not null
    print '<<< created table machine >>>'
else
    print '<<< failed creating table machine >>>'
go

/* 
 * table: scans 
 */

create table scans(
    scan_id                int                  identity(1,1),
    customer_id            int                  null,
    scan_type_lk           varchar(10)          not null,
    serial_number          varchar(128)         not null,
    tag_id                 varchar(128)         null,
    region_lk              varchar(10)          null,
    notes                  varchar(1024)        null,
    manual_account_name    varchar(256)         null,
    manual_location        varchar(256)         null,
	batch_uuid  		   varchar(100) 		null,
	batch_record_num  	   int 					null,
	batch_record_total     int 					null,
	scan_image		  	   varbinary(max) 		null,
	scan_gps_latitude	   decimal(11,8)		null,
	scan_gps_longitude	   decimal(11,8)		null,
	scan_gps_accuracy	   decimal(10,2)		null,
    create_date                 datetime2(7)    default getdate() not null,
    create_user_id              int             default 0 not null,
    last_change_date            datetime2(7)    default getdate() not null,
    last_change_user_id         int             default 0 not null,
    system_last_change_date     datetime2(7)    default getdate() not null,
    constraint pk_scans primary key nonclustered (scan_id)
)
go

if object_id('scans') is not null
    print '<<< created table scans >>>'
else
    print '<<< failed creating table scans >>>'
go

/* 
 * table: scan_results 
 */

create table scan_results(
    scan_result_id         int                  identity(1,1),
    scan_id                int                  not null,
    scan_result_type_lk    varchar(10)          not null,
    create_date                 datetime2(7)    default getdate() not null,
    create_user_id              int             default 0 not null,
    last_change_date            datetime2(7)    default getdate() not null,
    last_change_user_id         int             default 0 not null,
    system_last_change_date     datetime2(7)    default getdate() not null,
    constraint pk_scan_results primary key clustered (scan_result_id)
)
go



if object_id('scan_results') is not null
    print '<<< created table scan_results >>>'
else
    print '<<< failed creating table scan_results >>>'
go

/* 
 * index: ckey1 
 */

create unique index customers_key on customers(customer_key)
go
if exists (select * from sys.indexes where object_id=object_id('customer') and name='ckey1')
    print '<<< created index customer.ckey1 >>>'
else
    print '<<< failed creating index customer.ckey1 >>>'
go

/* 
 * table: customer 
 */

alter table customers add constraint ref_customers_lu_region
    foreign key (region_lk)
    references lk_regions(region_lk)
go


/* 
 * table: job_log 
 */

alter table job_logs add constraint ref_job_logs_lu_job_type
    foreign key (job_type_lk)
    references lk_job_types(job_type_lk)
go


/* 
 * table: machine 
 */

alter table machines add constraint ref_machines_scan
    foreign key (scan_id)
    references scans(scan_id)
go

alter table machines add constraint ref_machines_customer
    foreign key (customer_id)
    references customers(customer_id)
go

alter table machines add constraint ref_machines_distributor 
    foreign key (distributor_customer_id)
    references customers(customer_id)
go

alter table machines add constraint ref_machines_lu_region
    foreign key (region_lk)
    references lk_regions(region_lk)
go

alter table machines add constraint ref_machines_lu_machinetype
    foreign key (machine_type_lk)
    references lk_machine_types(machine_type_lk)
go


/* 
 * table: scan 
 */

alter table scans add constraint ref_scans_lu_scan_type
    foreign key (scan_type_lk)
    references lk_scan_types(scan_type_lk)
go

alter table scans add constraint ref_scans_customer
    foreign key (customer_id)
    references customers(customer_id)
go

alter table scans add constraint ref_scans_lu_region 
    foreign key (region_lk)
    references lk_regions(region_lk)
go


/* 
 * table: scan_results
 */

alter table scan_results add constraint ref_scan_results_lu_scan_result_type 
    foreign key (scan_result_type_lk)
    references lk_scan_result_types(scan_result_type_lk)
go

alter table scan_results add constraint ref_scan_results_scan
    foreign key (scan_id)
    references scans(scan_id)
go


