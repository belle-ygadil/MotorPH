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
                }

                else if (subChoice == 2) {
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
         
            double philHealthTotal = monthlyGross * 0.03;

            if (philHealthTotal < 300) philHealthTotal = 300;
            if (philHealthTotal > 1800) philHealthTotal = 1800;
            double philHealth = philHealthTotal / 2; 
            
            double pagIbig = 0;

            if (monthlyGross >= 1000 && monthlyGross <= 1500)
                pagIbig = monthlyGross * 0.01;
            else if (monthlyGross > 1500)
                pagIbig = monthlyGross * 0.02;
            if (pagIbig > 100)
                pagIbig = 100;
            double taxIncome = monthlyGross - (sss + philHealth + pagIbig);
            
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
            else
                tax = 200833.33 + (taxIncome - 666667) * 0.35;
            
            double netFirst = grossFirst;
            double netSecond = grossSecond;
            double totalDeduction = sss + philHealth + pagIbig + tax;
            
            System.out.println("\nCutoff Date: " + month + " 1 to 15 ");
            System.out.println("Total hours worked: "+ firstHalf);
            System.out.println("Gross salary: PHP "+ grossFirst);
            System.out.println("Net salary: PHP "+ netFirst);
            
            System.out.println("\nCutoff Date: " + month + " 16 to " + daysInMonth);
            System.out.println("Total hours worked: "+ secondHalf);
            System.out.println("Gross salary: "+ grossSecond);
            System.out.println("SSS: PHP "+ sss);
            System.out.println("PhilHealth: PHP "+ philHealth);
            System.out.println("Pag-IBIG: PHP "+ pagIbig);
            System.out.println("Tax: PHP"+ tax);
            System.out.println("Total deduction: PHP "+ totalDeduction);
            System.out.println("Net salary: PHP "+ netSecond);
        }

        if (!isAll) System.out.println("===================================");
    }

    static double computeHours(LocalTime login, LocalTime logout) {

        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(17, 0);
        LocalTime grace = LocalTime.of(8, 5);

        if (!login.isAfter(grace)) login = start;
        if (login.isBefore(start)) login = start;
        if (logout.isAfter(end)) logout = end;
        if (logout.isBefore(start)) return 0;

        long minutesWorked = Duration.between(login, logout).toMinutes();

        if (minutesWorked < 0) return 0;

        return minutesWorked / 60.0;
    }
}