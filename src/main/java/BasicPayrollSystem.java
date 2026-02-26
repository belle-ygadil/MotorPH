import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.util.Scanner;

public class BasicPayrollSystem {

    public static void main(String[] args) {

        String empFile = "C:\\Users\\Ms.Shiela\\OneDrive\\NADIA Files\\NetBeansProjects\\FirstJavaProgram\\src\\employee.csv";
        String attFile = "C:\\Users\\Ms.Shiela\\OneDrive\\NADIA Files\\NetBeansProjects\\FirstJavaProgram\\attendanceFile";

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        if (!((username.equals("employee") && password.equals("12345")) ||
              (username.equals("payroll staff") && password.equals("12345")))) {
            System.out.println("Incorrect username and/or password.");
            sc.close();
            return;
        }

        if (username.equals("employee")) {
            System.out.println("\n1. Enter your employee number");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();
            if (choice == 2) {
                sc.close();
                return;
            }

            System.out.print("Enter Employee #: ");
            String inputEmpNo = sc.nextLine(); 

            String empNo = "";
            String firstName = "";
            String lastName = "";
            String birthday = "";
            boolean found = false;

            try (BufferedReader br = new BufferedReader(new FileReader(empFile))) {
                br.readLine();
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = line.split(",");
                    if (data[0].equals(inputEmpNo)) {
                        empNo = data[0];
                        lastName = data[1];
                        firstName = data[2];
                        birthday = data[3];
                        found = true;
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error reading employee file.");
                sc.close();
                return;
            }

            if (!found) {
                System.out.println("Employee number does not exist.");
                sc.close();
                return;
            }

            System.out.println("\n===================================");
            System.out.println("Employee # : " + empNo);
            System.out.println("Employee Name : " + lastName + ", " + firstName);
            System.out.println("Birthday : " + birthday);
            System.out.println("===================================");

        } else if (username.equals("payroll staff")) {
            boolean exitPayroll = false;
            while (!exitPayroll) {
                System.out.println("\n1. Process Payroll");
                System.out.println("2. Exit");
                System.out.print("Choose an option: ");
                int choice = sc.nextInt();
                sc.nextLine();

                if (choice == 2) {
                    exitPayroll = true;
                    continue;
                }

                System.out.println("\n1. One employee");
                System.out.println("2. All employees");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");
                int subChoice = sc.nextInt();
                sc.nextLine();

                if (subChoice == 3) continue;

                if (subChoice == 1) {
                    System.out.print("Enter Employee #: ");
                    String inputEmpNo = sc.nextLine();
                    processPayroll(empFile, attFile, inputEmpNo, false);
                } else if (subChoice == 2) {
                    try (BufferedReader br = new BufferedReader(new FileReader(empFile))) {
                        br.readLine();
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (line.trim().isEmpty()) continue;
                            String[] data = line.split(",");
                            String inputEmpNo = data[0];
                            processPayroll(empFile, attFile, inputEmpNo, true);
                        }
                    } catch (Exception e) {
                        System.out.println("Error reading employee file.");
                    }
                }
            }
        }

        sc.close();
    }

    static void processPayroll(String empFile, String attFile, String empNo, boolean isAll) {
        String firstName = "", lastName = "", birthday = "";
        double hourlyRate = 0;
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(empFile))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data[0].equals(empNo)) {
                    lastName = data[1];
                    firstName = data[2];
                    birthday = data[3];
                    hourlyRate = Double.parseDouble(data[17].replace("\"","").replace(",", ""));
                    found = true;
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading employee file.");
            return;
        }

        if (!found) {
            System.out.println("Employee number does not exist.");
            return;
        }

        System.out.println("\n===================================");
        System.out.println("Employee # : " + empNo);
        System.out.println("Employee Name : " + lastName + ", " + firstName);
        System.out.println("Birthday : " + birthday);
        System.out.println("===================================");

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
        DecimalFormat df = new DecimalFormat("#,##0.00");

        for (int month = 6; month <= 12; month++) {
            double firstHalf = 0;
            double secondHalf = 0;
            int daysInMonth = YearMonth.of(2024, month).lengthOfMonth();

            try (BufferedReader br = new BufferedReader(new FileReader(attFile))) {
                br.readLine();
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = line.split(",");
                    if (!data[0].equals(empNo)) continue;

                    String[] dateParts = data[3].split("/");
                    int recordMonth = Integer.parseInt(dateParts[0]);
                    int day = Integer.parseInt(dateParts[1]);
                    int year = Integer.parseInt(dateParts[2]);
                    if (year != 2024 || recordMonth != month) continue;

                    LocalTime login = LocalTime.parse(data[4].trim(), timeFormat);
                    LocalTime logout = LocalTime.parse(data[5].trim(), timeFormat);
                    double hours = computeHours(login, logout);

                    if (day <= 15) firstHalf += hours;
                    else secondHalf += hours;
                }
            } catch (Exception e) {
                System.out.println("Error reading attendance file.");
                return;
            }

            String monthName = switch (month) {
                case 6 -> "June"; 
                case 7 -> "July"; 
                case 8 -> "August"; 
                case 9 -> "September";
                case 10 -> "October"; 
                case 11 -> "November"; 
                case 12 -> "December"; 
                default -> "";
            };

            double[] firstPay = calculatePayroll(firstHalf, hourlyRate);
            double[] secondPay = calculatePayroll(secondHalf, hourlyRate);

            System.out.println("\nCutoff Date: " + monthName + " 1 to 15");
            System.out.println("Total Hours Worked : " + df.format(firstHalf));
            System.out.println("Gross Salary: PHP " + df.format(firstPay[0]));
            System.out.println("Net Salary: PHP " + df.format(firstPay[5]));

            System.out.println("\nCutoff Date: " + monthName + " 16 to " + daysInMonth);
            System.out.println("Total Hours Worked : " + df.format(secondHalf));
            System.out.println("Gross Salary: PHP " + df.format(secondPay[0]));
            System.out.println("SSS: PHP " + df.format(secondPay[1]));
            System.out.println("PhilHealth: PHP " + df.format(secondPay[2]));
            System.out.println("Pag-IBIG: PHP " + df.format(secondPay[3]));
            System.out.println("Tax: PHP " + df.format(secondPay[4]));
            System.out.println("Total Deductions: PHP " + df.format(secondPay[1]+secondPay[2]+secondPay[3]+secondPay[4]));
            System.out.println("Net Salary: PHP " + df.format(secondPay[5]));
        }

        if (!isAll) System.out.println("===================================");
    }

    static double computeHours(LocalTime login, LocalTime logout) {
        LocalTime graceTime = LocalTime.of(8, 10);
        LocalTime cutoffTime = LocalTime.of(17, 0);
        if (logout.isAfter(cutoffTime)) logout = cutoffTime;
        long minutesWorked = Duration.between(login, logout).toMinutes();
        if (minutesWorked > 60) minutesWorked -= 60;
        else minutesWorked = 0;
        double hours = minutesWorked / 60.0;
        if (!login.isAfter(graceTime)) return 8.0;
        return Math.min(hours, 8.0);
    }

    //contribution rates are simplified and do not reflect actual government rates
    static double[] calculatePayroll(double hoursWorked, double hourlyRate) {
        double gross = hoursWorked * hourlyRate;
        double sss = gross * 0.11;
        double phil = gross * 0.04;
        double pagibig = gross * 0.02;
        double tax = gross * 0.10;
        double net = gross - (sss + phil + pagibig + tax);
        return new double[]{gross, sss, phil, pagibig, tax, net};
    }
}