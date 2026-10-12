
import java.util.Scanner;
import java.util.ArrayList;

public class CampusConnect {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ArrayList<Student> students = StudentDAO.getAllStudents();
        ArrayList<HelpRequest> requests = HelpRequestDAO.getAllRequests();
        Student loggedInStudent = null;

        for (Student s : students) {
            ArrayList<String> savedSkills = SkillDAO.getSkills(s.getRoll());

            for (String savedSkill : savedSkills) {
                s.addSkill(savedSkill);
            }
        }

        int choice = 0;
        do {
            System.out.println("\n===== CAMPUS CONNECT =====");
            System.out.println("1. Register Student");
            System.out.println("2. View Students");
            System.out.println("3. Post Help Request");
            System.out.println("4. View Help Requests");
            System.out.println("5. Search Student");
            System.out.println("6. Resolve Help Request");
            System.out.println("7. Update Help Request");
            System.out.println("8. Delete Help Request");
            System.out.println("9. Search Help Requests by Topic");
            System.out.println("10. Filter Requests by Status");
            System.out.println("11. Sort Requests (Newest First)");
            System.out.println("12. Add Student Skill");
            System.out.println("13. Find Matching Helpers");
            System.out.println("14. Assign Helper");
            System.out.println("15. Student Login");
            System.out.println("16. Student Logout");
            System.out.println("17. Student Dashboard");
            System.out.println("18. Helper Leaderboard");
            System.out.println("19. Accept Help Request");
            System.out.println("20. Reject Help Request");
            System.out.println("21. My Assigned Requests");
            System.out.println("22. Search Requests by Topic");
            System.out.println("23. Filter Requests by Status");
            System.out.println("24. Advanced Search (Topic + Status)");
            System.out.println("25. Exit");

            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input! Enter a number.");
                sc.nextLine();
                continue;
            }

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1: {

                    System.out.print("Enter Student Name: ");
                    String name = sc.nextLine().trim();

                    System.out.print("Enter Roll Number: ");
                    String roll = sc.nextLine().trim();

                    System.out.print("Enter Course: ");
                    String course = sc.nextLine().trim();

                    // Step 1: Validate student details
                    if (!InputValidator.isValidStudent(name, roll, course)) {

                        System.out.println("Registration Failed!");

                        if (!InputValidator.isValidName(name)) {
                            System.out.println("Name cannot be empty!");
                        }

                        if (!InputValidator.isValidRoll(roll)) {
                            System.out.println("Roll Number cannot be empty!");
                        }

                        if (!InputValidator.isValidCourse(course)) {
                            System.out.println("Course cannot be empty!");
                        }

                        break;
                    }

                    // Step 2: Check duplicate roll number
                    boolean duplicate = false;

                    for (Student s : students) {
                        if (s.getRoll().equals(roll)) {
                            duplicate = true;
                            break;
                        }
                    }

                    if (duplicate) {
                        System.out.println("Error: Roll Number Already Registered!");
                        break;
                    }

                    // Step 3: Validate password
                    System.out.print("Create Password: ");
                    String password = sc.nextLine();

                    if (password.length() < 12) {
                        System.out.println(
                                "Password must contain at least 12 characters!");
                        break;
                    }

                    // Step 4: Create Student object
                    Student student = new Student(name, roll, course);

                    // Step 5: Save student in SQLite database
                    if (StudentDAO.registerStudent(student, password)) {

                        students.add(student);

                        System.out.println(
                                "Student Registered Successfully!");

                    } else {

                        System.out.println(
                                "Student Registration Failed!");
                    }

                    break;
                }

                case 2: {
                    if (students.isEmpty()) {
                        System.out.println("No students registered!");
                    } else {
                        System.out.println("\n===== STUDENT LIST =====");

                        for (Student s : students) {
                            s.displayDetails();
                        }

                        System.out.println("Total Students: " + students.size());
                    }
                    break;
                }

