package com.lbisoft.core.framework.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public final class DateHelper {
  private DateHelper() {
  }

  private static DecimalFormat ageFormat = new DecimalFormat(".#");
  static {
    ageFormat.setRoundingMode(RoundingMode.FLOOR);
  }

  public static Instant asInstant(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toInstant();
  }

  public static Long asMillisecondsSinceEpoch(Instant instant) {
    return instant.getEpochSecond() * 1000;
  }

  public static Instant fromMillisecondsSinceEpoch(Long ms) {
    return Instant.ofEpochMilli(ms);
  }

  public static String getFormattedAgeAsOf(LocalDate birthDate, LocalDate asOfDate) {
    Double age = getAgeAsOf(birthDate, asOfDate);
    return age != null ? ageFormat.format(age.doubleValue()) : null;
  }

  public static Double getAgeAsOf(LocalDate birthDate, LocalDate asOfDate) {
    if (birthDate == null) {
      return null;
    }
    if (asOfDate == null) {
      asOfDate = LocalDate.now();
    }

    int years = (int) ChronoUnit.YEARS.between(birthDate, asOfDate);
    LocalDate lastBirthday = birthDate.withYear(asOfDate.getYear());
    if (lastBirthday.isAfter(asOfDate)) {
      lastBirthday = lastBirthday.withYear(asOfDate.getYear() - 1);
    }

    int daysOfYear = (int) ChronoUnit.DAYS.between(lastBirthday, asOfDate);
    double daysInYear = Year.isLeap(asOfDate.getYear()) ? 366 : 365;
    return years + (daysOfYear / daysInYear);
  }
  /**
   * Returns the greater of two dates. If one of the dates is null, the other one is returned.
   * @param d1
   * @param d2
   * @return
   */
  public static LocalDate max(LocalDate d1, LocalDate d2) {
    if (d1 == null) return d2;
    if (d2 == null) return d1;
    return d1.isAfter(d2) ? d1 : d2;
  }

  /**
   * Returns the lesser of two dates
   * @param d1
   * @param d2
   * @return
   */
  public static LocalDate min(LocalDate d1, LocalDate d2) {
    return d1.isBefore(d2) ? d1 : d2;
  }
}
