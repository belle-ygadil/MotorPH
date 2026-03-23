/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * MotorPH Payroll System - Revised
 */

package com.mycompany.motorph_revised;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.time.LocalTime;
import java.time.Duration;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MotorPH_Revised {
    
    // -------------------------------------------------------
    // FILE PATHS
    // Relative paths are used so the program runs on any machine without editing hardcoded absolute directories.
    // -------------------------------------------------------
    static final String EMPLOYEE_FILE_PATH   = "resources/employee.csv";
    static final String ATTENDANCE_FILE_PATH = "resources/attendance.csv";
 
    // -------------------------------------------------------
    // PAYROLL PERIOD CONSTANTS
    // Only attendance records within this year and month range are included in payroll computation.
    // -------------------------------------------------------
    static final int PAYROLL_START_MONTH = 6;   // June
    static final int PAYROLL_END_MONTH   = 12;  // December
    static final int PAYROLL_YEAR        = 2024;
 
    // -------------------------------------------------------
    // ATTENDANCE RECORDS CACHE
    // All attendance rows are loaded once at startup into this list.
    // This avoids re-reading the attendance file for every employee and every month.
    // Each element is one split CSV row (String array).
    // -------------------------------------------------------
    static List<String[]> allAttendanceRecords = new ArrayList<>();
 
    // =======================================================
    // MAIN
    // Entry point of the program.
    // Responsibilities: load attendance data, handle login, and route to the correct menu.
    // =======================================================
    
    public static void main(String[] args) {
 // Open one Scanner for all console input throughout the program
        Scanner userInputScanner = new Scanner(System.in);
 
        // Load all attendance records into memory once before any processing.
        // This means the attendance file is read only one time total, regardless of how many employees or months are processed.
        loadAllAttendanceRecords();
 
        // Prompt for login and return the validated username (or null on failure)
        String validatedUsername = handleLogin(userInputScanner);
 
        // If login failed, handleLogin already printed the error message
        if (validatedUsername == null) {
            userInputScanner.close();
            return;
        }
 
        // Route the user to the correct menu based on their username
        if (validatedUsername.equals("employee")) {
            showEmployeeMenu(userInputScanner);
        } else if (validatedUsername.equals("payroll_staff")) {
            showPayrollStaffMenu(userInputScanner);
        }
 
        // Close the Scanner at the end of main
        userInputScanner.close();
    }
 
    // =======================================================
    // HANDLE LOGIN
    // Prompts the user for a username and password, then checks against the two valid credential combinations.
    //
    // Parameter : userInputScanner - shared Scanner for input
    // Returns   : the validated username string if login succeeds, or null if credentials are wrong
    // =======================================================
    static String handleLogin(Scanner userInputScanner) {
        System.out.print("Enter username: ");
        String username = userInputScanner.nextLine().trim();
 
        System.out.print("Enter password: ");
        String password = userInputScanner.nextLine().trim();
 
        // Check if the credentials match either valid account
        boolean isEmployee     = username.equals("employee")      && password.equals("12345");
        boolean isPayrollStaff = username.equals("payroll_staff") && password.equals("12345");
 
        if (!isEmployee && !isPayrollStaff) {
            // Neither valid account matched - reject login
            System.out.println("Incorrect username and/or password.");
            return null;
        }
 
        // Return the username so main knows which menu to show
        return username;
    }
 
    // =======================================================
    // SHOW EMPLOYEE MENU
    // Displays options for an employee-role user.
    // Option 1: look up and display their own basic info.
    // Option 2: exit the program.
    //
    // Parameter : userInputScanner - shared Scanner for input
    // =======================================================
    static void showEmployeeMenu(Scanner userInputScanner) {
        System.out.println("\n1. Enter your employee number");
        System.out.println("2. Exit the program");
        System.out.print("Choose an option: ");
        String menuChoice = userInputScanner.nextLine().trim();
 
        if (menuChoice.equals("1")) {
            System.out.print("Enter Employee #: ");
            String employeeNumber = userInputScanner.nextLine().trim();
 
            // Look up and display the employee's basic details
            displayEmployeeInfo(employeeNumber);
        }
        // Any other input (including "2") falls through and exits
    }
 
    // =======================================================
    // SHOW PAYROLL STAFF MENU
    // Displays options for a payroll_staff-role user.
    // Option 1: process payroll (leads to a sub-menu).
    // Option 2: exit the program.
    //
    // Parameter : userInputScanner - shared Scanner for input
    // =======================================================
    static void showPayrollStaffMenu(Scanner userInputScanner) {
        System.out.println("\n1. Process Payroll");
        System.out.println("2. Exit the program");
        System.out.print("Choose an option: ");
        String staffMenuChoice = userInputScanner.nextLine().trim();
 
        // If the user did not choose option 1, exit the method
        if (!staffMenuChoice.equals("1")) {
            System.out.println("Program has been terminated.");
            return;
        }
 
        // Payroll sub-menu 
        System.out.println("\n1. One employee");
        System.out.println("2. All employees");
        System.out.println("3. Exit the program");
        System.out.print("Choose an option: ");
        String payrollSubChoice = userInputScanner.nextLine().trim();
 
        if (payrollSubChoice.equals("1")) {
            // Ask for a specific employee number and process only that employee
            System.out.print("Enter Employee #: ");
            String employeeNumber = userInputScanner.nextLine().trim();
            processSingleEmployee(employeeNumber);
 
        } else if (payrollSubChoice.equals("2")) {
            // Process every employee found in the employee CSV file
            processAllEmployees();
            
        } else {
            // Option 3 or any invalid input - display termination message
            System.out.println("Program has been terminated.");
        }
    }
        
    // =======================================================
    // DISPLAY EMPLOYEE INFO
    // Used by the employee login menu to show basic details.
    // Looks up the employee by number and prints their name, number, and birthday.
    //
    // Parameter : employeeNumber - the number entered by user
    // =======================================================
    static void displayEmployeeInfo(String employeeNumber) {
        // Search the employee CSV for a matching row
        String[] employeeRow = findEmployeeRow(employeeNumber);
 
        if (employeeRow == null) {
            System.out.println("Employee number does not exist.");
            return;
        }
 
        // Reuse the shared header-printing method to display details
        printEmployeeHeader(employeeRow);
    }
 
    // =======================================================
    // PRINT EMPLOYEE HEADER
    // Prints the standard employee info block that appears at the top of every payslip display.
    // Extracted into its own reusable method so the same output format is used
    // consistently in displayEmployeeInfo, processSingleEmployee, and processAllEmployees.
    //
    // Parameter : employeeRow - full split CSV row for one employee
    //   [0] Employee #   [1] Last Name   [2] First Name   [3] Birthday
    // =======================================================
    static void printEmployeeHeader(String[] employeeRow) {
        String empNo     = employeeRow[0].trim(); // Column 0: Employee number
        String lastName  = employeeRow[1].trim(); // Column 1: Last name
        String firstName = employeeRow[2].trim(); // Column 2: First name
        String birthday  = employeeRow[3].trim(); // Column 3: Date of birth
 
        System.out.println("\n===================================");
        System.out.println("Employee #    : " + empNo);
        System.out.println("Employee Name : " + lastName + ", " + firstName);
        System.out.println("Birthday      : " + birthday);
        System.out.println("===================================");
    }
 
    // =======================================================
    // PROCESS SINGLE EMPLOYEE
    // Looks up one employee by their number, prints their header, then computes and displays their full payroll
    // summary for all months from June to December.
    //
    // Parameter : employeeNumber - the number entered by user
    // =======================================================
    static void processSingleEmployee(String employeeNumber) {
        // Search the employee CSV for a matching employee number
        String[] employeeRow = findEmployeeRow(employeeNumber);
 
        if (employeeRow == null) {
            System.out.println("Employee number does not exist.");
            return;
        }
 
        // Print the standard header block (name, number, birthday)
        printEmployeeHeader(employeeRow);
 
        // Column 18 is the Hourly Rate - the last column in the employee CSV.
        // parseDouble handles formatted values like "535.71" safely.
        double hourlyRate = parseDouble(employeeRow[18].trim());
 
        // Run payroll computation and display for June through December
        printPayrollSummary(employeeRow[0].trim(), hourlyRate);
    }
 
    // =======================================================
    // PROCESS ALL EMPLOYEES
    // Reads every row from the employee CSV and runs payroll for each employee found.
    // =======================================================
    static void processAllEmployees() {
        File employeeFile = new File(EMPLOYEE_FILE_PATH);
 
        // Confirm the employee file exists before trying to open it
        if (!employeeFile.exists()) {
            System.out.println("ERROR: Employee file not found at: "
                    + EMPLOYEE_FILE_PATH);
            return;
        }
 
        // try-with-resources ensures the file is closed automatically
        try (BufferedReader employeeReader =
                     new BufferedReader(new FileReader(employeeFile))) {
 
            employeeReader.readLine(); // Skip the header row (column titles)
 
            String csvLine;
            // Read one employee row at a time until the end of the file
            while ((csvLine = employeeReader.readLine()) != null) {
 
                if (csvLine.trim().isEmpty()) continue; // Skip blank lines
 
                // Parse the CSV line into individual fields
                // splitCsvLine handles quoted fields containing commas
                String[] employeeRow = splitCsvLine(csvLine);
 
                // A valid employee row must have all 19 columns (indexes 0 to 18)
                if (employeeRow.length < 19) continue;
 
                // Print the standard employee header block
                printEmployeeHeader(employeeRow);
 
                // Column 18 is the Hourly Rate used for gross salary computation
                double hourlyRate = parseDouble(employeeRow[18].trim());
 
                // Compute and print the monthly payroll for this employee
                printPayrollSummary(employeeRow[0].trim(), hourlyRate);
            }
 
        } catch (Exception fileReadException) {
            System.out.println("ERROR: Could not read employee file. "
                    + fileReadException.getMessage());
        }
    }
 
    // =======================================================
    // LOAD ALL ATTENDANCE RECORDS
    // Reads the entire attendance CSV into the shared allAttendanceRecords list once at program startup.
    // =======================================================
    static void loadAllAttendanceRecords() {
        File attendanceFile = new File(ATTENDANCE_FILE_PATH);
 
        // If the file is missing, warn the user but do not crash.
        // Payroll will proceed and show 0 hours for all employees.
        if (!attendanceFile.exists()) {
            System.out.println("WARNING: Attendance file not found at: "
                    + ATTENDANCE_FILE_PATH);
            return;
        }
 
        try (BufferedReader attendanceReader =
                     new BufferedReader(new FileReader(attendanceFile))) {
 
            attendanceReader.readLine(); // Skip the header row
 
            String csvLine;
            while ((csvLine = attendanceReader.readLine()) != null) {
                if (csvLine.trim().isEmpty()) continue; // Skip blank lines
 
                String[] attendanceRow = splitCsvLine(csvLine);
 
                // Only store rows that have all 6 required columns
                if (attendanceRow.length >= 6) {
                    allAttendanceRecords.add(attendanceRow);
                }
            }
 
        } catch (Exception fileReadException) {
            System.out.println("ERROR: Could not load attendance file. "
                    + fileReadException.getMessage());
        }
    }
 
    // =======================================================
    // PRINT PAYROLL SUMMARY
    // For one employee, loops through each month from June to December. 
    // For each month it:
    //   1. Scans the in-memory attendance list for matching records.
    //   2. Computes total hours worked per cutoff (days 1-15 and 16-end).
    //   3. Calculates gross salary for each cutoff.
    //   4. Computes monthly government deductions on combined gross.
    //   5. Prints a formatted payslip for both cutoff periods.
    //
    // Parameters:
    //   empNo      - employee number to match in attendance records
    //   hourlyRate - the employee's pay per hour from the CSV
    // =======================================================
    static void printPayrollSummary(String empNo, double hourlyRate) {
 
        // Loop through each month in the payroll period (June to December)
        for (int month = PAYROLL_START_MONTH; month <= PAYROLL_END_MONTH; month++) {
 
            // Running totals for hours in each half of the month
            double firstCutoffHours  = 0; // Hours worked on days 1 through 15
            double secondCutoffHours = 0; // Hours worked on days 16 through end
 
            // Get the actual last day of this month (ex. 30 for June, 31 for July)
            int lastDayOfMonth = YearMonth.of(PAYROLL_YEAR, month).lengthOfMonth();
 
            // Scan the preloaded attendance list for this employee and month
            // Iterating the in-memory list is much faster than re-reading the file
            for (String[] attendanceRow : allAttendanceRecords) {
 
                // Column 0 is Employee # - skip rows that belong to other employees
                if (!attendanceRow[0].trim().equals(empNo)) continue;
 
                // Column 3 is the date in MM/DD/YYYY format
                String[] dateParts = attendanceRow[3].trim().split("/");
                if (dateParts.length < 3) continue; // Skip rows with malformed dates
 
                int recordMonth = Integer.parseInt(dateParts[0].trim()); // ex. 6
                int recordDay   = Integer.parseInt(dateParts[1].trim()); // ex. 14
                int recordYear  = Integer.parseInt(dateParts[2].trim()); // ex. 2024
 
                // Only process records for the current loop month and payroll year
                if (recordYear != PAYROLL_YEAR || recordMonth != month) continue;
 
                // Column 4 = Time In, Column 5 = Time Out (format: H:MM or HH:MM)
                LocalTime timeIn  = parseTime(attendanceRow[4].trim());
                LocalTime timeOut = parseTime(attendanceRow[5].trim());
 
                // Skip records where the time values could not be parsed
                if (timeIn == null || timeOut == null) continue;
 
                // Apply payroll time rules to get effective hours for this day
                double hoursWorkedThisDay = computeHours(timeIn, timeOut);
 
                // Assign to the correct cutoff based on the day number
                if (recordDay <= 15) {
                    firstCutoffHours  += hoursWorkedThisDay; // 1st cutoff: days 1-15
                } else {
                    secondCutoffHours += hoursWorkedThisDay; // 2nd cutoff: days 16-end
                }
            }
 
            // Gross salary = hours in that cutoff * the hourly rate
            double grossFirstCutoff  = firstCutoffHours  * hourlyRate;
            double grossSecondCutoff = secondCutoffHours * hourlyRate;
 
            // Monthly gross = sum of both cutoffs; used as the deduction basis
            double monthlyGross = grossFirstCutoff + grossSecondCutoff;
 
            // Government deductions computed on the full monthly gross
            // All four methods receive monthlyGross (or its components) as required
            double sssDeduction        = computeSSS(monthlyGross);
            double philHealthDeduction = computePhilHealth(monthlyGross);
            double pagIbigDeduction    = computePagIbig(monthlyGross);
            double incomeTaxDeduction  = computeIncomeTax(
                    monthlyGross, sssDeduction, philHealthDeduction, pagIbigDeduction);
 
            // Combined total of all four deductions
            double totalDeductions = sssDeduction + philHealthDeduction
                    + pagIbigDeduction + incomeTaxDeduction;
 
            // 1st cutoff: no deductions - employee receives full gross amount
            double netFirstCutoff  = grossFirstCutoff;
 
            // 2nd cutoff: all monthly deductions are subtracted from gross
            double netSecondCutoff = grossSecondCutoff - totalDeductions;
 
            String monthName = getMonthName(month);
 
            // Display 1st cutoff payslip (days 1-15, no deductions) 
            System.out.println("\nCutoff Date        : " + monthName + " 1 to 15");
            System.out.println("Total Hours Worked : " + firstCutoffHours);
            System.out.println("Gross Salary       : PHP " + grossFirstCutoff);
            System.out.println("Net Salary         : PHP " + netFirstCutoff);
 
            // Display 2nd cutoff payslip (days 16-end, full deduction breakdown)
            System.out.println("\nCutoff Date        : " + monthName + " 16 to " + lastDayOfMonth);
            System.out.println("Total Hours Worked : " + secondCutoffHours);
            System.out.println("Gross Salary       : PHP " + grossSecondCutoff);
            System.out.println("SSS                : PHP " + sssDeduction);
            System.out.println("PhilHealth         : PHP " + philHealthDeduction);
            System.out.println("Pag-IBIG           : PHP " + pagIbigDeduction);
            System.out.println("Tax                : PHP " + incomeTaxDeduction);
            System.out.println("Total Deductions   : PHP " + totalDeductions);
            System.out.println("Net Salary         : PHP " + netSecondCutoff);
            System.out.println("-----------------------------------");
        }
    }
 
    // =======================================================
    // COMPUTE HOURS WORKED
    // Determines payable hours for a single attendance record by applying MotorPH's official time-tracking rules.
    //
    // Rules applied step by step:
    //   1. Logout is capped at 5:00 PM - no overtime is paid.
    //   2. If login is at or before 8:10 AM (grace period), the effective start time is set to exactly 8:00 AM.
    //      If login is after 8:10 AM (ex. 8:30), the actual login time is used as the start - minutes are lost.
    //   3. If effective login >= effective logout, return 0.
    //   4. A 60-minute unpaid lunch break is deducted from any shift longer than 60 minutes total.
    //
    // Parameters:
    //   rawTimeIn  - the actual login time read from the CSV
    //   rawTimeOut - the actual logout time read from the CSV
    // Returns: payable hours as a double (ex. 7.5 = 7h 30m)
    // =======================================================
    static double computeHours(LocalTime rawTimeIn, LocalTime rawTimeOut) {
 
        LocalTime shiftStartTime = LocalTime.of(8, 0);   // Shift opens at 8:00 AM
        LocalTime graceEndTime   = LocalTime.of(8, 10);  // Grace window closes at 8:10 AM
        LocalTime shiftEndTime   = LocalTime.of(17, 0);  // Shift closes at 5:00 PM
 
        // Step 1: Overtime is not counted -- cap logout at 5:00 PM
        LocalTime effectiveTimeOut = rawTimeOut.isAfter(shiftEndTime)
                ? shiftEndTime   // Use 5:00 PM if employee stayed late
                : rawTimeOut;    // Otherwise use actual logout time
 
        // Step 2: Apply grace period to the login time.
        // Logins at or before 8:10 AM count from 8:00 AM (not penalized).
        // Logins after 8:10 AM use the actual login time (minutes are docked).
        LocalTime effectiveTimeIn = rawTimeIn.isAfter(graceEndTime)
                ? rawTimeIn       // Late arrival - use actual login time
                : shiftStartTime; // On time or within grace - start from 8:00 AM
 
        // Step 3: If the employee's effective start is at or after their capped logout, they have no payable time for this day
        if (!effectiveTimeIn.isBefore(effectiveTimeOut)) return 0;
 
        // Step 4: Compute total minutes between effective in and effective out
        long totalMinutesBetween = Duration.between(effectiveTimeIn, effectiveTimeOut)
                .toMinutes();
 
        // Step 5: Deduct 60-minute unpaid lunch break from shifts over 60 minutes
        // A shift of 60 minutes or less does not have a lunch break deducted
        long paidMinutes = totalMinutesBetween > 60
                ? totalMinutesBetween - 60 // Deduct lunch for normal shifts
                : totalMinutesBetween;     // No deduction for very short shifts
 
        // Step 6: Convert paid minutes to decimal hours without rounding
        return paidMinutes / 60.0;
    }
 
    // =======================================================
    // COMPUTE SSS CONTRIBUTION
    // Looks up the employee's SSS monthly contribution from the 2024 SSS contribution schedule (bracket table).
    // The contribution is a fixed amount determined by which salary range the monthly gross falls into.
    //
    // Parameter : monthlyGross - combined gross for the full month
    // Returns   : SSS contribution amount
    // =======================================================
    static double computeSSS(double monthlyGross) {
        // Each condition checks if the salary falls below a bracket ceiling.
        // The first matching condition returns the fixed contribution for that bracket.
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
        else                           return 1125.00; // Maximum contribution
    }
 
    // =======================================================
    // COMPUTE PHILHEALTH CONTRIBUTION
    // The total PhilHealth premium is 3% of monthly gross.
    // A floor of PHP 300 and ceiling of PHP 1,800 apply to the total premium. The employee pays exactly half.
    //
    // Parameter : monthlyGross - combined gross for the full month
    // Returns   : employee's half of the total PhilHealth premium
    // =======================================================
    static double computePhilHealth(double monthlyGross) {
        // Total premium is 3% of the monthly gross salary
        double totalPremium = monthlyGross * 0.03;
 
        // Enforce the minimum and maximum premium limits
        if (totalPremium < 300)  totalPremium = 300;   // Minimum: PHP 300 total
        if (totalPremium > 1800) totalPremium = 1800;  // Maximum: PHP 1,800 total
 
        // The employee contributes exactly half; the employer covers the other half
        return totalPremium / 2;
    }
 
    // =======================================================
    // COMPUTE PAG-IBIG CONTRIBUTION
    // Employees earning PHP 1,000-1,500 contribute 1%.
    // Employees earning above PHP 1,500 contribute 2%.
    // The employee contribution is capped at PHP 100/month.
    //
    // Parameter : monthlyGross - combined gross for the full month
    // Returns   : Pag-IBIG contribution amount
    // =======================================================
    static double computePagIbig(double monthlyGross) {
        double contribution = 0;
 
        if (monthlyGross >= 1000 && monthlyGross <= 1500) {
            contribution = monthlyGross * 0.01; // 1% for the lower salary tier
        } else if (monthlyGross > 1500) {
            contribution = monthlyGross * 0.02; // 2% for salaries above PHP 1,500
        }
 
        // The monthly contribution cannot exceed the PHP 100 cap
        if (contribution > 100) contribution = 100;
 
        return contribution;
    }
 
    // =======================================================
    // COMPUTE INCOME TAX
    // Uses the TRAIN Law (BIR 2024) progressive tax table to compute the monthly withholding tax.
    //
    // Taxable income is the monthly gross minus the three mandatory government deductions (SSS, PhilHealth, Pag-IBIG).
    // These are deducted before tax to reflect actual BIR rules.
    //
    // Parameters:
    //   monthlyGross - combined gross salary for the month
    //   sss          - SSS contribution (reduces taxable income)
    //   philHealth   - PhilHealth employee share (reduces taxable income)
    //   pagIbig      - Pag-IBIG contribution (reduces taxable income)
    // Returns: monthly withholding tax amount
    // =======================================================
    static double computeIncomeTax(double monthlyGross, double sss,
                                    double philHealth, double pagIbig) {
        // Taxable income = gross minus all mandatory pre-tax deductions
        double taxableIncome = monthlyGross - (sss + philHealth + pagIbig);
 
        // TRAIN Law monthly tax brackets (effective 2023 onwards)
        if (taxableIncome <= 20832) {
            return 0; // Below the taxable threshold - zero tax
 
        } else if (taxableIncome <= 33333) {
            // 20% on the excess over PHP 20,833
            return (taxableIncome - 20833) * 0.20;
 
        } else if (taxableIncome <= 66667) {
            // PHP 2,500 fixed + 25% on the excess over PHP 33,333
            return 2500 + (taxableIncome - 33333) * 0.25;
 
        } else if (taxableIncome <= 166667) {
            // PHP 10,833 fixed + 30% on the excess over PHP 66,667
            return 10833 + (taxableIncome - 66667) * 0.30;
 
        } else if (taxableIncome <= 666667) {
            // PHP 40,833.33 fixed + 32% on the excess over PHP 166,667
            return 40833.33 + (taxableIncome - 166667) * 0.32;
 
        } else {
            // PHP 200,833.33 fixed + 35% on the excess over PHP 666,667
            return 200833.33 + (taxableIncome - 666667) * 0.35;
        }
    }
 
    // =======================================================
    // FIND EMPLOYEE ROW
    // Opens the employee CSV and searches line by line for a row whose first column (Employee #) matches the given employee number.
    // Returns the full split row on match.
    //
    // Parameter : employeeNumber - the number to search for
    // Returns   : String array of the matching row's fields, or null if no match is found
    // =======================================================
    static String[] findEmployeeRow(String employeeNumber) {
        File employeeFile = new File(EMPLOYEE_FILE_PATH);
 
        // Check the file exists before attempting to open it
        if (!employeeFile.exists()) {
            System.out.println("ERROR: Employee file not found at: "
                    + EMPLOYEE_FILE_PATH);
            return null;
        }
 
        try (BufferedReader employeeReader =
                     new BufferedReader(new FileReader(employeeFile))) {
 
            employeeReader.readLine(); // Skip the header row
 
            String csvLine;
            while ((csvLine = employeeReader.readLine()) != null) {
                if (csvLine.trim().isEmpty()) continue;
 
                String[] employeeRow = splitCsvLine(csvLine);
 
                // Need at least 19 columns to safely access index 18
                if (employeeRow.length < 19) continue;
 
                // Column 0 is the Employee # - check for an exact match
                if (employeeRow[0].trim().equals(employeeNumber)) {
                    return employeeRow; // Match found - return this employee's data
                }
            }
 
        } catch (Exception fileReadException) {
            System.out.println("ERROR: Could not read employee file. "
                    + fileReadException.getMessage());
        }
 
        return null; // No employee with that number was found in the file
    }
 
    // =======================================================
    // PARSE TIME
    // Converts a time string from the attendance CSV into a LocalTime object that can be used for arithmetic.
    // Handles both single-digit hours (e.g., "8:05") and two-digit hours (e.g., "17:00").
    //
    // Parameter : timeString - raw time value from the CSV
    // Returns   : LocalTime, or null if the string is invalid
    // =======================================================
    static LocalTime parseTime(String timeString) {
        try {
            // Split the time string on ":" to get hours and minutes separately
            String[] timeParts = timeString.split(":");
            int hour   = Integer.parseInt(timeParts[0].trim()); // Hour part (0-23)
            int minute = Integer.parseInt(timeParts[1].trim()); // Minute part (0-59)
            return LocalTime.of(hour, minute);
        } catch (Exception parseException) {
            // If the string is malformed or missing, return null.
            // The caller is responsible for skipping null records.
            return null;
        }
    }
 
    // =======================================================
    // PARSE DOUBLE
    // Safely converts a string to a numeric double value.
    // Removes comma separators first to handle salary values formatted as "90,000" or "1,500" in the employee CSV.
    //
    // Parameter : rawValue - the string to convert to a number
    // Returns   : numeric double, or 0.0 if conversion fails
    // =======================================================
    static double parseDouble(String rawValue) {
        try {
            // Strip comma separators before parsing (ex. "535.71" stays as-is)
            return Double.parseDouble(rawValue.replace(",", "").trim());
        } catch (NumberFormatException conversionException) {
            // Return 0.0 as a safe default when the string is not a valid number
            return 0.0;
        }
    }
 
    // =======================================================
    // SPLIT CSV LINE
    // Splits one CSV row into an array of field strings while correctly handling fields that are enclosed in double
    // quotes and contain commas inside them.
    //
    // This method reads character-by-character and only splits on commas that appear outside of quoted sections.
    // Double-quote characters are consumed (not included in output).
    //
    // Parameter : csvLine - one raw line read from a CSV file
    // Returns   : array of field strings with quote marks removed
    // =======================================================
    static String[] splitCsvLine(String csvLine) {
        List<String> parsedFields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean insideQuotedField = false; // True when currently inside "..."
 
        for (int charIndex = 0; charIndex < csvLine.length(); charIndex++) {
            char currentChar = csvLine.charAt(charIndex);
 
            if (currentChar == '"') {
                // A quote character toggles whether we are inside a quoted field
                insideQuotedField = !insideQuotedField;
 
            } else if (currentChar == ',' && !insideQuotedField) {
                // A comma outside a quoted field ends the current field value
                parsedFields.add(currentField.toString());
                currentField.setLength(0); // Clear the buffer for the next field
 
            } else {
                // Any other character is part of the current field's value
                currentField.append(currentChar);
            }
        }
 
        // The last field has no trailing comma, so add it manually
        parsedFields.add(currentField.toString());
 
        return parsedFields.toArray(new String[0]);
    }
 
    // =======================================================
    // GET MONTH NAME
    // Converts a numeric month value (1-12) to its full name.
    // Used to produce readable labels in payslip output.
    //
    // Parameter : monthNumber - integer representing the month
    // Returns   : the full month name as a String
    // =======================================================
    static String getMonthName(int monthNumber) {
        switch (monthNumber) {
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
            default: return "Unknown";
        }
    }
}
