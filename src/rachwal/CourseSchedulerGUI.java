package rachwal;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseSchedulerGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextArea textArea;
    private JButton openButton;
    private JButton showCombinationsButton;
    private JButton applyFilterButton;
    private JTextField earliestStartTimeField;
    private JTextField latestEndTimeField;
    private JCheckBox availableSeatsCheckBox;
    private JTable scheduleTable;
    private Map<String, Color> courseColors;
    private List<List<Section>> allCombinations;
    
    ///////////////////////////////////////////
    private void customizeTableHeader() {
        JTableHeader header = scheduleTable.getTableHeader();
        header.setBackground(new Color(150, 150, 150)); // Dark gray background
        header.setForeground(Color.WHITE); // White text
        header.setFont(new Font("Dialog", Font.BOLD, 14)); // Bold font
    }

    
    @SuppressWarnings("serial")
	private class ScheduleTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == 0) { // Time column styling
                setBackground(new Color(220, 220, 220)); // Light gray for time column
                setHorizontalAlignment(CENTER);
            } else if (value instanceof Section) {
                Section section = (Section) value;
                setBackground(courseColors.getOrDefault(section.getCourse().getCourseId(), Color.LIGHT_GRAY));
                setForeground(Color.BLACK);
                setText(section.getTitle() + " (" + section.getSectionNumber() + ")");
            } else {
                setBackground(Color.WHITE); // White background for empty cells
                setForeground(Color.BLACK);
                setText((value == null) ? "" : value.toString());
            }
            setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5)); // Padding in cells
            return this;
        }
    }

    
    private void customizeTableGrid() {
        scheduleTable.setGridColor(new Color(200, 200, 200)); // Light gray grid lines
        scheduleTable.setShowGrid(true);
        scheduleTable.setShowVerticalLines(true);
        scheduleTable.setShowHorizontalLines(true);
        scheduleTable.setRowHeight(30); // Increase row height for better readability
    }

    
 
    
    
    
