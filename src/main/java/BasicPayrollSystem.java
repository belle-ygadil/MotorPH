import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class BasicPayrollSystem {
    public static void main(String[] args) {
        String empFile = "C:\\Users\\Ms.Shiela\\OneDrive\\NADIA Files\\NetBeansProjects\\Motorph\\src\\main\\java\\employee.csv";
        String attFile = "C:\\Users\\Ms.Shiela\\OneDrive\\NADIA Files\\NetBeansProjects\\Motorph\\src\\main\\java\\attendanceFile";

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
        }

        else if (username.equals("payroll staff")) {
     
        System.out.println("\n1. Process Payroll\n2. Exit");
        System.out.print("Choose an option: ");
        int choice = Integer.parseInt(sc.nextLine());
        if (choice != 1) return;

        System.out.println("\n1. One employee\n2. All employees\n3. Exit");
        System.out.print("Choose an option: ");
        int subChoice = Integer.parseInt(sc.nextLine());
        if (subChoice != 1) return;

        System.out.print("Enter Employee #: ");
        String inputEmpNo = sc.nextLine();

        String empNo = "", firstName = "", lastName = "", birthday = "";
        boolean found = false;
        double hourlyRate = 975.0 / 8; //This is wrong 

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
            return;
        }

        if (!found) {
            System.out.println("Employee does not exist.");
            return;
        }

        System.out.println("\n===================================");
        System.out.println("Employee # : " + empNo);
        System.out.println("Employee Name : " + lastName + ", " + firstName);
        System.out.println("Birthday : " + birthday);
        System.out.println("===================================");

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");

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

            double grossFirst = firstHalf * hourlyRate;
            double grossSecond = secondHalf * hourlyRate;
            double monthlyGross = grossFirst + grossSecond;

            double sssTotal = 1125;
            double philTotal = 900;
            double pagibigTotal = 100;
            double taxableIncome = monthlyGross - (sssTotal + philTotal + pagibigTotal);
            double taxTotal = taxableIncome * 0.25;

            double ratioSecond = grossSecond / monthlyGross;
            double sssSecond = sssTotal * ratioSecond;
            double philSecond = philTotal * ratioSecond;
            double pagibigSecond = pagibigTotal * ratioSecond;
            double taxSecond = taxTotal * ratioSecond;
            double netSecond = grossSecond - (sssSecond + philSecond + pagibigSecond + taxSecond);

                        
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

            System.out.println("\nCutoff Date: " + monthName + " 1 to 15");
            System.out.println("Total hours worked: " + firstHalf);
            System.out.println("Gross salary: PHP " + grossFirst);
            System.out.println("Net salary: PHP " + grossFirst);

            System.out.println("\nCutoff Date: " + monthName + " 16 to " + daysInMonth);
            System.out.println("Total hours worked: " + secondHalf);
            System.out.println("Gross salary: PHP " + grossSecond);
            System.out.println("SSS: PHP " + sssSecond);
            System.out.println("PhilHealth: PHP " + philSecond);
            System.out.println("Pag-IBIG: PHP " + pagibigSecond);
            System.out.println("Tax: PHP " + taxSecond);
            System.out.println("Total deduction: PHP " + (sssSecond + philSecond + pagibigSecond + taxSecond));
            System.out.println("Net salary: PHP " + netSecond);
        }
    }
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
}