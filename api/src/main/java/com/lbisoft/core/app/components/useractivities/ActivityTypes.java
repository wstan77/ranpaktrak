package com.lbisoft.core.app.components.useractivities;

import com.lbisoft.core.framework.useractivities.ActivityType;
import com.lbisoft.core.framework.useractivities.ActivityTypeBuilder;

public class ActivityTypes {
    static final String NOTE_ID = "Note Id";
    static final String NOTE_SEARCH_FORM = "Note Search Form";
    static final String NOTE = "Note";
    static final String THREAD_ID = "Thread Id";
    static final String THREAD = "Thread";
    static final String THREAD_MESSAGE = "Thread Message";
    static final String TASK_ID = "Task Id";
    static final String TASK = "Task";
    static final String TASK_SEARCH_FORM = "Task Search Form";
    static final String PERMISSIONS = "Permissions";
    static final String PERSON_ID = "Person Id";
    static final String PERSON_SEARCH_FORM = "Person Search Form";
    static final String PERSON = "Person";
    static final String REPORTED_ISSUE = "Reported Issue";
    static final String REPORTED_ISSUE_ID = "Reported Issue Id";
    static final String SYSTEM_DATE_FORM = "System Date Form";
    static final String SYSTEM_VALUE_FORM = "System Value Form";
    static final String USER_ID = "User Id";
    static final String USER_SEARCH_FORM = "User Search Form";
    static final String USER = "User";
    static final String REFERENCE_TYPE_LK = "Reference Type";
    static final String REFERENCE_ID = "Reference Id";
    static final String CUSTOMERS = "Customers";
    static final String MACHINES = "Machines";
    static final String SCANS = "Scans";
    static final String REGION = "Region";
    static final String LAST_SYNC_DATE = "Last Sync Date";

    public static final ActivityType LOAD_THREAD = new ActivityTypeBuilder("GET_THREAD")
            .description("Load Thread")
            .relatedIdName(THREAD_ID)
            .build();
    public static final ActivityType LOAD_THREADS_FOR_REFERENCE = new ActivityTypeBuilder("GET_THREADS_FOR_REFERENCE")
            .description("Load Threads for Reference")
            .relatedIdName(REFERENCE_ID)
            .relatedStringName(REFERENCE_TYPE_LK)
            .build();
    public static final ActivityType LOAD_THREADS_FOR_PARTICIPANT = new ActivityTypeBuilder(
            "GET_THREADS_FOR_PARTICIPANT")
            .description("Load Threads for Participant")
            .relatedIdName(USER_ID)
            .build();
    public static final ActivityType CREATE_THREAD = new ActivityTypeBuilder("CREATE_THREAD")
            .description("Create Thread")
            .relatedObjectName(THREAD)
            .build();
    public static final ActivityType DELETE_THREAD = new ActivityTypeBuilder("DELETE_THREAD")
            .description("Delete Thread")
            .relatedIdName(THREAD_ID)
            .build();
    public static final ActivityType POST_THREAD_MESSAGE = new ActivityTypeBuilder("POST_THREAD_MESSAGE")
            .description("Post Thread Message")
            .relatedObjectName(THREAD_MESSAGE)
            .build();
    public static final ActivityType SEARCH_TASKS = new ActivityTypeBuilder("SEARCH_TASKS")
            .description("Search Tasks")
            .relatedObjectName(TASK_SEARCH_FORM)
            .relatedStringName(REFERENCE_TYPE_LK)
            .relatedIdName(REFERENCE_ID)
            .build();
    public static final ActivityType CREATE_TASK = new ActivityTypeBuilder("CREATE_TASK")
            .description("Create Task")
            .relatedObjectName(TASK)
            .build();
    public static final ActivityType UPDATE_TASK = new ActivityTypeBuilder("UPDATE_TASK")
            .description("Update Task")
            .relatedObjectName(TASK)
            .build();
    public static final ActivityType ASSIGN_TASK = new ActivityTypeBuilder("ASSIGN_TASK")
            .description("Assign Task")
            .relatedIdName(TASK_ID)
            .relatedNumberName(USER_ID)
            .build();
    public static final ActivityType CHANGE_TASK_STATUS = new ActivityTypeBuilder("CHANGE_TASK_STATUS")
            .description("Change Task Status")
            .relatedIdName(TASK_ID)
            .relatedStringName("Status Lk")
            .build();
    public static final ActivityType DELETE_TASK = new ActivityTypeBuilder("DELETE_TASK")
            .description("Delete Task")
            .relatedIdName(TASK_ID)
            .build();
    public static final ActivityType CHECK_TASKS = new ActivityTypeBuilder("CHECK_TASKS")
            .description("Check Tasks")
            .build();
    public static final ActivityType LOAD_NOTE = new ActivityTypeBuilder("GET_NOTE")
            .description("Load Note")
            .relatedIdName(NOTE_ID)
            .build();
    public static final ActivityType SEARCH_NOTES = new ActivityTypeBuilder("SEARCH_NOTES")
            .description("Search Notes")
            .relatedObjectName(NOTE_SEARCH_FORM)
            .build();
    public static final ActivityType SAVE_NOTE = new ActivityTypeBuilder("SAVE_NOTE")
            .description("Save Note")
            .relatedObjectName(NOTE)
            .build();
    public static final ActivityType DELETE_NOTE = new ActivityTypeBuilder("DELETE_NOTE")
            .description("Delete Note")
            .relatedIdName(NOTE_ID)
            .build();
    public static final ActivityType SAVE_PERMISSIONS = new ActivityTypeBuilder("SAVE_PERMISSIONS")
            .description("Save Permissions")
            .relatedIdName(PERMISSIONS)
            .build();
    public static final ActivityType SAVE_USER = new ActivityTypeBuilder("SAVE_USER")
            .description("Save User")
            .relatedObjectName(USER)
            .build();
    public static final ActivityType DELETE_USER = new ActivityTypeBuilder("DELETE_USER")
            .description("Delete User")
            .relatedIdName(USER_ID)
            .build();
    public static final ActivityType LOAD_USER = new ActivityTypeBuilder("LOAD_USER")
            .description("Load User")
            .relatedIdName(USER_ID)
            .build();
    public static final ActivityType CREATE_USER = new ActivityTypeBuilder("CREATE_USER")
            .description("Create User")
            .relatedObjectName(USER)
            .build();
    public static final ActivityType SEARCH_USERS = new ActivityTypeBuilder("SEARCH_USERS")
            .description("Search User")
            .relatedObjectName(USER_SEARCH_FORM)
            .build();
    public static final ActivityType REPORT_ISSUE = new ActivityTypeBuilder("REPORT_ISSUE")
            .description("Report Issue")
            .relatedObjectName(REPORTED_ISSUE)
            .build();
    public static final ActivityType SAVE_REPORTED_ISSUE = new ActivityTypeBuilder("SAVE_REPORTED_ISSUE")
            .description("Update Reported Issue")
            .relatedObjectName(REPORTED_ISSUE)
            .build();
    public static final ActivityType LOAD_REPORTED_ISSUE = new ActivityTypeBuilder("LOAD_REPORTED_ISSUE")
            .description("Load Reported Issue")
            .relatedIdName(REPORTED_ISSUE_ID)
            .build();

