package com.lbisoft.core.app.components.person.xrefs.model;

public class PersonXref {
    private String sourceSystemLk;
    private String sourceId;
    private Integer personId;
    
    public String getSourceSystemLk() {
        return sourceSystemLk;
    }
    public void setSourceSystemLk(String sourceSystemLk) {
        this.sourceSystemLk = sourceSystemLk;
    }
    public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    public Integer getPersonId() {
        return personId;
    }
    public void setPersonId(Integer personId) {
        this.personId = personId;
    }
}
