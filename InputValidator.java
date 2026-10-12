
public class InputValidator {

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    public static boolean isValidRoll(String roll) {
        return roll != null && !roll.trim().isEmpty();
    }

    public static boolean isValidCourse(String course) {
        return course != null && !course.trim().isEmpty();
    }

    public static boolean isValidTopic(String topic) {
        return topic != null && !topic.trim().isEmpty();
    }

    public static boolean isValidDescription(String description) {
        return description != null
                && !description.trim().isEmpty();
    }

    public static boolean isValidStudent(
            String name, String roll, String course) {

        return isValidName(name)
                && isValidRoll(roll)
                && isValidCourse(course);
    }

    public static void validateTopic(String topic)
            throws InvalidInputException {

        if (!isValidTopic(topic)) {
            throw new InvalidInputException(
                    "Topic cannot be empty!");
        }
    }

}
