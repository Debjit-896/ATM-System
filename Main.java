import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/atm_db";
        String user = "root";
        String pass = "root";

        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            Scanner sc = new Scanner(System.in);
            ATM atm = new ATM(conn);

            atm.checkPin(sc);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}