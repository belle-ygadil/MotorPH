/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Ms.Shiela
 */

import java.io.*;
import java.util.*;

public class Login {

    // Hardcoded valid usernames and passwords
    static Map<String, String> credentials = new HashMap<>();
    static {
        credentials.put("employee", "12345");
        credentials.put("payroll staff", "12345");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1. Ask for username and password
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        // 2. Validate credentials
        if (!credentials.containsKey(username) || !credentials.get(username).equals(password)) {
            System.out.println("Incorrect username and/or password.");
            return; // terminate program
        }

        // 3. If login is correct
        if (username.equals("employee")) {
            employeeMenu(sc);
        } else if (username.equals("payroll staff")) {
            System.out.println("Payroll staff access granted. (Add your functionality here)");
            // Add payroll staff options if needed
        }
    }

    // Menu for employee
    private static void employeeMenu(Scanner sc) {
        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Enter your employee number");
            System.out.println("2. Exit the program");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                System.out.print("Enter employee number: ");
                String empNumber = sc.nextLine().trim();
                displayEmployeeDetails(empNumber);
            } else if (choice.equals("2")) {
                System.out.println("Program terminated.");
                return;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    // Read CSV and display details
    private static void displayEmployeeDetails(String empNumber) {
        String filePath = "employee.csv"; // path to your CSV
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); // assuming CSV: empNumber,empName,birthday
                if (data[0].equals(empNumber)) {
                    System.out.println("\nEmployee Details:");
                    System.out.println("Employee Number: " + data[0]);
                    System.out.println("Employee Name: " + data[1]);
                    System.out.println("Birthday: " + data[2]);
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Employee number does not exist.");
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file.");
            e.printStackTrace();
        }
    }
}