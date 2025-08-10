import java.sql.*;
import java.util.Scanner;

class ATM {
    float Bal = 20179.78f;
    int Pin = 7820;
    Connection connection;

    public ATM(Connection connection) {
        this.connection = connection;
    }

    // Creating delay
    public void sleep() {
        try {
            System.out.println();
            Thread.sleep(1000);
            System.out.println();
        } catch (InterruptedException e) {
            System.out.println("An Error Occurred During Processing");
        }
    }

    // Save transaction to DB
    public void saveTransaction(String type, float amount) {
        String query = "INSERT INTO transactions (transaction_type, amount, balance_after) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, type);
            ps.setFloat(2, amount);
            ps.setFloat(3, Bal);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Show transaction history
    public void showTransactions() {
        String query = "SELECT * FROM transactions ORDER BY transaction_time DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n----- Transaction History -----");
            System.out.printf("%-5s %-15s %-10s %-12s %-20s%n",
                    "ID", "Type", "Amount", "Balance", "Time");
            System.out.println("--------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-15s %-10.2f %-12.2f %-20s%n",
                        rs.getInt("transaction_id"),
                        rs.getString("transaction_type"),
                        rs.getFloat("amount"),
                        rs.getFloat("balance_after"),
                        rs.getTimestamp("transaction_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // PIN checking
    public void checkPin(Scanner sc) {
        System.out.print("Please Enter Your PIN: ");
        int enteredPin = sc.nextInt();
        if (enteredPin == Pin) {
            System.out.println("\nPlease Wait...");
            sleep();
            menu(sc);
        } else {
            sleep();
            System.out.println("Invalid Pin! Please Enter a valid Pin...");
            checkPin(sc);
        }
    }

    // Menu
    public void menu(Scanner sc) {
        while (true) {
            System.out.println("\n------ Dear Customer, Please Select Transaction ------");
            System.out.println("1. FAST CASH");
            System.out.println("2. WITHDRAWAL");
            System.out.println("3. PIN CHANGE");
            System.out.println("4. BALANCE ENQUIRY");
            System.out.println("5. DEPOSIT");
            System.out.println("6. SHOW TRANSACTIONS");
            System.out.println("7. EXIT");

            int opt = sc.nextInt();
            switch (opt) {
                case 1 -> fastCash(sc);
                case 2 -> withdrawal(sc);
                case 3 -> pinChange(sc);
                case 4 -> balanceInquiry();
                case 5 -> depositMoney(sc);
                case 6 -> showTransactions();
                case 7 -> {
                    System.out.println("Thank you for using our ATM.");
                    return;
                }
                default -> System.out.println("Please Select a Valid Choice");
            }
        }
    }

    // Fast cash
    public void fastCash(Scanner sc) {
        System.out.println("Please Select Amount:");
        System.out.println("1. 100   5. 2000");
        System.out.println("2. 300   6. 3000");
        System.out.println("3. 500   7. 5000");
        System.out.println("4. 1000  8. 10000");

        int choice = sc.nextInt();
        float amount = switch (choice) {
            case 1 -> 100;
            case 2 -> 300;
            case 3 -> 500;
            case 4 -> 1000;
            case 5 -> 2000;
            case 6 -> 3000;
            case 7 -> 5000;
            case 8 -> 10000;
            default -> -1;
        };

        if (amount > 0 && amount <= Bal) {
            Bal -= amount;
            System.out.println("Withdrawal Successful...");
            saveTransaction("Fast Cash", amount);
            balanceDisplay(sc);
        } else {
            System.out.println("Invalid choice or insufficient balance.");
        }
    }

    // Withdrawal
    public void withdrawal(Scanner sc) {
        System.out.print("Enter amount to withdraw: ");
        float amount = sc.nextFloat();
        if (amount <= Bal && amount > 0) {
            Bal -= amount;
            System.out.println("Withdrawal Successful...");
            saveTransaction("Withdrawal", amount);
            balanceDisplay(sc);
        } else {
            System.out.println("Insufficient Funds or Invalid Amount.");
        }
    }

    // PIN change
    public void pinChange(Scanner sc) {
        System.out.print("Enter new PIN: ");
        int newPin = sc.nextInt();
        System.out.print("Re-enter new PIN: ");
        int rePin = sc.nextInt();

        if (newPin == rePin && newPin >= 1000 && newPin <= 9999) {
            Pin = newPin;
            System.out.println("PIN Change Successful...");
            saveTransaction("PIN Change", 0);
        } else {
            System.out.println("PIN Mismatch or Invalid.");
        }
    }

    // Balance Inquiry
    public void balanceInquiry() {
        System.out.println("Current Balance: " + Bal);
    }

    // Deposit
    public void depositMoney(Scanner sc) {
        System.out.print("Enter deposit amount: ");
        float amount = sc.nextFloat();
        if (amount > 0) {
            Bal += amount;
            System.out.println("Deposit Successful...");
            saveTransaction("Deposit", amount);
            balanceDisplay(sc);
        } else {
            System.out.println("Invalid Amount.");
        }
    }

    // Show balance option
    public void balanceDisplay(Scanner sc) {
        System.out.print("Show balance on screen? (1.Yes / 2.No): ");
        int choice = sc.nextInt();
        if (choice == 1) {
            System.out.println("Current Balance: " + Bal);
        }
        System.out.println("Transaction Complete. Thank you!");
    }
}