                case 3: {

                    // Step 1: Check login
                    if (loggedInStudent == null) {
                        System.out.println("Please login first!");
                        break;
                    }

                    String requestRoll = loggedInStudent.getRoll();

                    // Step 2: Verify registered student
                    boolean registered = false;

                    for (Student s : students) {
                        if (s.getRoll().equals(requestRoll)) {
                            registered = true;
                            break;
                        }
                    }

                    if (!registered) {
                        System.out.println("Student not registered!");
                        break;
                    }

                    // Step 3: Take help request details
                    System.out.print("Enter Help Topic: ");
                    String topic = sc.nextLine().trim();

                    System.out.print("Describe your problem: ");
                    String description = sc.nextLine().trim();

                    // Step 4: Validate topic and description
                    if (!InputValidator.isValidTopic(topic) ||
                            !InputValidator.isValidDescription(description)) {

                        System.out.println("Help Request Failed!");

                        if (!InputValidator.isValidTopic(topic)) {
                            System.out.println("Topic cannot be empty!");
                        }

                        if (!InputValidator.isValidDescription(description)) {
                            System.out.println("Description cannot be empty!");
                        }

                        break;
                    }

                    // Step 5: Save request in SQLite
                    int generatedId = HelpRequestDAO.addRequest(
                            requestRoll, topic, description);

                    if (generatedId > 0) {

                        HelpRequest request = new HelpRequest(
                                generatedId, requestRoll, topic, description);

                        requests.add(request);

                        System.out.println(
                                "Help Request Posted Successfully!");

                        System.out.println(
                                "Your Request ID: " + generatedId);

                    } else {
                        System.out.println(
                                "Failed to Post Help Request!");
                    }

                    break;
                }

                case 4: {
                    if (requests.isEmpty()) {
                        System.out.println("No Help Requests Found!");
                    } else {
                        System.out.println("\n===== HELP REQUESTS =====");

                        for (HelpRequest r : requests) {
                            r.displayRequest();
                        }
                    }
                    break;
                }

                case 5: {
                    System.out.print("Enter Roll Number to Search: ");
                    String searchRoll = sc.nextLine().trim();

                    boolean found = false;

                    for (Student s : students) {
                        if (s.getRoll().equals(searchRoll)) {
                            System.out.println("Student Found:");
                            s.displayDetails();
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        System.out.println("Student Not Found!");
                    }
                    break;
                }

                case 6: {

                    if (loggedInStudent == null) {
                        System.out.println("Please login first!");
                        break;
                    }

                    System.out.print("Enter Request ID to Resolve: ");

                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid Request ID!");
                        sc.nextLine();
                        break;
                    }

                    int resolveId = sc.nextInt();
                    sc.nextLine();

                    HelpRequest resolveTarget = null;

                    for (HelpRequest r : requests) {
                        if (r.getRequestId() == resolveId) {
                            resolveTarget = r;
                            break;
                        }
                    }

                    if (resolveTarget == null) {
                        System.out.println("Request ID Not Found!");
                        break;
                    }

                    String currentRoll = loggedInStudent.getRoll();

                    boolean isOwner = resolveTarget.getRoll().equals(currentRoll);

                    boolean isHelper = currentRoll.equals(resolveTarget.getAssignedHelper());

                    if (!isOwner && !isHelper) {
                        System.out.println(
                                "Permission Denied! You cannot resolve this request.");
                        break;
                    }

                    if (resolveTarget.getStatus().equals("RESOLVED")) {
                        System.out.println("Request is already resolved!");
                        break;
                    }

                    if (HelpRequestDAO.resolveRequest(resolveId)) {
                        resolveTarget.resolveRequest();
                        System.out.println("Request Resolved Successfully!");
                    } else {
                        System.out.println("Failed to Resolve Request!");
                    }

                    break;
                }

                case 7: {
                    System.out.print("Enter Request ID to Update: ");

                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid Request ID!");
                        sc.nextLine();
                        break;
                    }

