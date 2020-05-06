package bankingapp;

import java.util.Scanner;

public class BankingApplication {

    public static void main(String[] args) {
	    char charEntered;
	    Scanner scanner = new Scanner(System.in);
	    BankAccount account = new BankAccount("Dale", "Carniequeue");

        while (true) {
            System.out.println("Hello, " + account.customerName + ". What would you like to do today?");
            System.out.println("--Enter 'd' or 'deposit' to deposit funds into your account");
            System.out.println("--Enter 'e' or 'exit' to close this window");

            while (true) {
                charEntered = scanner.next().charAt(0);

                if (charEntered == 'e' || charEntered == 'd') {
                    break;
                }

                System.out.println("Please enter either 'e' or 'd'");
            }

            if (charEntered == 'd') {
                account.depositFunds();
            }

            if (charEntered == 'e') {
                break;
            }
        }
    }
}

class BankAccount {
    int balance;
    String customerName;
    String customerID;

    BankAccount(String name, String Id) {
        this.customerName = name;
        this.customerID = Id;
        this.balance = 0;
    }

    public int getBalance() {
        return balance;
    }
    public String getCustomerName() {
        return customerName;
    }
    public String getCustomerID() {
        return customerID;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void depositFunds() {
        Scanner scanner = new Scanner(System.in);
        int amountToDeposit;

        System.out.println("What amount would you like to deposit? ");
        amountToDeposit = scanner.nextInt();

        setBalance(getBalance() + amountToDeposit);
        System.out.println("Thank you for your deposit! Your new balance is: " + getBalance());
    }
}
