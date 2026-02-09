package com.lbisoft.core.framework.security;

public class EncryptionSettings {
  public static final String ALGORITHM = "PBEWithHmacSHA512AndAES_256";
  public static final String KEY_OBTENTION_ITERATIONS = "1000";
  public static final String POOL_SIZE = "1";
  public static final String PROVIDER_NAME = "SunJCE";
  public static final String STRING_OUTPUT_TYPE = "base64";
  public static final String PASSWORD = "collegesalt";
}