                    int updateId = sc.nextInt();
                    sc.nextLine();

                    HelpRequest updateTarget = null;

                    for (HelpRequest r : requests) {
                        if (r.getRequestId() == updateId) {
                            updateTarget = r;
                            break;
                        }
                    }

                    if (updateTarget == null) {
                        System.out.println("Request ID Not Found!");
                        break;
                    }

                    if (updateTarget.getStatus().equals("RESOLVED")) {
                        System.out.println("Resolved requests cannot be updated!");
                        break;
                    }

                    System.out.print("Enter Roll Number: ");
                    String ownerRoll = sc.nextLine().trim();

                    if (!updateTarget.getRoll().equals(ownerRoll)) {
                        System.out.println("Roll Number Does Not Match!");
                        break;
                    }

                    System.out.print("Enter New Topic: ");
                    String newTopic = sc.nextLine().trim();

                    System.out.print("Enter New Description: ");
                    String newDescription = sc.nextLine().trim();

                    if (newTopic.isEmpty() || newDescription.isEmpty()) {
                        System.out.println("Topic and description cannot be empty!");
                        break;
                    }

                    if (HelpRequestDAO.updateRequest(
                            updateId, newTopic, newDescription)) {

                        updateTarget.updateRequest(newTopic, newDescription);
                        System.out.println("Request Updated Successfully!");

                    } else {
                        System.out.println("Failed to Update Request!");
                    }

