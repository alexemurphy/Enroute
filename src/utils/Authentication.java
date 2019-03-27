package utils;

import main.db.DBHelper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class Authentication {

	private static final Random RANDOM = new SecureRandom();
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	private static MessageDigest digest;

	static {
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Validate existing user using username and passcode combination
	 *
	 * @param user Username provided - email address
	 * @param pass Password provided
	 * @return True if authenticated
	 */
	public static Boolean authenticateWithPass(String user, String pass) {
		PreparedStatement preparedStatement;
		ResultSet results;
		Connection connection;
		try {
			connection = DBHelper.instance().getConnection();
			preparedStatement = connection.prepareStatement("SELECT PASSWORD,SALT FROM USERS WHERE EMAIL = ?;");
			preparedStatement.setString(1, user);
			results = preparedStatement.executeQuery();
			results.next();
			String hashedPass = results.getString("PASSWORD");
			String salt = results.getString("SALT");
			return checkLogin(pass, hashedPass, salt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public static String genNewSessionKey() {
		StringBuilder sessionKey = new StringBuilder();
		Random random = new Random();
		int limit = random.nextInt(100);
		for (int i = 0; i < limit; i++) {
			sessionKey.append(random.nextInt());
		}
		return sessionKey.toString();
	}


	public static String[] generateHash(String plainTextPassword) {
		byte[] salt = new byte[32];
		RANDOM.nextBytes(salt);
		byte[] bytes = generateBytes(salt, plainTextPassword.getBytes(CHARSET));

		return new String[]{
				new String(Base64.getEncoder().encode(bytes), CHARSET),
				new String(Base64.getEncoder().encode(salt), CHARSET)
		};
	}


	private static boolean checkLogin(String plainTextPassword, String hashedPassword, String salt) {
		byte[] expectedBytes = Base64.getDecoder().decode(hashedPassword.getBytes(CHARSET));
		byte[] saltBytes = Base64.getDecoder().decode(salt.getBytes(CHARSET));

		byte[] actualBytes = generateBytes(saltBytes, plainTextPassword.getBytes(CHARSET));

		return Arrays.equals(expectedBytes, actualBytes);
	}


	private static byte[] generateBytes(byte[] salt, byte[] passwordBytes) {
		byte[] newBytes = new byte[salt.length + passwordBytes.length];
		System.arraycopy(salt, 0, newBytes, 0, salt.length);
		System.arraycopy(passwordBytes, 0, newBytes, salt.length, passwordBytes.length);

		return digest.digest(newBytes);
	}
}