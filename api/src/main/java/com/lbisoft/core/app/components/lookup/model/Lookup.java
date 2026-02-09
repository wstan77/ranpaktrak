package com.lbisoft.core.app.components.lookup.model;

public class Lookup<K extends Object, V extends Object> {
  private K primaryKey;
  private V lookup;

  public Lookup(K primaryKey, V lookup) {
    this.primaryKey = primaryKey;
    this.lookup = lookup;
  }

  public K getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(K primaryKey) {
    this.primaryKey = primaryKey;
  }

  public V getLookup() {
    return lookup;
  }

  public void setLookup(V lookup) {
    this.lookup = lookup;
  }
}