////////////////////////////////////////////////
    
    
    
    
    
    
    
    

    public CourseSchedulerGUI() {
        createUI();
        courseColors = new HashMap<>();
        allCombinations = new ArrayList<>();
    }



    
    
    
    
    private void createUI() {
        setTitle("Course Scheduler");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        textArea = new JTextArea(20, 70);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        openButton = new JButton("Open File");
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    parseFile(selectedFile.getAbsolutePath());
                }
            }
        });

        showCombinationsButton = new JButton("Show Combinations");
        showCombinationsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateAndShowCombinations();
            }
        });

        applyFilterButton = new JButton("Apply Filter");
        applyFilterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyFilters();
            }
        });

        earliestStartTimeField = new JTextField(5);
        latestEndTimeField = new JTextField(5);
        availableSeatsCheckBox = new JCheckBox("Only show sections with available seats");

        JPanel panel = new JPanel();
        panel.add(new JLabel("Earliest Start Time (hh:mm):"));
        panel.add(earliestStartTimeField);
        panel.add(new JLabel("Latest End Time (hh:mm):"));
        panel.add(latestEndTimeField);
        panel.add(availableSeatsCheckBox);
        panel.add(openButton);
        panel.add(showCombinationsButton);
        panel.add(applyFilterButton);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        initializeScheduleTable();
    }

    
    
    
    private void initializeScheduleTable() {
        String[] columnNames = {"Time", "Mon", "Tue", "Wed", "Thu", "Fri"};
        Object[][] data = new Object[28][6]; // Assuming time slots from 8:00 AM to 9:30 PM in half-hour intervals
        String[] times = {"8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
                          "12:00 PM", "12:30 PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM",
                          "4:00 PM", "4:30 PM", "5:00 PM", "5:30 PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM",
                          "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM"};
        for (int i = 0; i < times.length; i++) {
            data[i][0] = times[i];
        }
        scheduleTable = new JTable(data, columnNames);
        scheduleTable.setDefaultRenderer(Object.class, new ScheduleTableCellRenderer());
        customizeTableHeader();
        customizeTableGrid();
        scheduleTable.setRowHeight(20); // Set row height to a smaller value to fit more rows on screen

        JScrollPane scheduleScrollPane = new JScrollPane(scheduleTable);
        add(scheduleScrollPane, BorderLayout.SOUTH);
    }

    
    
    
    
    
    
    
    
    
    
    

    private void parseFile(String filePath) {
        ScheduleParser parser = new ScheduleParser();
        try {
            List<Course> courses = parser.parseCoursesFromFile(filePath);
            for (Course course : courses) {
                textArea.append("Course ID: " + course.getCourseId() + "\n");
                for (Section section : course.getSections()) {
                    textArea.append("  Section Number: " + section.getSectionNumber() + "\n");
                    textArea.append("    Title: " + section.getTitle() + "\n");
                    textArea.append("    Location: " + section.getLocation() + "\n");
                    textArea.append("    Capacity: " + section.getCapacity() + ", Enrolled: " + section.getEnrolled() + ", Available: " + section.getAvailable() + "\n");
                    for (Meeting meeting : section.getMeetings()) {
                        textArea.append("      Meeting Type: " + meeting.getType() + "\n");
                        textArea.append("      Start Time: " + meeting.getStartTime() + "\n");
                        textArea.append("      End Time: " + meeting.getEndTime() + "\n");
                        textArea.append("      Days: " + meeting.getDays() + "\n");
                        textArea.append("      Room: " + meeting.getRoom() + "\n");
                    }
                    for (Faculty faculty : section.getFaculties()) {
                        textArea.append("      Faculty: " + faculty.getFacultyName() + "\n");
                    }
                    textArea.append("\n");
                }
                textArea.append("------\n\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void generateAndShowCombinations() {
        ScheduleParser parser = new ScheduleParser();
        try {
            String filePath = "C:\\Users\\Charles\\Desktop\\winter.txt"; // Replace with the actual file path
            List<Course> courses = parser.parseCoursesFromFile(filePath);
            ScheduleCombinations scheduleCombinations = new ScheduleCombinations(courses);

            allCombinations = scheduleCombinations.generateCombinations();

            // Assign a unique color to each course
            assignColorsToCourses(courses);

            // Show combinations in a new window
            JFrame combinationsFrame = new JFrame("Schedule Combinations");
            combinationsFrame.setSize(800, 600);
            combinationsFrame.setLayout(new BorderLayout());
            DefaultListModel<String> listModel = new DefaultListModel<>();
            JList<String> list = new JList<>(listModel);

            for (List<Section> combination : allCombinations) {
                StringBuilder sb = new StringBuilder();
                for (Section section : combination) {
                    sb.append("Course ID: ").append(section.getCourse().getCourseId())
                      .append(", Section Number: ").append(section.getSectionNumber())
                      .append(", Title: ").append(section.getTitle()).append("\n");
                }
                listModel.addElement(sb.toString());
            }

            list.addListSelectionListener(event -> {
                if (!event.getValueIsAdjusting()) {
                    int selectedIndex = list.getSelectedIndex();
                    if (selectedIndex != -1) {
                        List<Section> selectedCombination = allCombinations.get(selectedIndex);
                        displaySchedule(selectedCombination);
                    }
                }
            });

            JScrollPane listScrollPane = new JScrollPane(list);
            combinationsFrame.add(listScrollPane, BorderLayout.CENTER);
            combinationsFrame.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        List<List<Section>> filteredCombinations = new ArrayList<>();
        LocalTime earliestStartTime = null;
        LocalTime latestEndTime = null;
        boolean filterAvailableSeats = availableSeatsCheckBox.isSelected();

        try {
            if (!earliestStartTimeField.getText().isEmpty()) {
                earliestStartTime = LocalTime.parse(earliestStartTimeField.getText());
            }
            if (!latestEndTimeField.getText().isEmpty()) {
                latestEndTime = LocalTime.parse(latestEndTimeField.getText());
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Please use hh:mm format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Applying filters:");
        System.out.println("Earliest Start Time: " + earliestStartTime);
        System.out.println("Latest End Time: " + latestEndTime);
        System.out.println("Filter Available Seats: " + filterAvailableSeats);

        for (List<Section> combination : allCombinations) {
            System.out.println("Checking combination:");
            for (Section section : combination) {
                System.out.println("  Section Number: " + section.getSectionNumber() + ", Available: " + section.getAvailable());
            }
            if (meetsTimeAndSeatRestrictions(combination, earliestStartTime, latestEndTime, filterAvailableSeats)) {
                filteredCombinations.add(combination);
            }
        }

        System.out.println("Filtered combinations count: " + filteredCombinations.size());
        displayFilteredCombinations(filteredCombinations);
    }

    private boolean meetsTimeAndSeatRestrictions(List<Section> combination, LocalTime earliestStartTime, LocalTime latestEndTime, boolean filterAvailableSeats) {
        for (Section section : combination) {
            if (filterAvailableSeats && section.getAvailable() <= 0) {
                System.out.println("Section " + section.getSectionNumber() + " does not have available seats.");
                return false;
            }
            for (Meeting meeting : section.getMeetings()) {
                if (earliestStartTime != null && meeting.getStartTime().isBefore(earliestStartTime)) {
                    System.out.println("Section " + section.getSectionNumber() + " has a meeting starting before the earliest start time.");
                    return false;
                }
                if (latestEndTime != null && meeting.getEndTime().isAfter(latestEndTime)) {
                    System.out.println("Section " + section.getSectionNumber() + " has a meeting ending after the latest end time.");
                    return false;
                }
            }
        }
        return true;
    }

    private void displayFilteredCombinations(List<List<Section>> combinations) {
        JFrame combinationsFrame = new JFrame("Filtered Schedule Combinations");
        combinationsFrame.setSize(800, 600);
        combinationsFrame.setLayout(new BorderLayout());
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);

        for (List<Section> combination : combinations) {
            StringBuilder sb = new StringBuilder();
            for (Section section : combination) {
                sb.append("Course ID: ").append(section.getCourse().getCourseId())
                  .append(", Section Number: ").append(section.getSectionNumber())
                  .append(", Title: ").append(section.getTitle()).append("\n");
            }
            listModel.addElement(sb.toString());
        }

        list.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex != -1) {
                    List<Section> selectedCombination = combinations.get(selectedIndex);
                    displaySchedule(selectedCombination);
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(list);
        combinationsFrame.add(listScrollPane, BorderLayout.CENTER);
        combinationsFrame.setVisible(true);
    }

    private void displaySchedule(List<Section> combination) {
        // Clear the schedule table
        for (int i = 0; i < scheduleTable.getRowCount(); i++) {
            for (int j = 1; j < scheduleTable.getColumnCount(); j++) {
                scheduleTable.setValueAt("", i, j);
            }
        }

        // Fill the schedule table based on the selected combination
        for (Section section : combination) {
            for (Meeting meeting : section.getMeetings()) {
                int startTimeIndex = timeToIndex(meeting.getStartTime());
                int endTimeIndex = timeToIndex(meeting.getEndTime());
                for (int day : meeting.getDays()) {
                    for (int i = startTimeIndex; i < endTimeIndex; i++) {
                        scheduleTable.setValueAt(section, i, day);
                    }
                }
            }
        }
    }

    private int timeToIndex(LocalTime time) {
        int hour = time.getHour();
        int minute = time.getMinute();
        return (hour - 8) * 2 + (minute == 0 ? 0 : 1);
    }

    private void assignColorsToCourses(List<Course> courses) {
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.PINK};
        int colorIndex = 0;

        for (Course course : courses) {
            if (colorIndex >= colors.length) {
                colorIndex = 0; // Reuse colors if we run out
            }
            courseColors.put(course.getCourseId(), colors[colorIndex++]);
        }
    }

    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CourseSchedulerGUI ex = new CourseSchedulerGUI();
                ex.setVisible(true);
            }
        });
    }
}
