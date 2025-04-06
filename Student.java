import java.sql.*;
import java.util.*;
import java.util.Scanner;

class Student {
    private int studentID;
    private String name;
    private String department;
    private double marks;

    public Student(int studentID, String name, String department, double marks) {
        this.studentID = studentID;
        this.name = name;
        this.department = department;
        this.marks = marks;
    }

    public int getStudentID() { return studentID; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getMarks() { return marks; }

    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setMarks(double marks) { this.marks = marks; }
}

class StudentController {
    private Connection conn;

    public StudentController() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/studentdb", "root", "your_password");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    public void addStudent(Student student) {
        String query = "INSERT INTO students VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, student.getStudentID());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getDepartment());
            stmt.setDouble(4, student.getMarks());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add failed: " + e.getMessage());
        }
    }

    public void updateStudent(Student student) {
        String query = "UPDATE students SET name=?, department=?, marks=? WHERE studentID=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getDepartment());
            stmt.setDouble(3, student.getMarks());
            stmt.setInt(4, student.getStudentID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    public void deleteStudent(int studentID) {
        String query = "DELETE FROM students WHERE studentID=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    public Student getStudent(int studentID) {
        String query = "SELECT * FROM students WHERE studentID=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Student(rs.getInt("studentID"), rs.getString("name"),
                        rs.getString("department"), rs.getDouble("marks"));
            }
        } catch (SQLException e) {
            System.out.println("Fetch failed: " + e.getMessage());
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String query = "SELECT * FROM students";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                list.add(new Student(rs.getInt("studentID"), rs.getString("name"),
                        rs.getString("department"), rs.getDouble("marks")));
            }
        } catch (SQLException e) {
            System.out.println("Fetch all failed: " + e.getMessage());
        }
        return list;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentController controller = new StudentController();

        while (true) {
            System.out.println("\n1. Add Student\n2. Update Student\n3. Delete Student\n4. View One\n5. View All\n6. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("ID: "); int id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Name: "); String name = sc.nextLine();
                    System.out.print("Dept: "); String dept = sc.nextLine();
                    System.out.print("Marks: "); double marks = sc.nextDouble();
                    controller.addStudent(new Student(id, name, dept, marks));
                    break;

                case 2:
                    System.out.print("ID: "); id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("New Name: "); name = sc.nextLine();
                    System.out.print("New Dept: "); dept = sc.nextLine();
                    System.out.print("New Marks: "); marks = sc.nextDouble();
                    controller.updateStudent(new Student(id, name, dept, marks));
                    break;

                case 3:
                    System.out.print("ID: "); id = sc.nextInt();
                    controller.deleteStudent(id);
                    break;

                case 4:
                    System.out.print("ID: "); id = sc.nextInt();
                    Student s = controller.getStudent(id);
                    if (s != null)
                        System.out.println(s.getStudentID() + " | " + s.getName() + " | " + s.getDepartment() + " | " + s.getMarks());
                    else
                        System.out.println("Student not found.");
                    break;

                case 5:
                    List<Student> all = controller.getAllStudents();
                    for (Student stu : all)
                        System.out.println(stu.getStudentID() + " | " + stu.getName() + " | " + stu.getDepartment() + " | " + stu.getMarks());
                    break;

                case 6:
                    System.exit(0);

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
