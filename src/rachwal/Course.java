package rachwal;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseId;
    private List<Section> sections;

    public Course() {
        sections = new ArrayList<>();
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }
}
