package com.lbisoft.core.app.components.lookup.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.lookup.model.Lookup;
import com.lbisoft.core.app.components.lookup.service.Lookups;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/lookups", produces = "application/json")
@Tag(name = "Lookups", description = "Application Enums")
public class LookupController {

  @Autowired
  private Lookups lookups;

  @GetMapping(value = "/{lookupName}")
  public List<?> getLookupList(@PathVariable String lookupName, HttpServletRequest request) {
    return lookups.getList(lookupName);
  }

  @GetMapping("/{lookupName}/by-key")
  public Map<?, ?> getLookupsByKey(@PathVariable String lookupName, HttpServletRequest request) {
    return lookups.getLookupsByPrimaryKey(lookupName);
  }

  @GetMapping("/{lookupName}/tuples")
  public List<Lookup<Object, Object>> getLookupsAsTuples(@PathVariable String lookupName,
      HttpServletRequest request) {
    return lookups.getTuples(lookupName);
  }
}