                    break;
                }

                case 8: {

                    if (loggedInStudent == null) {
                        System.out.println("Please login first!");
                        break;
                    }

                    System.out.print("Enter Request ID to Delete: ");

                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid Request ID!");
                        sc.nextLine();
                        break;
                    }

                    int deleteId = sc.nextInt();
                    sc.nextLine();

                    HelpRequest deleteTarget = null;

                    for (HelpRequest r : requests) {
                        if (r.getRequestId() == deleteId) {
                            deleteTarget = r;
                            break;
                        }
                    }

                    if (deleteTarget == null) {
                        System.out.println("Request ID Not Found!");
                        break;
                    }

                    String deleteRoll = loggedInStudent.getRoll();

                    if (!deleteTarget.getRoll().equals(deleteRoll)) {
                        System.out.println("Roll Number Does Not Match!");
                        break;
                    }

                    if (HelpRequestDAO.deleteRequest(deleteId, deleteRoll)) {
                        requests.remove(deleteTarget);
                        System.out.println("Request Deleted Successfully!");
                    } else {
                        System.out.println("Failed to Delete Request!");
                    }

                    break;
                }

                case 9: {
                    System.out.print("Enter Topic to Search: ");
                    String searchTopic = sc.nextLine().trim();

                    if (searchTopic.isEmpty()) {
                        System.out.println("Please enter a topic!");
                        break;
                    }

                    boolean topicFound = false;

                    for (HelpRequest r : requests) {
                        if (r.getTopic().toLowerCase()
                                .contains(searchTopic.toLowerCase())) {

                            r.displayRequest();
                            topicFound = true;
                        }
                    }

                    if (!topicFound) {
                        System.out.println("No Requests Found for This Topic!");
                    }
                    break;
                }

                case 10: {
                    System.out.print("Enter Status (OPEN/RESOLVED): ");
                    String searchStatus = sc.nextLine().trim().toUpperCase();

                    if (!searchStatus.equals("OPEN")
                            && !searchStatus.equals("RESOLVED")) {
                        System.out.println("Invalid Status!");
                        break;
                    }

                    boolean statusFound = false;

                    for (HelpRequest r : requests) {
                        if (r.getStatus().equals(searchStatus)) {
                            r.displayRequest();
                            statusFound = true;
                        }
                    }

                    if (!statusFound) {
                        System.out.println("No Requests Found with This Status!");
                    }
                    break;
                }

                case 11: {
                    if (requests.isEmpty()) {
                        System.out.println("No Help Requests Found!");
                        break;
                    }

                    ArrayList<HelpRequest> sortedRequests = new ArrayList<>(requests);

                    sortedRequests.sort((a, b) -> Integer.compare(b.getRequestId(), a.getRequestId()));

                    System.out.println("\n===== NEWEST REQUESTS FIRST =====");

                    for (HelpRequest r : sortedRequests) {
                        r.displayRequest();
                    }
                    break;
                }

                case 12: {

                    if (loggedInStudent == null) {
                        System.out.println("Please login first!");
                        break;
                    }

                    String skillRoll = loggedInStudent.getRoll();
                    Student selectedStudent = loggedInStudent;

                    System.out.print("Enter Skill: ");
                    String skill = sc.nextLine().trim();

                    if (skill.isEmpty()) {
                        System.out.println("Skill cannot be empty!");
                        break;
                    }

                    if (SkillDAO.addSkill(skillRoll, skill)) {
                        selectedStudent.addSkill(skill);
                        System.out.println("Skill Added Successfully!");
                    } else {
                        System.out.println("Skill could not be saved or already exists!");
                    }

                    selectedStudent.displaySkills();

                    break;
                }

                case 13: {
                    System.out.print("Enter Help Request ID: ");

                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid Request ID!");
                        sc.nextLine();
                        break;
                    }

                    int findId = sc.nextInt();
                    sc.nextLine();

                    HelpRequest selectedRequest = null;

                    for (HelpRequest r : requests) {
                        if (r.getRequestId() == findId) {
                            selectedRequest = r;
                            break;
                        }
                    }

                    if (selectedRequest == null) {
                        System.out.println("Help Request Not Found!");
                        break;
                    }

                    if (selectedRequest.getStatus().equals("RESOLVED")) {
                        System.out.println("This request is already resolved!");
                        break;
                    }

                    System.out.println("\n===== MATCHING HELPERS =====");
                    System.out.println("Topic: " + selectedRequest.getTopic());

                    boolean helperFound = false;

                    for (Student s : students) {
                        if (s.getRoll().equals(selectedRequest.getRoll())) {
                            continue;
                        }

                        if (s.hasMatchingSkill(selectedRequest.getTopic())) {
                            s.displaySkills();
                            System.out.println("-------------------------");
                            helperFound = true;
                        }
                    }

                    if (!helperFound) {
                        System.out.println("No Matching Helpers Found!");
                    }
                    break;
                }

                case 14: {

                    if (loggedInStudent == null) {
                        System.out.println("Please login first!");
                        break;
                    }

                    System.out.print("Enter Help Request ID: ");

                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid Request ID!");
                        sc.nextLine();
                        break;
                    }

                    int assignId = sc.nextInt();
                    sc.nextLine();

                    HelpRequest targetRequest = null;

                    for (HelpRequest r : requests) {
                        if (r.getRequestId() == assignId) {
                            targetRequest = r;
                            break;
                        }
                    }

                    if (targetRequest == null) {
                        System.out.println("Request Not Found!");
                        break;
                    }

                    // Only the request owner can assign a helper
                    if (!targetRequest.getRoll().equals(loggedInStudent.getRoll())) {
                        System.out.println(
                                "Permission Denied! Only the request owner can assign a helper.");
                        break;
                    }

                    if (!targetRequest.getStatus().equals("OPEN")) {
                        System.out.println("Only OPEN requests can be assigned!");
                        break;
                    }

                    System.out.print("Enter Helper Roll Number: ");
                    String helperRoll = sc.nextLine().trim();

                    Student helper = null;

                    for (Student s : students) {
                        if (s.getRoll().equals(helperRoll)) {
                            helper = s;
                            break;
                        }
                    }

                    if (helper == null) {
                        System.out.println("Helper Student Not Found!");
                        break;
                    }

                    if (helperRoll.equals(loggedInStudent.getRoll())) {
                        System.out.println("You cannot assign yourself!");
                        break;
                    }

                    if (!helper.hasMatchingSkill(targetRequest.getTopic())) {
                        System.out.println("Helper does not have a matching skill!");
                        break;
                    }

                    if (HelpRequestDAO.assignHelper(assignId, helperRoll)) {
                        targetRequest.assignHelper(helperRoll);
                        System.out.println("Helper Assigned Successfully!");
                    } else {
                        System.out.println("Failed to Assign Helper!");
                    }

                    break;
                }

                case 15: {
                    if (loggedInStudent != null) {
                        System.out.println("Already logged in!");
                        break;
                    }

                    System.out.print("Enter Roll Number: ");
                    String loginRoll = sc.nextLine().trim();

                    System.out.print("Enter Password: ");
                    String loginPassword = sc.nextLine();

                    if (AuthDAO.login(loginRoll, loginPassword)) {

                        for (Student s : students) {
                            if (s.getRoll().equals(loginRoll)) {
                                loggedInStudent = s;
                                break;
                            }
                        }

                        if (loggedInStudent != null) {
                            System.out.println("Login Successful!");
                            System.out.println(
                                    "Welcome " + loggedInStudent.getName() + "!");
                        } else {
                            System.out.println("Student profile not found!");
                        }

                    } else {
                        System.out.println("Invalid Roll Number or Password!");
                    }

                    break;
                }

                case 16: {
                    if (loggedInStudent == null) {
                        System.out.println("No student is currently logged in!");
                    } else {
                        System.out.println(
                                "Goodbye " + loggedInStudent.getName() + "!");

                        loggedInStudent = null;
                        System.out.println("Logout Successful!");
                    }

                    break;
                }

                case 17: {

                    if (loggedInStudent == null) {
                        System.out.println("Please login first!");
                        break;
                    }

                    StudentDashboardDAO.showDashboard(loggedInStudent);

                    break;
                }

                case 18: {

                    HelperLeaderboardDAO.showLeaderboard();

                    break;
                }

                case 19: {
                    if (loggedInStudent == null) {
                        System.out.println("Please login first!");
                        break;
                    }

                    System.out.print("Enter Request ID to Accept: ");

                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid Request ID!");
                        sc.nextLine();
                        break;
                    }

                    int acceptId = sc.nextInt();
                    sc.nextLine();

                    HelpRequest target = null;

                    for (HelpRequest r : requests) {
                        if (r.getRequestId() == acceptId) {
                            target = r;
                            break;
                        }
                    }

                    if (target == null) {
                        System.out.println("Request Not Found!");
                        break;
                    }

                    if (!loggedInStudent.getRoll().equals(target.getAssignedHelper())) {
                        System.out.println("Permission Denied! You are not the assigned helper.");
                        break;
                    }

                    if (!target.getStatus().equals("ASSIGNED")) {
                        System.out.println("Only ASSIGNED requests can be accepted!");
                        break;
                    }

                    if (HelpRequestDAO.acceptRequest(
                            acceptId, loggedInStudent.getRoll())) {

                        target.acceptRequest();
                        System.out.println("Help Request Accepted Successfully!");

                    } else {
                        System.out.println("Failed to Accept Request!");
                    }

                    break;
                }

                case 20: {
                    if (loggedInStudent == null) {
                        System.out.println("Please login first!");
                        break;
                    }

                    System.out.print("Enter Request ID to Reject: ");

                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid Request ID!");
                        sc.nextLine();
                        break;
                    }

                    int rejectId = sc.nextInt();
                    sc.nextLine();

                    HelpRequest target = null;

                    for (HelpRequest r : requests) {
                        if (r.getRequestId() == rejectId) {
                            target = r;
                            break;
                        }
                    }

                    if (target == null) {
                        System.out.println("Request Not Found!");
                        break;
                    }

                    if (!loggedInStudent.getRoll().equals(target.getAssignedHelper())) {
                        System.out.println("Permission Denied! You are not the assigned helper.");
                        break;
                    }

                    if (!target.getStatus().equals("ASSIGNED")) {
                        System.out.println("Only ASSIGNED requests can be rejected!");
                        break;
                    }

                    if (HelpRequestDAO.rejectRequest(
                            rejectId, loggedInStudent.getRoll())) {

                        target.rejectRequest();
                        System.out.println("Help Request Rejected Successfully!");
                        System.out.println("Request is now OPEN for reassignment.");

                    } else {
                        System.out.println("Failed to Reject Request!");
                    }

                    break;
                }

                case 21: {
                    if (loggedInStudent == null) {
                        System.out.println("Please login first!");
                        break;
                    }

                    System.out.println("\n===== MY ASSIGNED REQUESTS =====");

                    boolean anyAssigned = false;

                    for (HelpRequest r : requests) {
                        if (loggedInStudent.getRoll().equals(r.getAssignedHelper())) {
                            r.displayRequest();
                            anyAssigned = true;
                        }
                    }

                    if (!anyAssigned) {
                        System.out.println("No Assigned Requests Found!");
                    }

                    break;
                }

                case 22: {
                    System.out.print("Enter Topic Keyword: ");
                    String keyword = sc.nextLine().trim();

                    if (keyword.isEmpty()) {
                        System.out.println("Please enter a topic!");
                        break;
                    }

                    ArrayList<HelpRequest> searchResults = HelpRequestDAO.searchByTopic(keyword);

                    System.out.println("\n===== TOPIC SEARCH RESULTS =====");

                    if (searchResults.isEmpty()) {
                        System.out.println("No matching requests found!");
                    } else {
                        for (HelpRequest r : searchResults) {
                            r.displayRequest();
                        }
                    }

                    break;
                }

                case 23: {
                    System.out.print(
                            "Enter Status (OPEN/ASSIGNED/IN_PROGRESS/RESOLVED): ");

                    String status = sc.nextLine().trim().toUpperCase();

                    if (!status.equals("OPEN") &&
                            !status.equals("ASSIGNED") &&
                            !status.equals("IN_PROGRESS") &&
                            !status.equals("RESOLVED")) {

                        System.out.println("Invalid status!");
                        break;
                    }

                    ArrayList<HelpRequest> filteredResults = HelpRequestDAO.filterByStatus(status);

                    System.out.println("\n===== STATUS FILTER RESULTS =====");

                    if (filteredResults.isEmpty()) {
                        System.out.println("No matching requests found!");
                    } else {
                        for (HelpRequest r : filteredResults) {
                            r.displayRequest();
                        }
                    }

                    break;
                }

                case 24: {
                    System.out.print("Enter Topic Keyword: ");
                    String keyword = sc.nextLine().trim();

                    if (keyword.isEmpty()) {
                        System.out.println("Please enter a topic!");
                        break;
                    }

                    System.out.print(
                            "Enter Status (OPEN/ASSIGNED/IN_PROGRESS/RESOLVED): ");

                    String status = sc.nextLine().trim().toUpperCase();

                    if (!status.equals("OPEN") &&
                            !status.equals("ASSIGNED") &&
                            !status.equals("IN_PROGRESS") &&
                            !status.equals("RESOLVED")) {

                        System.out.println("Invalid status!");
                        break;
                    }

                    ArrayList<HelpRequest> results = HelpRequestDAO.searchByTopicAndStatus(keyword, status);

                    System.out.println("\n===== ADVANCED SEARCH RESULTS =====");

                    if (results.isEmpty()) {
                        System.out.println("No matching requests found!");
                    } else {
                        for (HelpRequest r : results) {
                            r.displayRequest();
                        }
                    }

                    break;
                }
                case 25: {
                    System.out.println("Exiting CampusConnect...");
                    break;
                }

                default: {
                    System.out.println("Invalid Choice!");
                    break;
                }
            }

        } while (choice != 25);

        sc.close();
    }
}