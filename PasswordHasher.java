
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher {

    private static final int ITERATIONS = 600000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        PBEKeySpec spec = null;

        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);

            spec = new PBEKeySpec(
                    password.toCharArray(),
                    saltBytes,
                    ITERATIONS,
                    KEY_LENGTH);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException(
                    "Password hashing failed", e);
        } finally {
            if (spec != null) {
                spec.clearPassword();
            }
        }
    }

    public static boolean verifyPassword(
            String password, String salt, String storedHash) {

        try {
            String calculatedHash = hashPassword(password, salt);

            byte[] calculatedBytes = Base64.getDecoder().decode(calculatedHash);

            byte[] storedBytes = Base64.getDecoder().decode(storedHash);

            return MessageDigest.isEqual(
                    calculatedBytes, storedBytes);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
