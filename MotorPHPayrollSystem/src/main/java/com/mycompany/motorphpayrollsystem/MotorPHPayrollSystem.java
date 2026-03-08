/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * MotorPH Payroll System
*/

package com.mycompany.motorphpayrollsystem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.time.LocalTime;
import java.time.Duration;
import java.time.YearMonth;
import java.util.Scanner;
/**
 *
 * @author jonat
 */
public class MotorPHPayrollSystem {
    // -------------------------------------------------------
    // FILE PATHS 
    // -------------------------------------------------------
    static final String EMP_FILE = "C:\\Users\\jonat\\Documents\\NetBeansProjects\\Ex1\\MotorPH\\MotorPHPayrollSystem\\src\\main\\java\\com\\mycompany\\motorphpayrollsystem\\employee_data.csv";
    static final String ATT_FILE = "C:\\Users\\jonat\\Documents\\NetBeansProjects\\Ex1\\MotorPH\\MotorPHPayrollSystem\\src\\main\\java\\com\\mycompany\\motorphpayrollsystem\\attendance";

    // -------------------------------------------------------
    // MONTH RANGE: June (6) to December (12) of 2024
    // -------------------------------------------------------
    static final int START_MONTH = 6;
    static final int END_MONTH   = 12;
    static final int YEAR        = 2024;
    
    public static void main(String[] args) {
  Scanner sc = new Scanner(System.in);

        // -------------------------------------------------------
        // STEP 1 — LOGIN
        // -------------------------------------------------------
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        // Validate credentials
        boolean validEmployee     = username.equals("employee")     && password.equals("12345");
        boolean validPayrollStaff = username.equals("payroll_staff") && password.equals("12345");

        if (!validEmployee && !validPayrollStaff) {
            System.out.println("Incorrect username and/or password.");
            sc.close();
            return;
        }

        // -------------------------------------------------------
        // EMPLOYEE MENU
        // -------------------------------------------------------
        if (validEmployee) {
            System.out.println("\n1. Enter your employee number");
            System.out.println("2. Exit the program");
            System.out.print("Choose an option: ");
            String empChoice = sc.nextLine().trim();

            if (empChoice.equals("2")) {
                sc.close();
                return;
            }

            if (empChoice.equals("1")) {
                System.out.print("Enter Employee #: ");
                String inputEmpNo = sc.nextLine().trim();
                displayEmployeeInfo(inputEmpNo);
            }

            sc.close();
            return;
        }

        // -------------------------------------------------------
        // PAYROLL STAFF MENU
        // -------------------------------------------------------
        if (validPayrollStaff) {
            System.out.println("\n1. Process Payroll");
            System.out.println("2. Exit the program");
            System.out.print("Choose an option: ");
            String staffChoice = sc.nextLine().trim();

            if (staffChoice.equals("2")) {
                sc.close();
                return;
            }

            if (staffChoice.equals("1")) {
                System.out.println("\n1. One employee");
                System.out.println("2. All employees");
                System.out.println("3. Exit the program");
                System.out.print("Choose an option: ");
                String subChoice = sc.nextLine().trim();

                if (subChoice.equals("3")) {
                    sc.close();
                    return;
                }

                if (subChoice.equals("1")) {
                    System.out.print("Enter Employee #: ");
                    String inputEmpNo = sc.nextLine().trim();
                    processOneEmployee(inputEmpNo);
                }

                if (subChoice.equals("2")) {
                    processAllEmployees();
                }
            }

            sc.close();
        }
    }

