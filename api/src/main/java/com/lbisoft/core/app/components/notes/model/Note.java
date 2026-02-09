package com.lbisoft.core.app.components.notes.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.lbisoft.core.app.Constants.PermissionLks;
import com.lbisoft.core.framework.permissions.model.Ownable;
import com.lbisoft.core.framework.response.ResponseObject;

public class Note extends ResponseObject implements Ownable {
  private Integer noteId;
  private String noteTypeLk;
  private LocalDate noteDate;
  private String recordStatusLk;
  private String subject;
  private String comments;
  private List<NoteReference> references = Collections.emptyList();
  private byte[] attachment;
  private String fileName;
  private String fileExt;

  // audit info
  private LocalDateTime createDate;
  private Integer createUserId;
  private LocalDateTime lastChangeDate;
  private Integer lastChangeUserId;
  private LocalDateTime systemLastChangeDate;
  private String ownerName;
  private boolean readFlg;

  // end
  public Integer getNoteId() {
    return noteId;
  }

  public void setNoteId(Integer noteId) {
    this.noteId = noteId;
  }

  public String getNoteTypeLk() {
    return noteTypeLk;
  }

  public void setNoteTypeLk(String noteTypeLk) {
    this.noteTypeLk = noteTypeLk;
  }

  public LocalDate getNoteDate() {
    return noteDate;
  }

  public void setNoteDate(LocalDate noteDate) {
    this.noteDate = noteDate;
  }

  public String getRecordStatusLk() {
    return recordStatusLk;
  }

  public void setRecordStatusLk(String recordStatusLk) {
    this.recordStatusLk = recordStatusLk;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public List<NoteReference> getReferences() {
    return references;
  }

  public void setReferences(List<NoteReference> references) {
    this.references = references;
  }

  public byte[] getAttachment() {
    return attachment;
  }

  public void setAttachment(byte[] attachment) {
    this.attachment = attachment;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileExt() {
    return fileExt;
  }

  public void setFileExt(String fileExt) {
    this.fileExt = fileExt;
  }

  public LocalDateTime getCreateDate() {
    return createDate;
  }

  public void setCreateDate(LocalDateTime createDate) {
    this.createDate = createDate;
  }

  public Integer getCreateUserId() {
    return createUserId;
  }

  public void setCreateUserId(Integer createUserId) {
    this.createUserId = createUserId;
  }

  public LocalDateTime getLastChangeDate() {
    return lastChangeDate;
  }

  public void setLastChangeDate(LocalDateTime lastChangeDate) {
    this.lastChangeDate = lastChangeDate;
  }

  public Integer getLastChangeUserId() {
    return lastChangeUserId;
  }

  public void setLastChangeUserId(Integer lastChangeUserId) {
    this.lastChangeUserId = lastChangeUserId;
  }

  public LocalDateTime getSystemLastChangeDate() {
    return systemLastChangeDate;
  }

  public void setSystemLastChangeDate(LocalDateTime systemLastChangeDate) {
    this.systemLastChangeDate = systemLastChangeDate;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public boolean getReadFlg() {
    return readFlg;
  }

  public void setReadFlg(boolean readFlg) {
    this.readFlg = readFlg;
  }

  @Override
  public Integer getOwnerUserId() {
    return this.createUserId;
  }

  public void setOwnerUserId(Integer userId) {
    setCreateUserId(userId);
  }

  @Override
  public String getPermissionLk() {
    return PermissionLks.NOTES;
  }
}
