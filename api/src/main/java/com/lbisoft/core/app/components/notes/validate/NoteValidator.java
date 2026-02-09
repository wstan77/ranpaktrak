package com.lbisoft.core.app.components.notes.validate;

import java.util.List;

import com.lbisoft.core.app.Constants.UploadTypes;
import com.lbisoft.core.app.components.notes.model.Note;
import com.lbisoft.core.framework.utils.FileDownloadHelper;
import com.lbisoft.core.framework.validation.Validator;

public class NoteValidator {
  private static final Integer ONE_HUNDRED_MB = 100 * 1024 * 1024;

  public Validator validate(Note note) {
    Validator validator = new Validator();
    validator.check("Date", note.getNoteDate()).isPresent().isNotInTheFuture().and()
        .check("Note Type", note.getNoteTypeLk()).isPresent().and().check("Subject", note.getSubject()).isNotEmpty()
        .isUnderMaximumCharacters(150).complete();
    if (note.getAttachment() != null) {
      // validate file extension
      validator.check("Attachment File Extension", note.getFileExt()).isPresent().complete();

      if (validator.isValid()) {
        validator.addErrorUnless(FileDownloadHelper.validateExtension(note.getAttachment(), note.getFileExt()),
            "Attachment file extension doesn't match contents.");

        boolean supported = List.of(UploadTypes.values()).stream()
            .anyMatch(type -> type.toString().equalsIgnoreCase(note.getFileExt()));
        validator.addErrorUnless(supported, "Attachment file type is not supported.");
      }
      // validate file name
      validator.check("Note File Name", note.getFileName()).isPresent().isUnderMaximumCharacters(250).complete();
      // validate file size
      validator.addErrorIf(note.getAttachment().length > ONE_HUNDRED_MB, "Attachment must be less than 100 MB.");
    }

    if (note.getNoteId() != null) {
      validator.check("Note References", note.getReferences()).isPresent().hasAtLeastNItems(1).complete();
    }

    return validator;
  }
}
