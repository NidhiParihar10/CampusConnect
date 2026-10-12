
public class ExceptionDemo {

    public static void main(String[] args) {

        try {
            InputValidator.validateTopic("   ");

            System.out.println("Valid topic!");

        } catch (InvalidInputException e) {

            System.out.println(
                    "Validation Error: " + e.getMessage());
        }

        System.out.println("Program continues...");
    }
}
