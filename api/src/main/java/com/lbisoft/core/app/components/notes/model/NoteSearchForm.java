package com.lbisoft.core.app.components.notes.model;

import java.time.LocalDate;
import java.util.List;

import com.lbisoft.core.framework.models.AbstractSearchForm;

public class NoteSearchForm extends AbstractSearchForm {
    private List<String> tagKeys;
    private List<Integer> userIds;
	private LocalDate dateFrom;
	private LocalDate dateTo;
	private Boolean hasFile;
    private String noteTypeLk;

  public List<String> getTagKeys() {
    return tagKeys;
  }

  public void setTagKeys(List<String> tagKeys) {
    this.tagKeys = tagKeys;
  }

	public LocalDate getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(LocalDate dateFrom) {
		this.dateFrom = dateFrom;
	}

	public LocalDate getDateTo() {
		return dateTo;
	}

	public void setDateTo(LocalDate dateTo) {
		this.dateTo = dateTo;
	}

	public Boolean getHasFile() {
		return hasFile;
	}

	public void setHasFile(Boolean hasFile) {
		this.hasFile = hasFile;
	}

	public String getNoteTypeLk() {
		return noteTypeLk;
	}

	public void setNoteTypeLk(String noteTypeLk) {
		this.noteTypeLk = noteTypeLk;
  }

  public List<Integer> getUserIds() {
    return userIds;
  }

  public void setUserIds(List<Integer> userIds) {
    this.userIds = userIds;
  }
}
