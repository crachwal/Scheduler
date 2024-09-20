package rachwal;

import java.io.*; // Import for handling input and output
import java.nio.file.*; // Import for handling file paths and reading files
import java.time.LocalTime; // Import for handling local time
import java.time.format.DateTimeFormatter; // Import for formatting and parsing date-time objects
import java.time.format.DateTimeParseException; // Import for handling exceptions during date-time parsing
import java.util.ArrayList; // Import for using ArrayList
import java.util.Arrays; // Import for using Arrays utility methods
import java.util.List; // Import for using List interface
import java.util.Locale; // Import for using Locale
import java.util.stream.Collectors; // Import for using stream operations

public class ScheduleParser {

    // Method to parse courses from a file
    public List<Course> parseCoursesFromFile(String filePath) throws IOException {
        List<Course> courses = new ArrayList<>(); // List to store parsed courses
        Course currentCourse = null; // Variable to hold the current course being parsed
        Section currentSection = null; // Variable to hold the current section being parsed
        Meeting currentMeeting = null; // Variable to hold the current meeting being parsed

        // Formatter to parse time strings
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);

        // Try-with-resources to ensure the reader is closed after use
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line; // Variable to hold each line read from the file
            while ((line = reader.readLine()) != null) { // Read lines until end of file
                if (line.startsWith("Course ID:")) { // Check if the line starts with "Course ID:"
                    if (currentCourse != null) { // If a current course is being parsed
                        if (currentSection != null) { // If a current section is being parsed
                            currentCourse.getSections().add(currentSection); // Add the current section to the current course
                            currentSection = null; // Reset current section
                        }
                        courses.add(currentCourse); // Add the current course to the list of courses
                    }
                    currentCourse = new Course(); // Create a new course object
                    String courseId = line.split(": ")[1].trim(); // Extract the course ID
                    currentCourse.setCourseId(courseId); // Set the course ID
                } else if (line.startsWith("Section ")) { // Check if the line starts with "Section "
                    if (currentSection != null) { // If a current section is being parsed
                        currentCourse.getSections().add(currentSection); // Add the current section to the current course
                    }
                    currentSection = new Section(); // Create a new section object
                    currentSection.setCourse(currentCourse); // Set the reference to the parent course
                    int sectionNumber = Integer.parseInt(line.split(" ")[1].replace(":", "").trim()); // Extract the section number and parse it as an int
                    currentSection.setSectionNumber(sectionNumber); // Set the section number
                } else if (line.trim().startsWith("Title:")) { // Check if the line starts with "Title:"
                    String title = line.split(": ")[1].trim(); // Extract the title
                    currentSection.setTitle(title); // Set the title
                } else if (line.trim().startsWith("Capacity:")) { // Check if the line starts with "Capacity:"
                    int capacity = Integer.parseInt(line.split(": ")[1].trim()); // Extract the capacity
                    currentSection.setCapacity(capacity); // Set the capacity
                } else if (line.trim().startsWith("Enrolled:")) { // Check if the line starts with "Enrolled:"
                    int enrolled = Integer.parseInt(line.split(": ")[1].trim()); // Extract the number of enrolled students
                    currentSection.setEnrolled(enrolled); // Set the number of enrolled students
                } else if (line.trim().startsWith("Available:")) { // Check if the line starts with "Available:"
                    int available = Integer.parseInt(line.split(": ")[1].trim()); // Extract the number of available seats
                    currentSection.setAvailable(available); // Set the number of available seats
                } else if (line.trim().startsWith("Instructional Method:")) { // Check if the line starts with "Instructional Method:"
                    if (currentMeeting != null) { // If a current meeting is being parsed
                        currentSection.addMeeting(currentMeeting); // Add the current meeting to the current section
                    }
                    currentMeeting = new Meeting(); // Create a new meeting object
                    String method = line.split(": ")[1].trim(); // Extract the instructional method
                    currentMeeting.setType(method); // Set the instructional method
                } else if (line.trim().startsWith("Corrected Start Time:")) { // Check if the line starts with "Corrected Start Time:"
                    String startTimeStr = line.split(": ")[1].trim(); // Extract the start time string
                    startTimeStr = startTimeStr.toUpperCase(Locale.US); // Ensure AM/PM is uppercase
                    try {
                        LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter); // Parse the start time
                        currentMeeting.setStartTime(startTime); // Set the start time
                    } catch (DateTimeParseException e) {
                        System.err.println("Failed to parse start time: '" + startTimeStr + "'"); // Print error if parsing fails
                    }
                } else if (line.trim().startsWith("Corrected End Time:")) { // Check if the line starts with "Corrected End Time:"
                    String endTimeStr = line.split(": ")[1].trim(); // Extract the end time string
                    endTimeStr = endTimeStr.toUpperCase(Locale.US); // Ensure AM/PM is uppercase
                    try {
                        LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter); // Parse the end time
                        currentMeeting.setEndTime(endTime); // Set the end time
                    } catch (DateTimeParseException e) {
                        System.err.println("Failed to parse end time: '" + endTimeStr + "'"); // Print error if parsing fails
                    }
                } else if (line.trim().startsWith("Days:")) { // Check if the line starts with "Days:"
                    String daysStr = line.substring(line.indexOf('[') + 1, line.indexOf(']')); // Extract the days string
                    List<Integer> days = Arrays.stream(daysStr.split(",")).map(String::trim).map(Integer::parseInt).collect(Collectors.toList()); // Parse days into a list of integers
                    currentMeeting.setDays(days); // Set the days
                } else if (line.trim().startsWith("Room:")) { // Check if the line starts with "Room:"
                    String room = line.split(": ")[1].trim(); // Extract the room
                    currentMeeting.setRoom(room); // Set the room
                    currentSection.addMeeting(currentMeeting); // Add the current meeting to the current section
                    currentMeeting = null; // Reset current meeting
                } else if (line.trim().startsWith("Faculty ID:")) { // Check if the line starts with "Faculty ID:"
                    Faculty faculty = new Faculty(); // Create a new faculty object
                    faculty.setFacultyId(line.split(": ")[1].trim()); // Extract and set the faculty ID
                    line = reader.readLine(); // Read the next line for the faculty name
                    faculty.setFacultyName(line.split(": ")[1].trim()); // Extract and set the faculty name
                    currentSection.addFaculty(faculty); // Add the faculty to the current section
                }
            }
            // Add the last section and course
            if (currentSection != null) {
                currentCourse.getSections().add(currentSection); // Add the last section to the current course
            }
            if (currentCourse != null) {
                courses.add(currentCourse); // Add the last course to the list of courses
            }
        }
        return courses; // Return the list of parsed courses
    }

    public static void main(String[] args) {
        String filePath = "C:\\Users\\Charles\\Desktop\\winter.txt"; // Replace with the actual file path

        // Create a Path object from the filePath
        Path path = Paths.get(filePath);

        // Check if the file exists and is readable
        if (Files.exists(path) && Files.isReadable(path)) {
        	System.out.println("it gets here");
            ScheduleParser parser = new ScheduleParser(); // Create a new ScheduleParser object
            try {
                List<Course> courses = parser.parseCoursesFromFile(filePath); // Parse courses from the file
                courses.forEach(course -> { // Loop through each course
                    System.out.println("Course ID: " + course.getCourseId()); // Print the course ID
                    course.getSections().forEach(section -> { // Loop through each section in the course
                        System.out.println("  Section Number: " + section.getSectionNumber()); // Print the section number
                        System.out.println("    Title: " + section.getTitle()); // Print the section title
                        section.getMeetings().forEach(meeting -> { // Loop through each meeting in the section
                            System.out.println("      Meeting Type: " + meeting.getType()); // Print the meeting type
                            System.out.println("      Start Time: " + meeting.getStartTime()); // Print the start time
                            System.out.println("      End Time: " + meeting.getEndTime()); // Print the end time
                            System.out.println("      Days: " + meeting.getDays()); // Print the days
                            System.out.println("      Room: " + meeting.getRoom()); // Print the room
                        });
                        section.getFaculties().forEach(faculty -> { // Loop through each faculty in the section
                            System.out.println("      Faculty ID: " + faculty.getFacultyId()); // Print the faculty ID
                            System.out.println("      Faculty Name: " + faculty.getFacultyName()); // Print the faculty name
                        });
                    });
                });
            } catch (IOException e) {
                System.err.println("An error occurred while reading from the file: " + e.getMessage()); // Print error message if an exception occurs
            }
        } else {
            System.err.println("File does not exist or is not readable: " + filePath); // Print error message if file doesn't exist or isn't readable
        }
    }

}