    public static final ActivityType DELETE_PERSON = new ActivityTypeBuilder("DELETE_PERSON")
            .description("Delete Person")
            .relatedIdName(PERSON_ID)
            .build();
    public static final ActivityType LOAD_PERSON = new ActivityTypeBuilder("LOAD_PERSON")
            .description("Load Person")
            .relatedIdName(PERSON_ID)
            .build();
    public static final ActivityType CREATE_PERSON = new ActivityTypeBuilder("CREATE_PERSON")
            .description("Create Person")
            .relatedObjectName(PERSON)
            .build();
    public static final ActivityType SEARCH_PERSONS = new ActivityTypeBuilder("SEARCH_PERSONS")
            .description("Search Persons")
            .relatedObjectName(PERSON_SEARCH_FORM)
            .build();
    public static final ActivityType SAVE_PERSON = new ActivityTypeBuilder("SAVE_PERSON")
            .description("Save Person")
            .relatedObjectName(PERSON)
            .build();
    public static final ActivityType SAVE_SYSTEM_VALUES = new ActivityTypeBuilder("SAVE_SYSTEM_VALUES")
            .description("Save System Values")
            .relatedObjectName(SYSTEM_VALUE_FORM)
            .build();
    public static final ActivityType SAVE_SYSTEM_DATES = new ActivityTypeBuilder("SAVE_SYSTEM_DATES")
            .description("Save System Dates")
            .relatedObjectName(SYSTEM_DATE_FORM)
            .build();
    public static final ActivityType SYNC_CUSTOMERS = new ActivityTypeBuilder("SYNC_CUSTOMERS")
            .description("Sync Customers")
            .relatedStringName(REGION)
            .relatedDateName(LAST_SYNC_DATE)
            .build();
    public static final ActivityType SYNC_MACHINES = new ActivityTypeBuilder("SYNC_MACHINES")
            .description("Sync Machines")
            .relatedStringName(REGION)
            .relatedDateName(LAST_SYNC_DATE)
            .build();
    public static final ActivityType UPLOAD_SCANS = new ActivityTypeBuilder("UPLOAD_SCANS")
            .description("Upload Scans")
            .relatedStringName(REGION)
            .relatedObjectName(SCANS)
            .build();
}
