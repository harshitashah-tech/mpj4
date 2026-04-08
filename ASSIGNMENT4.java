// package HelloWorld;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

class InvalidCustomerIdException extends Exception {
    public InvalidCustomerIdException(String message) {
        super(message);
    }
}

class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}

class MinimumBalanceException extends Exception {
    public MinimumBalanceException(String message) {
        super(message);
    }
}

class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

class Customer {
    private final int customerId;
    private final String customerName;
    private double amount;

    public Customer(int customerId, String customerName, double amount) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }

    public void withdraw(double withdrawalAmount) {
        amount -= withdrawalAmount;
    }

    public String toRecord() {
        return customerId + "," + customerName + "," + String.format("%.2f", amount);
    }
}

class BankingSystem {
    private static final double MIN_BALANCE = 1000.0;
    private final Map<Integer, Customer> customers = new HashMap<>();
    private final File outputFile;

    public BankingSystem(String fileName) {
        this.outputFile = new File(fileName);
    }

    public void createAccount(int cid, String cname, double amount)
            throws InvalidCustomerIdException, InvalidAmountException, MinimumBalanceException {
        validateCustomerId(cid);
        validatePositiveAmount(amount);
        if (amount < MIN_BALANCE) {
            throw new MinimumBalanceException("Account should be created with minimum Rs. 1000.");
        }
        customers.put(cid, new Customer(cid, cname, amount));
        writeAllCustomersToFile();
    }

    public void withdrawAmount(int cid, double withdrawalAmount)
            throws InvalidCustomerIdException, InvalidAmountException, InsufficientFundsException {
        validateCustomerId(cid);
        validatePositiveAmount(withdrawalAmount);

        Customer customer = customers.get(cid);
        if (customer == null) {
            throw new InvalidCustomerIdException("No customer found with cid " + cid + ".");
        }

        if (withdrawalAmount > customer.getAmount()) {
            throw new InsufficientFundsException("Withdrawal amount cannot be greater than total amount.");
        }

        customer.withdraw(withdrawalAmount);
        writeAllCustomersToFile();
    }

    public void displayCustomers() {
        if (customers.isEmpty()) {
            System.out.println("No customer records available.");
            return;
        }

        System.out.println("\nCustomer Records:");
        for (Customer customer : customers.values()) {
            System.out.printf("cid=%d, cname=%s, amount=%.2f%n",
                    customer.getCustomerId(),
                    customer.getCustomerName(),
                    customer.getAmount());
        }
    }

    private void validateCustomerId(int cid) throws InvalidCustomerIdException {
        if (cid < 1 || cid > 20) {
            throw new InvalidCustomerIdException("cid should be in the range 1 to 20.");
        }
    }

    private void validatePositiveAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Entered amount should be positive.");
        }
    }

    private void writeAllCustomersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, false))) {
            writer.write("cid,cname,amount");
            writer.newLine();
            for (Customer customer : customers.values()) {
                writer.write(customer.toRecord());
                writer.newLine();
            }
        } catch (IOException exception) {
            System.out.println("File write error: " + exception.getMessage());
        }
    }
}

public class ASSIGNMENT4 {
    private static int readInteger(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextInt();
            } catch (InputMismatchException exception) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
            }
        }
    }

    private static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextDouble();
            } catch (InputMismatchException exception) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("ASSIGNMENT 4 - Banking System (File I/O + Exception Handling)");
        BankingSystem bankingSystem = new BankingSystem("ASSIGNMENT4/customer_records.txt");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n1. Create Account");
                System.out.println("2. Withdraw Amount");
                System.out.println("3. Display Customers");
                System.out.println("4. Exit");

                int choice = readInteger(scanner, "Enter choice: ");
                scanner.nextLine();

                try {
                    switch (choice) {
                        case 1:
                            int cid = readInteger(scanner, "Enter cid (1-20): ");
                            scanner.nextLine();
                            System.out.print("Enter customer name: ");
                            String cname = scanner.nextLine();
                            double amount = readDouble(scanner, "Enter amount: ");
                            bankingSystem.createAccount(cid, cname, amount);
                            System.out.println("Account created successfully.");
                            break;

                        case 2:
                            int withdrawalCid = readInteger(scanner, "Enter cid: ");
                            double withdrawalAmount = readDouble(scanner, "Enter withdrawal amount: ");
                            bankingSystem.withdrawAmount(withdrawalCid, withdrawalAmount);
                            System.out.println("Amount withdrawn successfully.");
                            break;

                        case 3:
                            bankingSystem.displayCustomers();
                            break;

                        case 4:
                            System.out.println("Exiting application.");
                            return;

                        default:
                            System.out.println("Invalid choice. Please select from 1 to 4.");
                    }
                } catch (InvalidCustomerIdException
                        | InvalidAmountException
                        | MinimumBalanceException
                        | InsufficientFundsException exception) {
                    System.out.println("Error: " + exception.getMessage());
                }
            }
        }
    }
}