    // =======================================================
    // DISPLAY EMPLOYEE INFO (employee login)
    // Reads the CSV and prints basic employee details.
    // =======================================================
    static void displayEmployeeInfo(String inputEmpNo) {
        File f = new File(EMP_FILE);
        if (!f.exists()) {
            System.out.println("ERROR: Employee file not found at: " + EMP_FILE);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // skip header row
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = splitCsvLine(line);
                if (data.length < 4) continue;
                if (data[0].trim().equals(inputEmpNo)) {
                    System.out.println("\n===================================");
                    System.out.println("Employee Number : " + data[0].trim());
                    System.out.println("Employee Name   : " + data[1].trim() + ", " + data[2].trim());
                    System.out.println("Birthday        : " + data[3].trim());
                    System.out.println("===================================");
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: Could not read employee file. " + e.getMessage());
            return;
        }

        System.out.println("Employee number does not exist.");
    }

    // =======================================================
    // PROCESS PAYROLL — ONE EMPLOYEE
    // =======================================================
    static void processOneEmployee(String inputEmpNo) {
        // --- Find employee in CSV ---
        String[] empData = findEmployee(inputEmpNo);
        if (empData == null) {
            System.out.println("Employee number does not exist.");
            return;
        }

        String empNo     = empData[0].trim();
        String lastName  = empData[1].trim();
        String firstName = empData[2].trim();
        String birthday  = empData[3].trim();
        double hourlyRate = parseDouble(empData[18].trim()); // column 19 = Hourly Rate

        System.out.println("\n===================================");
        System.out.println("Employee #    : " + empNo);
        System.out.println("Employee Name : " + lastName + ", " + firstName);
        System.out.println("Birthday      : " + birthday);
        System.out.println("===================================");

        printPayroll(empNo, hourlyRate);
    }

    // =======================================================
    // PROCESS PAYROLL — ALL EMPLOYEES
    // =======================================================
    static void processAllEmployees() {
        File f = new File(EMP_FILE);
        if (!f.exists()) {
            System.out.println("ERROR: Employee file not found at: " + EMP_FILE);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = splitCsvLine(line);
                if (data.length < 19) continue;

                String empNo      = data[0].trim();
                String lastName   = data[1].trim();
                String firstName  = data[2].trim();
                String birthday   = data[3].trim();
                double hourlyRate = parseDouble(data[18].trim()); // column 19 = Hourly Rate

                System.out.println("\n===================================");
                System.out.println("Employee #    : " + empNo);
                System.out.println("Employee Name : " + lastName + ", " + firstName);
                System.out.println("Birthday      : " + birthday);
                System.out.println("===================================");

                printPayroll(empNo, hourlyRate);
            }
        } catch (Exception e) {
            System.out.println("ERROR: Could not read employee file. " + e.getMessage());
        }
    }

