import java.sql.*;
import java.util.Scanner;
import java.util.InputMismatchException;

public class UserDatabaseProgram {

    public static void main(String[] args) {
        createTable();
        while (true) {
            System.out.println("\n1. Insert Data");
            System.out.println("2. Display All Records");
            System.out.println("3. Exit");

            System.out.print("Enter your choice (1/2/3): ");
            int choice = getIntInput();

            switch (choice) {
                case 1:
                    insertData();
                    break;
                case 2:
                    displayData();
                    break;
                case 3:
                    System.out.println("Exiting program. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    private static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:user_data.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static void createTable() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS user_data (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL," +
                            "age INTEGER," +
                            "email TEXT," +
                            "CHECK (age >= 18)" +
                            ")"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertData() {
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO user_data (name, age, email) VALUES (?, ?, ?)")) {

            System.out.print("Enter your name: ");
            String name = getStringInput();

            System.out.print("Enter your age: ");
            int age = getIntInput();

            // Validation: age should be between 18 and 99
            if (!(18 <= age && age <= 99)) {
                System.out.println("Invalid age. Please enter an age between 18 and 99.");
                return;
            }

            System.out.print("Enter your email: ");
            String email = getStringInput();

            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, email);

            pstmt.executeUpdate();
            System.out.println("Data successfully inserted!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayData() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM user_data");

            if (!rs.isBeforeFirst()) {
                System.out.println("No records found.");
            } else {
                System.out.println("\nID\tName\tAge\tEmail");
                System.out.println("--------------------------------------");
                while (rs.next()) {
                    System.out.printf("%d\t%s\t%d\t%s\n",
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("email")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getIntInput() {
        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a valid integer: ");
            }
        }
    }

    private static String getStringInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
