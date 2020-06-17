package com.repetentia.utils.encryption;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

import com.repetentia.utils.common.StringUtils;

public class StringEncryptor {
	private static PooledPBEStringEncryptor encryptor = null;
	private static boolean isLicensed = false;
	private static String license = null;

	public static PooledPBEStringEncryptor instance() {
		if (encryptor == null) {
			if (license == null && !isLicensed) {
				license = sha256(StringEncryptor.class);
			}
			newInstance(license);
		}
		return encryptor;
	}

	public static String sha256(Class<?> clazz) {
		PasswordEncryptor enc = new StrongPasswordEncryptor();
		return enc.encryptPassword(clazz.getCanonicalName());
	}

	public static PooledPBEStringEncryptor newInstance(String license) {
		if (StringUtils.isEmpty(license))
			throw new EncryptionOperationNotPossibleException("No Proper License Provided!!!");
		encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
		config.setKeyObtentionIterations("1000");
		config.setPoolSize(1);
		config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
		config.setStringOutputType("hexadecimal");
		encryptor.setPassword(license);
		encryptor.setConfig(config);
		return encryptor;
	}

	public static String encrypt(String message) {
		String encryptedMessage = StringEncryptor.instance().encrypt(message).toLowerCase();
		return encryptedMessage;
	}

	public static String decrypt(String encryptedMessage) {
		String message = StringEncryptor.instance().decrypt(encryptedMessage);
		return message;
	}

	public static void main(String[] args) {
//		enc.checkPassword(plainPassword, encryptedPassword)
//		PooledPBEStringEncryptor encryptor = StringEncryptor.instance("test1234");
//		String encrypted = encryptor.encrypt("asdfasdfasdfajskdl;");
//		System.out.println(encrypted);
//		String pre = "CA1162D3443FD80EB8A96E1345A04E93B01A0FD5A21C483E56C193FE7CFE9800F4D352B6225B401A8911C80120F3F3B91A4537612B80DC8AC9F88DD1FC4237AF";
//		String dec = encryptor.decrypt(encrypted);
//		String prede = encryptor.decrypt(pre);
//		System.out.println(dec);
//		System.out.println(prede);

	}
}