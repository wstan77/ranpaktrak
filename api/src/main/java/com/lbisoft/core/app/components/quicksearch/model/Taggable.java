package com.lbisoft.core.app.components.quicksearch.model;

import java.util.List;

import com.lbisoft.core.framework.search.model.SearchItem;

public interface Taggable extends SearchItem<Integer> {

  String getRecordStatusLk();

  String getIcon();

  List<String> getDetails();

  boolean getActiveFlg();

}
