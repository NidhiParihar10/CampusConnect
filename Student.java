
public class Student {

    private String name;
    private String roll;
    private String course;

    private java.util.HashSet<String> skills = new java.util.HashSet<>();

    public Student(String name, String roll, String course) {
        this.name = name;
        this.roll = roll;
        this.course = course;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getRoll() {
        return roll;
    }

    public String getCourse() {
        return course;
    }

    // Setter methods
    public void setName(String name) {
        this.name = name;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void displayDetails() {
        System.out.println(name + " | " + roll + " | " + course);
    }
    
    public boolean addSkill(String skill) {
        skill = skill.trim().toLowerCase();

        if (skill.isEmpty()) {
            return false;
        }

        return skills.add(skill);
    }

    public boolean hasMatchingSkill(String topic) {
        String requestTopic = topic.trim().toLowerCase();

        for (String skill : skills) {
            if (requestTopic.contains(skill)) {
                return true;
            }
        }

        return false;
    }

    public void displaySkills() {
        System.out.println("Student: " + name);
        System.out.println("Roll Number: " + roll);
        System.out.println("Skills: " + skills);
    }


}
