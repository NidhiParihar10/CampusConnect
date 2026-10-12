
public class TestPassword {

    public static void main(String[] args) {

        String password = "DemoPassword@123";

        String salt = PasswordHasher.generateSalt();

        String hash = PasswordHasher.hashPassword(password, salt);

        System.out.println("Salt: " + salt);
        System.out.println("Hash: " + hash);

        boolean correct = PasswordHasher.verifyPassword(
                "DemoPassword@123", salt, hash);

        boolean incorrect = PasswordHasher.verifyPassword(
                "WrongPassword", salt, hash);

        System.out.println("Correct Password: " + correct);
        System.out.println("Wrong Password: " + incorrect);
    }
}
