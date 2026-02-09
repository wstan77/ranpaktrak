package com.lbisoft.core.app.components.notes.validate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lbisoft.core.app.Constants.RecordStatusLks;
import com.lbisoft.core.app.Constants.UploadTypes;
import com.lbisoft.core.app.components.notes.model.Note;
import com.lbisoft.core.app.components.notes.model.NoteReference;
import com.lbisoft.core.app.components.quicksearch.model.QuickSearchTag;
import com.lbisoft.core.framework.exceptions.FormValidationException;
import com.lbisoft.core.framework.validation.Validator;

public class NoteValidatorTest {
  private static final String OVER_150_CHAR = "weOrbA9NBWtmGBT2ECVJQ60EaQa6GPEmg3tJrnyOwOatL3vB2WI6RxMx5nGdZlQuDWOEbxEMyuicuyEnl3p1onhdCEvWGziIHgJimweOrbA9NBWtmGBT2ECVJQ60EaQa6GPEmg3tJrnyOwOatL3vB2W";
  private static final String OVER_250_CHAR = "weOrbA9NBWtmGBT2ECVJQ60EaQa6GPEmg3tJrnyOwOatL3vB2WI6RxMx5nGdZlQuDWOEbxEMyuicuyEnl3p1onhdCEvWGziIHgJimweOrbA9NBWtmGBT2ECVJQ60EaQa6GPEmg3tJrnyOwOatL3vB2WI6RxMx5nGdZlQuDWOEbxEMyuicuyEnl3p1onhdCEvWGziIHgJimweOrbA9NBWtmGBT2ECVJQ60EaQa6GPEmg3tJrnyOwOatL3vB2W";
  private final NoteValidator validator = new NoteValidator();
  private Note note;

  @BeforeEach
  public void setup() {
    // get valid note
    note = getValidNote();

    // ensure note is valid before each test
    validate();
  }

  private Note getValidNote() {
    note = new Note();
    note.setNoteDate(LocalDate.now());
    note.setNoteId(1);
    note.setNoteTypeLk("type");
    note.setSubject("subject");
    note.setRecordStatusLk(RecordStatusLks.APPROVED);
    QuickSearchTag tag = new QuickSearchTag();
    tag.setType("type");
    tag.setId(2);
    NoteReference ref = new NoteReference();
    ref.setNoteId(1);
    ref.setTag(tag);

    note.setReferences(List.of(ref));

    return note;
  }

  /**
   * validate the given note
   */
  private void validate() {
    Validator test = validator.validate(note);
    test.validate();
  }

  /**
   * call validate and expect it to fall
   *
   * @param error - assert message if why test would have failed
   */
  private void validateShouldFail(String error) {
    try {
      Validator test = validator.validate(note);
      test.validate();
    } catch (FormValidationException e) {
      return;
    }
    fail(error);
  }

  @Test
  public void testTextValue() {
    // setup
    note.setSubject(OVER_150_CHAR);
    validateShouldFail("should fail because text is too long");

    // fix note
    note.setSubject("something shorter");
    validate();
  }

  @Test
  public void testDateValue() {
    LocalDate today = LocalDate.now();
    // setup
    note.setNoteDate(today.plusDays(1));
    validateShouldFail("should fail because date is in the future");

    // fix note
    note.setNoteDate(today.minusDays(1));
    validate();
  }

  @Test
  public void testCreateReferences() {
    // references with creating new note
    note.setNoteId(1);
    note.setReferences(null);
    validateShouldFail("should fail because references are null on an already created note (non null note id)");
    note.setNoteId(null);
    validate();

    note.setNoteId(1);
    note.setReferences(Collections.emptyList());
    validateShouldFail("should fail because references are empty on an already created note (non null note id)");
    note.setNoteId(null);
    validate();
  }

  @Test
  public void testAttachmentValue() {
    byte[] validAttachment = new byte[200];
    // setup
    note.setFileExt(UploadTypes.pdf.name());
    note.setFileName("Test Name");
    note.setAttachment(validAttachment);
    validate();

    // test should fail
    note.setFileName(OVER_250_CHAR);
    validateShouldFail("should fail because file name is too large");

    // test should fail
    note.setFileName(null);
    validateShouldFail("should fail because file name is null");

    // fix note
    note.setFileName("A shorter name");
    validate();

    // test should fail
    note.setFileExt("Invalid file type");
    validateShouldFail("should fail because file ext is not valid");

    // test should fail
    note.setFileExt(null);
    validateShouldFail("should fail because file ext is null");

    // fix note
    note.setFileExt(UploadTypes.png.name());
    validate();
  }

  @Test
  public void testRequired() {
    // references
    List<NoteReference> goodRefs = note.getReferences();

    note.setReferences(null);
    validateShouldFail("should fail because required field is null");
    note.setReferences(Collections.emptyList());
    validateShouldFail("should fail because required field is empty");
    note.setReferences(goodRefs);
    validate();

    // note date
    note.setNoteDate(null);
    validateShouldFail("should fail because required field is null");
    note.setNoteDate(LocalDate.now());
    validate();

    // note type
    note.setNoteTypeLk(null);
    validateShouldFail("should fail because required field is null");
    note.setNoteTypeLk("new type");
    validate();

    // subject
    note.setSubject(null);
    validateShouldFail("should fail because required field is null");
    note.setSubject("new subject");
    validate();
  }

}
