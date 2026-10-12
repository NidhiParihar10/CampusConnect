
public class HelpRequest {

    private int requestId;
    private String roll;
    private String topic;
    private String description;
    private String status;
    private String assignedHelper;

    // Constructor for new requests
    public HelpRequest(int requestId, String roll,
            String topic, String description) {

        this.requestId = requestId;
        this.roll = roll;
        this.topic = topic;
        this.description = description;
        this.status = "OPEN";
        this.assignedHelper = null;
    }

    // Constructor for loading saved requests
    public HelpRequest(int requestId, String roll,
            String topic, String description,
            String status) {

        this(requestId, roll, topic, description);
        this.status = status;
    }

    // Constructor for loading assigned helpers from database
    public HelpRequest(int requestId, String roll,
            String topic, String description,
            String status, String assignedHelper) {

        this(requestId, roll, topic, description, status);
        this.assignedHelper = assignedHelper;
    }

    // Getter methods
    public int getRequestId() {
        return requestId;
    }

    public String getRoll() {
        return roll;
    }

    public String getTopic() {
        return topic;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getAssignedHelper() {
        return assignedHelper;
    }

    // Resolve an existing request
    public void resolveRequest() {
        this.status = "RESOLVED";
    }

    // Update topic and description
    public void updateRequest(String newTopic, String newDescription) {
        this.topic = newTopic;
        this.description = newDescription;
    }

    // Assign a helper
    public void assignHelper(String helperRoll) {
        this.assignedHelper = helperRoll;
        this.status = "ASSIGNED";
    }

    // Display request details
    public void displayRequest() {

        System.out.println("Request ID: " + requestId);
        System.out.println("Roll Number: " + roll);
        System.out.println("Topic: " + topic);
        System.out.println("Description: " + description);
        System.out.println("Status: " + status);

        if (assignedHelper != null) {
            System.out.println("Assigned Helper Roll: " + assignedHelper);
        }

        System.out.println("--------------------------");
    }

    public void acceptRequest() {

        if (this.status.equals("ASSIGNED")) {
            this.status = "IN_PROGRESS";
        }

    }

    public void rejectRequest() {

        if (this.status.equals("ASSIGNED")) {
            this.status = "OPEN";
            this.assignedHelper = null;
        }

    }

}
