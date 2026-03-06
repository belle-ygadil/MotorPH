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

        double sss = 0;
        if (monthlyGross < 3250) sss = 135.00;
        else if (monthlyGross < 3750) sss = 157.50;
        else if (monthlyGross < 4250) sss = 180.00;
        else if (monthlyGross < 4750) sss = 202.50;
        else if (monthlyGross < 5250) sss = 225.00;
        else if (monthlyGross < 5750) sss = 247.50;
        else if (monthlyGross < 6250) sss = 270.00;
        else if (monthlyGross < 6750) sss = 292.50;
        else if (monthlyGross < 7250) sss = 315.00;
        else if (monthlyGross < 7750) sss = 337.50;
        else if (monthlyGross < 8250) sss = 360.00;
        else if (monthlyGross < 8750) sss = 382.50;
        else if (monthlyGross < 9250) sss = 405.00;
        else if (monthlyGross < 9750) sss = 427.50;
        else if (monthlyGross < 10250) sss = 450.00;
        else if (monthlyGross < 10750) sss = 472.50;
        else if (monthlyGross < 11250) sss = 495.00;
        else if (monthlyGross < 11750) sss = 517.50;
        else if (monthlyGross < 12250) sss = 540.00;
        else if (monthlyGross < 12750) sss = 562.50;
        else if (monthlyGross < 13250) sss = 585.00;
        else if (monthlyGross < 13750) sss = 607.50;
        else if (monthlyGross < 14250) sss = 630.00;
        else if (monthlyGross < 14750) sss = 652.50;
        else if (monthlyGross < 15250) sss = 675.00;
        else if (monthlyGross < 15750) sss = 697.50;
        else if (monthlyGross < 16250) sss = 720.00;
        else if (monthlyGross < 16750) sss = 742.50;
        else if (monthlyGross < 17250) sss = 765.00;
        else if (monthlyGross < 17750) sss = 787.50;
        else if (monthlyGross < 18250) sss = 810.00;
        else if (monthlyGross < 18750) sss = 832.50;
        else if (monthlyGross < 19250) sss = 855.00;
        else if (monthlyGross < 19750) sss = 877.50;
        else if (monthlyGross < 20250) sss = 900.00;
        else if (monthlyGross < 20750) sss = 922.50;
        else if (monthlyGross < 21250) sss = 945.00;
        else if (monthlyGross < 21750) sss = 967.50;
        else if (monthlyGross < 22250) sss = 990.00;
        else if (monthlyGross < 22750) sss = 1012.50;
        else if (monthlyGross < 23250) sss = 1035.00;
        else if (monthlyGross < 23750) sss = 1057.50;
        else if (monthlyGross < 24250) sss = 1080.00;
        else if (monthlyGross < 24750) sss = 1102.50;
        else sss = 1125.00;

        double philHealth = monthlyGross * 0.03;
        if (philHealth < 300) philHealth = 300;
        if (philHealth > 1800) philHealth = 1800;

        double pagIbig = 0;
        if (monthlyGross >= 1000 && monthlyGross <= 1500)
            pagIbig = monthlyGross * 0.01;
        else if (monthlyGross > 1500)
            pagIbig = monthlyGross * 0.02;
        if (pagIbig > 100)
            pagIbig = 100;

        double taxIncome = monthlyGross - (sss + philHealth/2 + pagIbig/2); 
        double tax = 0;
        if (taxIncome <= 20832) tax = 0;
        else if (taxIncome <= 33333)
            tax = (taxIncome - 20833) * 0.20;
        else if (taxIncome <= 66667)
            tax = 2500 + (taxIncome - 33333) * 0.25;
        else if (taxIncome <= 166667)
            tax = 10833 + (taxIncome - 66667) * 0.30;
        else if (taxIncome <= 666667)
            tax = 40833.33 + (taxIncome - 166667) * 0.32;
        else tax = 200833.33 + (taxIncome - 666667) * 0.35;

        double ratioFirst = grossFirst / monthlyGross;
        double ratioSecond = grossSecond / monthlyGross;

        double sssFirst = 0; 
        double sssSecond = sss;

        double philFirst = 0;
        double philSecond = philHealth;

        double pagIbigFirst = 0;
        double pagIbigSecond = pagIbig;

        double taxFirst = 0;
        double taxSecond = tax;

        
        double netFirst = grossFirst - (sssFirst + philFirst + pagIbigFirst + taxFirst);
        double netSecond = grossSecond - (sssSecond + philSecond + pagIbigSecond + taxSecond);
        
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
        System.out.println("Net salary: PHP " + netFirst);

        System.out.println("\nCutoff Date: " + monthName + " 16 to " + daysInMonth);
        System.out.println("Total hours worked: " + secondHalf);
        System.out.println("Gross salary: PHP " + grossSecond);
        System.out.println("SSS: PHP " + sssSecond);
        System.out.println("PhilHealth: PHP " + philSecond);
        System.out.println("Pag-IBIG: PHP " + pagIbigSecond);
        System.out.println("Tax: PHP " + taxSecond);
        System.out.println("Total deduction: PHP " + (sssSecond + philSecond + pagIbigSecond + taxSecond));
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