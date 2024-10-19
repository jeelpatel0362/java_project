package Practical_exam;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Person {
    private String name;
    private int age;
    private String gender;

    public Person(String name, int age, String gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public void displayDetails() {
        System.out.println("Name: " + name + ", Age: " + age + ", Gender: " + gender);
    }
}

class Student extends Person {
    private static int totalStudents = 0;
    private final String studentID;
    private String course;
    private int[] marks;

    public Student(String name, int age, String gender, String studentID, String course, int[] marks) {
        super(name, age, gender);
        this.studentID = studentID;
        this.course = course;
        this.marks = marks;
        totalStudents++;
    }

    public static int getTotalStudents() {
        return totalStudents;
    }

    public int calculateTotalMarks() {
        int total = 0;
        for (int mark : marks) {
            total += mark;
        }
        return total;
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Student ID: " + studentID + ", Course: " + course + ", Total Marks: " + calculateTotalMarks());
    }
}

public class StudentManagement {
    private static List<Student> students = new ArrayList<>();
    private static Thread averageThread;
    private static Thread monitorThread;

    public static void main(String[] args) {
        loadStudentsFromFile("students.txt");

        int[] marks1 = {85, 90, 78};
        Student student1 = new Student("Alice", 20, "Female", "S001", "Computer Science", marks1);
        int[] marks2 = {95, 88, 91};
        Student student2 = new Student("Bob", 22, "Male", "S002", "Mathematics", marks2);

        students.add(student1);
        students.add(student2);

        for (Student student : students) {
            student.displayDetails();
        }

        calculateHighLowScores();

        averageThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    calculateClassAverage();
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        });

        monitorThread = new Thread(() -> {
            int previousCount = 0;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if (Student.getTotalStudents() != previousCount) {
                        previousCount = Student.getTotalStudents();
                        System.out.println("Total Students: " + previousCount);
                    }
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        });

        averageThread.start();
        monitorThread.start();

        // Simulate some other logic
        try {
            Thread.sleep(30000); // Run for 30 seconds for demonstration
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Request to stop the threads
        averageThread.interrupt();
        monitorThread.interrupt();

        try {
            averageThread.join(); // Wait for the average thread to finish
            monitorThread.join(); // Wait for the monitor thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        saveStudentsToFile("students.txt");
    }

    private static void calculateHighLowScores() {
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return;
        }

        int highestScore = Integer.MIN_VALUE;
        int lowestScore = Integer.MAX_VALUE;

        for (Student student : students) {
            int totalMarks = student.calculateTotalMarks();
            if (totalMarks > highestScore) {
                highestScore = totalMarks;
            }
            if (totalMarks < lowestScore) {
                lowestScore = totalMarks;
            }
        }

        System.out.println("Highest Score: " + highestScore + ", Lowest Score: " + lowestScore);
    }

    private static void calculateClassAverage() {
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return;
        }

        int totalMarks = 0;
        for (Student student : students) {
            totalMarks += student.calculateTotalMarks();
        }
        double average = (double) totalMarks / students.size();
        System.out.printf("Class Average: %.2f%n", average);
    }

    private static void saveStudentsToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Student student : students) {
                writer.write(student.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadStudentsFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Assuming a CSV format for student data, parse it here
                String[] parts = line.split(",");
                String name = parts[0];
                int age = Integer.parseInt(parts[1]);
                String gender = parts[2];
                String studentID = parts[3];
                String course = parts[4];
                String[] marksString = parts[5].split(";");
                int[] marks = new int[marksString.length];
                for (int i = 0; i < marksString.length; i++) {
                    marks[i] = Integer.parseInt(marksString[i]);
                }
                Student student = new Student(name, age, gender, studentID, course, marks);
                students.add(student);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

