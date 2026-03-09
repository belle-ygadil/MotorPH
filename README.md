# MotorPH
Payroll system for MotorPH

**Team Details**

Ysabelle Gadil – Developed the initial version of the payroll system by writing the original code and implementing the core functionalities of the program. She established the main structure of the system, including the payroll computation process and the handling of employee data. She also collaborated with the team in reviewing the code and organizing the project files in GitHub.

John Mervin Layson – Reviewed and revised the initial code of the payroll system to improve its functionality and accuracy. He identified issues within the program, implemented corrections, and refined the code to ensure proper payroll calculations. He also collaborated with the team in managing and organizing the project files in the GitHub repository.

Kera Froilan Montejo – Collaborated with Ysabelle in revising and improving parts of the code to help ensure the system functions correctly. She also worked with the team in organizing and managing the project files in the GitHub repository to maintain proper structure and accessibility of the project.


**Program Details**

The MotorPH Basic Payroll System is a Java console-based program designed to read employee information and attendance records from CSV files and generate payroll summaries. The system processes employee work hours and calculates salaries and government deductions for the payroll periods from June to December 2024.

The program uses two CSV files as its data sources. The first file contains the employee details such as employee number, name, birthday, and hourly rate. The second file contains the attendance records including the employee number, date, time-in, and time-out entries.

By combining the information from these files, the system calculates the total hours worked, gross salary, government deductions, and net salary for each payroll cutoff.

_User Login_

When the program starts, the user is prompted to enter a username and password. The system supports two types of users: Employee and Payroll Staff.

Employees are allowed to view their personal information, while payroll staff are able to process payroll for individual employees or for all employees in the system.

If the login credentials are incorrect, the program will terminate and display an error message.

_Employee Information Retrieval_

When an employee number is entered, the program reads the Employee Details CSV file using file reading functions. The system scans each row of the file until it finds a matching employee number.

Once the employee is found, the program retrieves and displays the following information:
Employee Number
Employee Name
Birthday
If the employee number does not exist in the file, the program informs the user that the employee record cannot be found.

_Attendance Processing_

The program reads the Attendance Records CSV file to obtain daily work logs. Each record contains the employee number, the date of attendance, and the corresponding time-in and time-out values.

The system processes attendance records for the year 2024 and only considers records from June to December, which is the required payroll period for the system.

For each employee, the system checks every attendance entry and calculates the total hours worked for each payroll cutoff.

_Payroll Cutoff Periods_

Each month is divided into two payroll cutoff periods.

The first cutoff covers the period from the 1st to the 15th day of the month, while the second cutoff covers the 16th day until the last day of the month.

The program separates the hours worked within these periods and calculates the gross salary for each cutoff individually.

_Hours Worked Calculation_

The system calculates the number of hours worked using specific company rules.

Only working hours between 8:00 AM and 5:00 PM are considered valid. Any time beyond 5:00 PM is not included in the calculation.

Employees are given a grace period until 8:10 AM. If the employee logs in at or before 8:10 AM, the system treats the login time as exactly 8:00 AM.

A one-hour lunch break is automatically deducted from the total working time if the employee worked for more than one hour during the day.

The maximum number of working hours counted in a day is eight hours.

The program uses Java's LocalTime and Duration classes to compute the total working time based on the login and logout records.

_Gross Salary Calculation_

The gross salary is calculated by multiplying the total hours worked by the employee’s hourly rate.

First, the program computes the gross salary for the first cutoff and the second cutoff separately. These values are then combined to determine the employee’s monthly gross salary.

The monthly gross salary is important because government deductions are computed based on the total earnings for the entire month.

_Government Deductions_

After calculating the combined monthly gross salary, the system computes the required government deductions.

The deductions included in the system are SSS, PhilHealth, Pag-IBIG, and Income Tax.

SSS contributions are determined using a salary bracket table that assigns a fixed employee contribution based on the employee’s monthly gross salary.

PhilHealth contributions are computed as 3% of the monthly gross salary, but the premium is subject to a minimum of PHP 300 and a maximum of PHP 1800. The employee only pays 50% of the total premium.

Pag-IBIG contributions are calculated based on salary brackets. Employees earning between PHP 1000 and PHP 1500 contribute 1% of their salary, while employees earning above PHP 1500 contribute 2%, with a maximum contribution of PHP 100.

Income tax is computed using the TRAIN Law tax brackets, where the taxable income is calculated after subtracting the SSS, PhilHealth, and Pag-IBIG contributions from the monthly gross salary.

_Net Salary Computation_

The net salary represents the employee’s final take-home pay after deductions.

The first payroll cutoff does not include any deductions. Therefore, the employee receives the full gross salary for the first cutoff period.

All government deductions are applied during the second cutoff, where the total deductions are subtracted from the second cutoff gross salary to produce the final net salary.

_Program Output_

For each employee, the system displays the payroll details for every month from June to December.

The output includes the payroll cutoff period, total hours worked, gross salary, government deductions, total deductions, and net salary.

This information allows payroll staff to review the complete salary breakdown for each payroll period.


**Project Plan File:**

https://docs.google.com/spreadsheets/d/1lp4qZaSsJ_TknQWaCTUAn1KcZmpZVeNnY6DuaN_YkqY/edit?usp=drivesdk
