package com.lbisoft.core.framework.useractivities;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbisoft.core.framework.user.HasUserId;
import com.lbisoft.core.framework.utils.RequestHelper;
import com.lbisoft.core.framework.utils.StringHelper;

import jakarta.servlet.http.HttpServletRequest;
import ua_parser.Client;
import ua_parser.Parser;

public abstract class AbstractUserActivityService {

  static final String DETAIL_FORMAT = "%s : %s";
  static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

  final Logger LOGGER = LoggerFactory.getLogger(getClass());

  public abstract void saveActivity(UserActivity activity, HasUserId user);

  protected abstract ObjectMapper getObjectMapper();

  /**
   * Given a message and the searchForm object append the search parameters to the
   * end of the
   * message
   */
  public void logActivity(UserActivity activity, HttpServletRequest request) {
    HasUserId user = RequestHelper.getUser(request);
    String uaString = request.getHeader("User-Agent");

    Thread t = new Thread(
        new Runnable() {
          @Override
          public void run() {
            if (activity.getType() == null) {
              LOGGER.error("User Activity must have a type");
            }

            ActivityType type = activity.getType();

            // User Agent
            Parser uaParser = new Parser();
            Client c = uaParser.parse(uaString);

            activity.setUserAgent(uaString);
            activity.setBrowserName(c.userAgent.family);
            activity.setBrowserVersion(StringHelper.join(".", c.userAgent.major, c.userAgent.minor));
            activity.setOsName(c.os.family);
            activity.setOsVersion(StringHelper.join(".", c.os.major, c.os.minor));
            activity.setDeviceType(c.device.family);

            List<String> details = new ArrayList<>();
            if (user != null) {
              details.add("by User Id : " + user.getUserId());
            }
            if (type.getResourceName() != null && activity.getResourceId() != null) {
              details.add(
                  String.format("%s : %d", type.getResourceName(), activity.getResourceId()));
            }
            if (type.getRelatedIdName() != null && activity.getRelatedId() != null) {
              details.add(
                  String.format("%s : %d", type.getRelatedIdName(), activity.getRelatedId()));
            }
            if (type.getRelatedNumberName() != null && activity.getRelatedNumber() != null) {
              details.add(
                  String.format(
                      "%s : %f", type.getRelatedNumberName(), activity.getRelatedNumber()));
            }
            if (type.getRelatedDateName() != null && activity.getRelatedDate() != null) {
              details.add(
                  String.format(
                      DETAIL_FORMAT,
                      type.getRelatedDateName(),
                      activity.getRelatedDate().format(DATE_FORMAT)));
            }
            if (type.getRelatedBoolName() != null && activity.getRelatedBool() != null) {
              details.add(
                  String.format(
                      "%s : %b", type.getRelatedBoolName(), activity.getRelatedBool()));
            }
            if (type.getRelatedStringName() != null && activity.getRelatedString() != null) {
              details.add(
                  String.format(
                      DETAIL_FORMAT, type.getRelatedStringName(), activity.getRelatedString()));
            }

            if (type.getRelatedObjectName() != null && activity.getRelatedObject() != null) {
              if (activity.getRelatedObjectString() == null) {
                try {
                  String relatedObjString = getObjectMapper().writeValueAsString(activity.getRelatedObject());
                  activity.setRelatedObjectString(relatedObjString);

                } catch (Exception e) {
                  LOGGER.error("Failed to convert object for User Activity", e);
                }
              }

              details.add(
                  String.format(
                      DETAIL_FORMAT,
                      type.getRelatedObjectName(),
                      activity.getRelatedObjectString()));
            }

            if (activity.getDeviceType() != null) {
              details.add("Device Type : " + activity.getDeviceType());
            }

            String message = type.getDescription() + " - " + StringUtils.join(details, "; ");

            LOGGER.info(message);
            saveActivity(activity, user);
          }
        });
    t.start();
  }
}
