package com.lbisoft.core.framework.security;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

public final class Encryptor {
  private static final PooledPBEStringEncryptor encryptor;

  static {
    encryptor = new PooledPBEStringEncryptor();
    EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
    config.setAlgorithm(EncryptionSettings.ALGORITHM);
    config.setProviderName(EncryptionSettings.PROVIDER_NAME);
    config.setPoolSize(1);
    config.setStringOutputType(EncryptionSettings.STRING_OUTPUT_TYPE);
    config.setPassword(EncryptionSettings.PASSWORD);
    encryptor.setConfig(config);
  }
  
  private Encryptor() {}
  
  /**
   * Encrypt a value in preparation to store in the databae
   *
   * @param item - string representation of field
   * @return
   */
  public static byte[] encrypt(String item) {
    if (item == null) {
      return null;
    }
    return encryptor.encrypt(item).getBytes();
  }

  /**
   * Decrypt a value from the database
   *
   * @param item - byte representation (most likely from the database) to decrypt
   * @return
   */
  public static String decrypt(byte[] item) {
    if (item == null) {
      return null;
    }
    return encryptor.decrypt(new String(item));
  }

  public static String decrypt(String item) {
    if (StringUtils.isEmpty(item)) {
      return null;
    }
    return encryptor.decrypt(item);
  }
}
