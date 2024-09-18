package rachwal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Section {
    private Course course; // Reference to the parent Course object
    private int sectionNumber;
 
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private int capacity;
    private int enrolled;
    private int available;
    private List<Meeting> meetings;
    private List<Faculty> faculties;
    private List<Integer> days;

    public Section() {
        meetings = new ArrayList<>();
        faculties = new ArrayList<>();
    }

    // Getters and setters
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(int sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(int enrolled) {
        this.enrolled = enrolled;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }

    public List<Faculty> getFaculties() {
        return faculties;
    }

    public void setFaculties(List<Faculty> faculties) {
        this.faculties = faculties;
    }

    public void addMeeting(Meeting meeting) {
        this.meetings.add(meeting);
    }

    public void addFaculty(Faculty faculty) {
        this.faculties.add(faculty);
    }

    public List<Integer> getDays() {
        return days;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }
}
