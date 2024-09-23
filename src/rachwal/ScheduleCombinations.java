package rachwal;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleCombinations {
    private List<Course> courses;

    public ScheduleCombinations(List<Course> courses) {
        this.courses = courses;
    }

    public List<List<Section>> generateCombinations() {
        List<List<Section>> validCombinations = new ArrayList<>();
        generateCombinationsHelper(courses, 0, new ArrayList<>(), validCombinations);
        return validCombinations;
    }

    private void generateCombinationsHelper(List<Course> courses, int courseIndex, List<Section> currentCombination, List<List<Section>> validCombinations) {
        if (courseIndex == courses.size()) {
            if (isValidCombination(currentCombination)) {
                validCombinations.add(new ArrayList<>(currentCombination));
            }
            return;
        }

        Course currentCourse = courses.get(courseIndex);
        for (Section section : currentCourse.getSections()) {
            currentCombination.add(section);
            generateCombinationsHelper(courses, courseIndex + 1, currentCombination, validCombinations);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }

    private boolean isValidCombination(List<Section> combination) {
        for (int i = 0; i < combination.size(); i++) {
            Section section1 = combination.get(i);
            for (Meeting meeting1 : section1.getMeetings()) {
                for (int j = i + 1; j < combination.size(); j++) {
                    Section section2 = combination.get(j);
                    for (Meeting meeting2 : section2.getMeetings()) {
                        if (meetingsOverlap(meeting1, meeting2)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean meetingsOverlap(Meeting meeting1, Meeting meeting2) {
        for (int day1 : meeting1.getDays()) {
            for (int day2 : meeting2.getDays()) {
                if (day1 == day2) {
                    if (timesOverlap(meeting1.getStartTime(), meeting1.getEndTime(), meeting2.getStartTime(), meeting2.getEndTime())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean timesOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