    // =======================================================
    // PRINT PAYROLL — June to December for one employee
    // Computes hours, gross, deductions, and net per cutoff.
    // =======================================================
    static void printPayroll(String empNo, double hourlyRate) {

        for (int month = START_MONTH; month <= END_MONTH; month++) {

            // Accumulate hours for each cutoff
            double firstHalfHours  = 0;
            double secondHalfHours = 0;
            int daysInMonth = YearMonth.of(YEAR, month).lengthOfMonth();

            File attFile = new File(ATT_FILE);
            if (!attFile.exists()) {
                System.out.println("ERROR: Attendance file not found at: " + ATT_FILE);
                return;
            }

            // --- Read attendance records ---
            try (BufferedReader br = new BufferedReader(new FileReader(attFile))) {
                br.readLine(); // skip header
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = splitCsvLine(line);

                    // Attendance CSV expected columns:
                    // [0] EmpNo, [1] LastName, [2] FirstName, [3] Date (MM/DD/YYYY),
                    // [4] TimeIn (H:MM), [5] TimeOut (H:MM)
                    if (data.length < 6) continue;
                    if (!data[0].trim().equals(empNo)) continue;

                    // Parse date
                    String[] dateParts = data[3].trim().split("/");
                    if (dateParts.length < 3) continue;
                    int recMonth = Integer.parseInt(dateParts[0].trim());
                    int recDay   = Integer.parseInt(dateParts[1].trim());
                    int recYear  = Integer.parseInt(dateParts[2].trim());

                    if (recYear != YEAR || recMonth != month) continue;

                    // Parse time-in and time-out
                    LocalTime timeIn  = parseTime(data[4].trim());
                    LocalTime timeOut = parseTime(data[5].trim());

                    if (timeIn == null || timeOut == null) continue;

                    double hoursWorked = computeHours(timeIn, timeOut);

                    if (recDay <= 15) {
                        firstHalfHours  += hoursWorked;
                    } else {
                        secondHalfHours += hoursWorked;
                    }
                }
            } catch (Exception e) {
                System.out.println("ERROR: Could not read attendance file. " + e.getMessage());
                return;
            }

            // --- Gross salary per cutoff ---
            double grossFirst  = firstHalfHours  * hourlyRate;
            double grossSecond = secondHalfHours * hourlyRate;
            double monthlyGross = grossFirst + grossSecond;

            // --- Deductions computed on combined monthly gross (Rule 5) ---
            double sss        = computeSSS(monthlyGross);
            double philHealth = computePhilHealth(monthlyGross);
            double pagIbig    = computePagIbig(monthlyGross);
            double tax        = computeIncomeTax(monthlyGross, sss, philHealth, pagIbig);

            // --- All deductions applied on 2nd cutoff only ---
            double totalDeductions = sss + philHealth + pagIbig + tax;
            double netFirst        = grossFirst;                       // No deductions on 1st cutoff
            double netSecond       = grossSecond - totalDeductions;    // All deductions on 2nd cutoff

            String monthName = getMonthName(month);

            // --- Display 1st cutoff ---
            System.out.println("\nCutoff Date     : " + monthName + " 1 to 15");
            System.out.println("Total Hours Worked : " + firstHalfHours);
            System.out.println("Gross Salary       : PHP " + grossFirst);
            System.out.println("Net Salary         : PHP " + netFirst);

            // --- Display 2nd cutoff ---
            System.out.println("\nCutoff Date     : " + monthName + " 16 to " + daysInMonth);
            System.out.println("Total Hours Worked : " + secondHalfHours);
            System.out.println("Gross Salary       : PHP " + grossSecond);
            System.out.println("SSS                : PHP " + sss);
            System.out.println("PhilHealth         : PHP " + philHealth);
            System.out.println("Pag-IBIG           : PHP " + pagIbig);
            System.out.println("Tax                : PHP " + tax);
            System.out.println("Total Deductions   : PHP " + totalDeductions);
            System.out.println("Net Salary         : PHP " + netSecond);
            System.out.println("-----------------------------------");
        }
    }

    // =======================================================
    // COMPUTE HOURS WORKED
    // Rules:
    //   - Only count 8:00 AM to 5:00 PM (max 8 hrs, no overtime).
    //   - Grace period: login <= 8:10 AM is treated as exactly 8:00 AM.
    //   - Deduct 1-hour lunch break only if total raw time > 1 hour.
    //   - Logout capped at 5:00 PM.
    // =======================================================
    static double computeHours(LocalTime rawTimeIn, LocalTime rawTimeOut) {

        LocalTime shiftStart  = LocalTime.of(8, 0);
        LocalTime graceEnd    = LocalTime.of(8, 10);
        LocalTime shiftEnd    = LocalTime.of(17, 0);  // 5:00 PM

        // Cap logout at 5:00 PM — no overtime counted
        LocalTime effectiveOut = rawTimeOut.isAfter(shiftEnd) ? shiftEnd : rawTimeOut;

        // Apply grace period: if login is at or before 8:10, treat as 8:00
        LocalTime effectiveIn = rawTimeIn.isAfter(graceEnd) ? rawTimeIn : shiftStart;

        // If effective time-in is after the capped time-out, no hours counted
        if (!effectiveIn.isBefore(effectiveOut)) return 0;

        // Raw minutes between effective in and effective out
        long rawMinutes = Duration.between(effectiveIn, effectiveOut).toMinutes();

        // Deduct 60-minute lunch break only if the shift is longer than 60 minutes
        long workedMinutes = rawMinutes > 60 ? rawMinutes - 60 : rawMinutes;

        // Convert minutes to hours (no rounding)
        return workedMinutes / 60.0;
    }

    // =======================================================
    // GOVERNMENT DEDUCTION METHODS (from Task 9)
    // All computed on the combined monthly gross salary.
    // =======================================================

    // SSS — bracket-based employee contribution table
    static double computeSSS(double monthlyGross) {
        if      (monthlyGross < 3250)  return 135.00;
        else if (monthlyGross < 3750)  return 157.50;
        else if (monthlyGross < 4250)  return 180.00;
        else if (monthlyGross < 4750)  return 202.50;
        else if (monthlyGross < 5250)  return 225.00;
        else if (monthlyGross < 5750)  return 247.50;
        else if (monthlyGross < 6250)  return 270.00;
        else if (monthlyGross < 6750)  return 292.50;
        else if (monthlyGross < 7250)  return 315.00;
        else if (monthlyGross < 7750)  return 337.50;
        else if (monthlyGross < 8250)  return 360.00;
        else if (monthlyGross < 8750)  return 382.50;
        else if (monthlyGross < 9250)  return 405.00;
        else if (monthlyGross < 9750)  return 427.50;
        else if (monthlyGross < 10250) return 450.00;
        else if (monthlyGross < 10750) return 472.50;
        else if (monthlyGross < 11250) return 495.00;
        else if (monthlyGross < 11750) return 517.50;
        else if (monthlyGross < 12250) return 540.00;
        else if (monthlyGross < 12750) return 562.50;
        else if (monthlyGross < 13250) return 585.00;
        else if (monthlyGross < 13750) return 607.50;
        else if (monthlyGross < 14250) return 630.00;
        else if (monthlyGross < 14750) return 652.50;
        else if (monthlyGross < 15250) return 675.00;
        else if (monthlyGross < 15750) return 697.50;
        else if (monthlyGross < 16250) return 720.00;
        else if (monthlyGross < 16750) return 742.50;
        else if (monthlyGross < 17250) return 765.00;
        else if (monthlyGross < 17750) return 787.50;
        else if (monthlyGross < 18250) return 810.00;
        else if (monthlyGross < 18750) return 832.50;
        else if (monthlyGross < 19250) return 855.00;
        else if (monthlyGross < 19750) return 877.50;
        else if (monthlyGross < 20250) return 900.00;
        else if (monthlyGross < 20750) return 922.50;
        else if (monthlyGross < 21250) return 945.00;
        else if (monthlyGross < 21750) return 967.50;
        else if (monthlyGross < 22250) return 990.00;
        else if (monthlyGross < 22750) return 1012.50;
        else if (monthlyGross < 23250) return 1035.00;
        else if (monthlyGross < 23750) return 1057.50;
        else if (monthlyGross < 24250) return 1080.00;
        else if (monthlyGross < 24750) return 1102.50;
        else                           return 1125.00;
    }

    // PhilHealth — 3% of monthly gross, employee pays 50%, floor PHP 300, ceiling PHP 1800 total premium
    static double computePhilHealth(double monthlyGross) {
        double premium = monthlyGross * 0.03;
        if (premium < 300)  premium = 300;
        if (premium > 1800) premium = 1800;
        return premium / 2; // employee share
    }

    // Pag-IBIG — 1% (PHP 1000-1500), 2% (above PHP 1500), capped at PHP 100
    static double computePagIbig(double monthlyGross) {
        double contribution = 0;
        if (monthlyGross >= 1000 && monthlyGross <= 1500) {
            contribution = monthlyGross * 0.01;
        } else if (monthlyGross > 1500) {
            contribution = monthlyGross * 0.02;
        }
        if (contribution > 100) contribution = 100;
        return contribution;
    }

    // Income Tax — TRAIN Law monthly withholding tax brackets
    static double computeIncomeTax(double monthlyGross, double sss, double philHealth, double pagIbig) {
        double taxableIncome = monthlyGross - (sss + philHealth + pagIbig);
        if      (taxableIncome <= 20832)  return 0;
        else if (taxableIncome <= 33333)  return (taxableIncome - 20833) * 0.20;
        else if (taxableIncome <= 66667)  return 2500 + (taxableIncome - 33333) * 0.25;
        else if (taxableIncome <= 166667) return 10833 + (taxableIncome - 66667) * 0.30;
        else if (taxableIncome <= 666667) return 40833.33 + (taxableIncome - 166667) * 0.32;
        else                              return 200833.33 + (taxableIncome - 666667) * 0.35;
    }

    // =======================================================
    // HELPER — Find employee record by employee number
    // Returns the split CSV row, or null if not found.
    // =======================================================
    static String[] findEmployee(String inputEmpNo) {
        File f = new File(EMP_FILE);
        if (!f.exists()) {
            System.out.println("ERROR: Employee file not found at: " + EMP_FILE);
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = splitCsvLine(line);
                if (data.length < 19) continue;
                if (data[0].trim().equals(inputEmpNo)) return data;
            }
        } catch (Exception e) {
            System.out.println("ERROR: Could not read employee file. " + e.getMessage());
        }

        return null; // not found
    }

    // =======================================================
    // HELPER — Parse a time string into LocalTime
    // Supports formats: "H:MM" or "HH:MM"
    // Returns null if the string cannot be parsed.
    // =======================================================
    static LocalTime parseTime(String timeStr) {
        try {
            String[] parts = timeStr.split(":");
            int hour   = Integer.parseInt(parts[0].trim());
            int minute = Integer.parseInt(parts[1].trim());
            return LocalTime.of(hour, minute);
        } catch (Exception e) {
            return null; // unparseable time — caller should skip this record
        }
    }

    // =======================================================
    // HELPER — Parse a double safely, stripping commas first
    // Returns 0 if the string is not a valid number.
    // =======================================================
    static double parseDouble(String value) {
        try {
            return Double.parseDouble(value.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // =======================================================
    // HELPER — Split a CSV line, correctly handling quoted fields
    // that contain commas (e.g., "90,000" or "Valero Street, Makati").
    // Walks character-by-character: toggles inQuotes when it sees a
    // double-quote, and only splits on commas that are outside quotes.
    // =======================================================
    static String[] splitCsvLine(String line) {
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes; // toggle quoted-field mode
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString()); // end of field
                current.setLength(0);           // reset buffer
            } else {
                current.append(c); // normal character — add to current field
            }
        }

        fields.add(current.toString()); // add the last field
        return fields.toArray(new String[0]);
    }

    // =======================================================
    // HELPER — Return the month name for a given month number
    // =======================================================
    static String getMonthName(int month) {
        switch (month) {
            case 1:  return "January";
            case 2:  return "February";
            case 3:  return "March";
            case 4:  return "April";
            case 5:  return "May";
            case 6:  return "June";
            case 7:  return "July";
            case 8:  return "August";
            case 9:  return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
            default: return "";
        }
    }
}
