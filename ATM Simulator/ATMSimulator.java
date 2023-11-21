package com.compozent_internship;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ATMSimulator {
	private Map<String, Account> accounts;
	private Account currentUser;
	private Scanner scanner;
	private int loginAttempts;

	public ATMSimulator() {
		accounts = new HashMap<>();
		scanner = new Scanner(System.in);
		loginAttempts = 0;
	}

	public static void main(String[] args) {
		ATMSimulator atm = new ATMSimulator();
		atm.run();
	}

	public void run() {
		while (true) {
			System.out.println("\nWelcome to the ATM Simulator!");
			System.out.println("1. Create Account");
			System.out.println("2. Log In");
			System.out.println("0. Exit");

			int choice = getIntInput("Enter your choice: ");

			switch (choice) {
			case 1:
				createAccount();
				break;
			case 2:
				logIn();
				break;
			case 0:
				System.out.println("Thank you for using the ATM Simulator. Goodbye!");
				System.exit(0);
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private int getIntInput(String prompt) {
		System.out.print(prompt);
		while (!scanner.hasNextInt()) {
			System.out.println("Invalid input. Please enter a valid integer.");
			scanner.next(); // consume the invalid input
		}
		return scanner.nextInt();
	}

	private double getDoubleInput(String prompt) {
		System.out.print(prompt);
		while (!scanner.hasNextDouble()) {
			System.out.println("Invalid input. Please enter a valid double.");
			scanner.next(); // consume the invalid input
		}
		return scanner.nextDouble();
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt);
		return scanner.next();
	}

	private void createAccount() {
		System.out.println("Create a new account");

		String username;
		String password;
		String confirmedPassword;
		String pin;
		String confirmedPin;

		do {
			username = getStringInput("\nEnter username: ");
			if (!isValidUsername(username)) {
				System.out.println(
						"NOTE: username should have atleast 5 Character including alphanumeric lowercase characters");
				// System.out.println("Invalid username. Please use alphanumeric lowercase
				// characters.");
			}
		} while (!isValidUsername(username));

		do {
			password = getStringInput("\nEnter password: ");
			confirmedPassword = getStringInput("Confirm password: ");
			if (!isValidPassword(password) || !password.equals(confirmedPassword)) {
				System.out.println(
						"NOTE: password Should should have atleast 5 Character including at least one uppercase letter, one lowercase letter, one numeric digit, and one special character.");
				// System.out.println("Invalid password. Please meet the criteria and match the
				// confirmation.");
			}
		} while (!isValidPassword(password) || !password.equals(confirmedPassword));

		do {
			pin = getStringInput("\nEnter PIN: ");
			confirmedPin = getStringInput("Confirm PIN: ");
			if (!isValidPin(pin) || !pin.equals(confirmedPin)) {
				System.out.println(
						"NOTE: PIN Should have exactly 5 characters with at least one alphabet, one numeric digit, and one special character");
				// System.out.println("Invalid PIN. Please meet the criteria and match the
				// confirmation.");
			}
		} while (!isValidPin(pin) || !pin.equals(confirmedPin));

		if (!accounts.containsKey(username)) {
			Account account = new Account(username, password, pin);
			accounts.put(username, account);
			System.out.println("Account created successfully!");
		} else {
			System.out.println("Account with that username already exists. Please choose a different username.");
		}
	}

	private boolean isValidUsername(String username) {
		// Alphanumeric (lowercase)
		return username.matches("^[a-z0-9]+$");
	}

	private boolean isValidPassword(String password) {
		// Alphanumeric with at least one uppercase, one lowercase, one numeric, and one
		// special character
		return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$");
	}

	private boolean isValidPin(String pin) {
		// Exactly 5 characters with at least one alphabet, one numeric, and one special
		// character
		return pin.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{5}$");
	}

	private void logIn() {
		if (loginAttempts >= 3) {
			System.out.println("Too many unsuccessful login attempts. Exiting the ATM Simulator.");
			System.exit(0);
		}

		System.out.println("Log into your account");

		String username = getStringInput("Enter username: ");
		String password = getStringInput("Enter password: ");
		String pin = getStringInput("Enter PIN: ");

		if (accounts.containsKey(username) && accounts.get(username).checkCredentials(password, pin)) {
			currentUser = accounts.get(username);
			loginAttempts = 0; // Reset login attempts on successful login
			showLoggedInMenu();
		} else {
			System.out.println("Invalid username, password, or PIN. Please try again.");
			loginAttempts++;
			logIn(); // Recursive call to allow for a limited number of login attempts
		}
	}

	private void showLoggedInMenu() {
		while (true) {
			System.out.println("\nWelcome, " + currentUser.getUsername() + "!");
			System.out.println("1. Deposit");
			System.out.println("2. Withdraw");
			System.out.println("3. Transfer Funds");
			System.out.println("4. View Balance");
			System.out.println("5. View Transaction History");
			System.out.println("0. Log Out");

			int choice = getIntInput("Enter your choice: ");

			switch (choice) {
			case 1:
				deposit();
				break;
			case 2:
				withdraw();
				break;
			case 3:
				transferFunds();
				break;
			case 4:
				viewBalance();
				break;
			case 5:
				viewTransactionHistory();
				break;
			case 0:
				currentUser = null;
				return;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private void deposit() {
		double amount = getDoubleInput("Enter the amount to deposit: ");
		currentUser.deposit(amount);
		recordTransaction("Deposit", amount);
		System.out.println("Deposit successful. Current balance: " + currentUser.getBalance());
	}

	private void withdraw() {
		double amount = getDoubleInput("Enter the amount to withdraw: ");
		if (currentUser.withdraw(amount)) {
			recordTransaction("Withdrawal", amount);
			System.out.println("Withdrawal successful. Current balance: " + currentUser.getBalance());
		} else {
			System.out.println("Insufficient funds. Please try again with a lower amount.");
		}
	}

	private void transferFunds() {
		String recipientUsername = getStringInput("Enter the recipient's username: ");
		if (accounts.containsKey(recipientUsername)) {
			if (currentUser.getUsername().equals(recipientUsername)) {
				System.out.println("Cannot transfer funds to the same account. Please enter a different recipient.");
			} else {
				double amount = getDoubleInput("Enter the amount to transfer: ");
				if (currentUser.transfer(accounts.get(recipientUsername), amount)) {
					recordTransaction("Transfer to " + recipientUsername, amount);
					System.out.println("Transfer successful. Current balance: " + currentUser.getBalance());
				} else {
					System.out.println("Insufficient funds. Please try again with a lower amount.");
				}
			}
		} else {
			System.out.println("Recipient account not found. Please check the username and try again.");
		}
	}

	private void viewTransactionHistory() {
		List<Account.Transaction> transactions = currentUser.getTransactions();
		if (transactions.isEmpty()) {
			System.out.println("No transaction history available.");
		} else {
			System.out.println("Transaction History for " + currentUser.getUsername() + ":");
			for (Account.Transaction transaction : transactions) {
				System.out.println(transaction);
			}
		}
	}

	private void recordTransaction(String type, double amount) {
		currentUser.recordTransaction(type, amount);
	}

	private void viewBalance() {
		System.out.println("Current balance: " + currentUser.getBalance());
	}

	private static class Account {
		private String username;
		private String password;
		private String pin;
		private double balance;
		private List<Transaction> transactions;

		public Account(String username, String password, String pin) {
			this.username = username;
			this.password = hashPassword(password); // Hash the password
			this.pin = pin; // Store the PIN as-is (without hashing for simplicity)
			this.balance = 0.0;
			this.transactions = new ArrayList<>(); // Initialize the transactions list
		}

		public List<Transaction> getTransactions() {
			return transactions;
		}

		private void recordTransaction(String type, double amount) {
			transactions.add(new Transaction(type, amount));
		}

		public String getUsername() {
			return username;
		}

		public double getBalance() {
			return balance;
		}

		public void deposit(double amount) {
			balance += amount;
		}

		public boolean withdraw(double amount) {
			if (amount <= balance) {
				balance -= amount;
				return true;
			}
			return false;
		}

		public boolean transfer(Account recipient, double amount) {
			if (amount <= balance) {
				withdraw(amount);
				recipient.deposit(amount);
				// recordTransaction("Transfer to " + recipient.getUsername() + " ($" + amount +
				// ")", -amount);
				recipient.recordTransaction("Transfer from " + getUsername() + " ($" + amount + ")", amount);
				return true;
			}
			return false;
		}

		public boolean checkCredentials(String password, String inputPin) {
			return this.password.equals(hashPassword(password)) && this.pin.equals(inputPin);
		}

		private String hashPassword(String password) {
			try {
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				byte[] hashedBytes = md.digest(password.getBytes());

				// Convert the byte array to a hexadecimal string
				StringBuilder sb = new StringBuilder();
				for (byte b : hashedBytes) {
					sb.append(String.format("%02x", b));
				}
				return sb.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			}
		}

		private static class Transaction {
			private String type;
			private double amount;
			private Date timestamp;

			public Transaction(String type, double amount) {
				this.type = type;
				this.amount = amount;
				this.timestamp = new Date();
			}

			@Override
			public String toString() {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return "[" + dateFormat.format(timestamp) + "] " + type + ": $" + amount;
			}
		}
	}
}
