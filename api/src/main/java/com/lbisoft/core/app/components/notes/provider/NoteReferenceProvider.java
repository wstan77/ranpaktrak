package com.lbisoft.core.app.components.notes.provider;

import java.util.List;

import com.lbisoft.core.app.components.notes.model.NoteReference;
import com.lbisoft.core.app.components.quicksearch.model.QuickSearchTag;
import com.lbisoft.core.app.components.quicksearch.model.Taggable;
import com.lbisoft.core.app.db.model.generated.NoteReferenceDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

public class NoteReferenceProvider extends AbstractProvider<NoteReferenceDb, NoteReference> {

  @Override
  protected void defineClassMap(ClassMapBuilder<NoteReferenceDb, NoteReference> classMapBuilder) {
    classMapBuilder.field("referenceId", "tag.id").field("referenceTypeLk", "tag.type");
  }

  @Override
  protected NoteReference completeMap(NoteReferenceDb input, NoteReference output, Object... children) {
    QuickSearchTag tag = new QuickSearchTag();
    tag.setId(input.getReferenceId());
    tag.setType(input.getReferenceTypeLk());

    output.setTag(tag);

    return output;
  }

  @Override
  protected NoteReference completeMapAll(NoteReferenceDb input, NoteReference output, List<?>... children) {
    return completeMap(input, output);
  }

  @Override
  protected void defineChildren(ChildrenMap<NoteReference> childrenMap) {
    childrenMap.defineOneToOne(Taggable.class, NoteReference::setTag,
        (reference, tag) -> tag.getType().equals(reference.getTag().getType())
            && tag.getId().equals(reference.getTag().getId()));

  }

}
