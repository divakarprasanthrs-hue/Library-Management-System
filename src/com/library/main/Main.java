package com.library.main;

import com.library.exception.*;
import com.library.model.*;
import com.library.service.Library;
import com.library.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Library Management System console application.
 * Fulfills all Specification 2 requirements:
 * - Menu Options:
 *   1. Add Book
 *   2. Add Member
 *   3. Borrow Book
 *   4. Return Book
 *   5. Display Available Books
 *   6. Exit
 * - Expected Outputs:
 *   - Show borrow/return receipt.
 *   - Display updated stock after each transaction.
 *   - Show fines (if applicable).
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class Main {

    private static final Library library = new Library("Karpagam College Central Library");

    public static void main(String[] args) {
        // Pre-populate with initial demonstration catalog and members
        library.seedInitialData();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        printHeader();

        while (running) {
            printMenu();
            int choice = ValidationUtil.readIntInRange(scanner, "Enter your choice [1-6]: ", 1, 6);
            System.out.println();

            switch (choice) {
                case 1 -> handleAddBook(scanner);
                case 2 -> handleAddMember(scanner);
                case 3 -> handleBorrowBook(scanner);
                case 4 -> handleReturnBook(scanner);
                case 5 -> handleDisplayAvailableBooks();
                case 6 -> {
                    running = false;
                    handleExit();
                }
                default -> System.out.println("  [!] Invalid choice. Please try again.");
            }
            System.out.println();
        }

        scanner.close();
    }

    private static void printHeader() {
        System.out.println("================================================================================");
        System.out.println("             23CSR306 JAVA PROGRAMMING - ASSIGNMENT II                         ");
        System.out.println("             SPECIFICATION 2: LIBRARY MANAGEMENT SYSTEM                        ");
        System.out.println("             Student: Cibi K. | Register Number: 717825F112                    ");
        System.out.println("================================================================================");
    }

    private static void printMenu() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("                            MAIN MENU OPTIONS                                   ");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  1. Add Book");
        System.out.println("  2. Add Member");
        System.out.println("  3. Borrow Book");
        System.out.println("  4. Return Book");
        System.out.println("  5. Display Available Books");
        System.out.println("  6. Exit");
        System.out.println("--------------------------------------------------------------------------------");
    }

    /**
     * Menu Option 1: Add Book
     */
    private static void handleAddBook(Scanner scanner) {
        System.out.println(">>> [1. ADD NEW BOOK]");
        String bookId = ValidationUtil.readNonEmptyString(scanner, "Enter Book ID (e.g. BK-105): ");
        String title = ValidationUtil.readNonEmptyString(scanner, "Enter Book Title: ");
        String authorName = ValidationUtil.readNonEmptyString(scanner, "Enter Author Name: ");
        System.out.print("Enter Author Email (optional, press Enter to skip): ");
        String authorEmail = scanner.nextLine().trim();
        String isbn = ValidationUtil.readNonEmptyString(scanner, "Enter ISBN: ");
        int totalCopies = ValidationUtil.readPositiveInt(scanner, "Enter Total Copies (Stock): ");

        try {
            Author author = new Author(authorName, authorEmail);
            Book book = new Book(bookId, title, author, isbn, totalCopies);
            library.addBook(book);
            System.out.println("\n  [SUCCESS] Book added successfully to the library catalog!");
            System.out.printf("  Added: %s | Total Stock: %d copies%n", book.getTitle(), book.getTotalCopies());
        } catch (DuplicateEntityException | IllegalArgumentException e) {
            System.out.println("\n  [FAILED TO ADD BOOK] " + e.getMessage());
        }
    }

    /**
     * Menu Option 2: Add Member
     */
    private static void handleAddMember(Scanner scanner) {
        System.out.println(">>> [2. ADD NEW MEMBER]");
        String memberId = ValidationUtil.readNonEmptyString(scanner, "Enter Member ID (e.g. MEM-003 or PREM-003): ");
        String name = ValidationUtil.readNonEmptyString(scanner, "Enter Member Name: ");
        String phone = ValidationUtil.readNonEmptyString(scanner, "Enter Contact Phone: ");

        System.out.println("Select Membership Type:");
        System.out.println("  1. Regular Member (Max: 3 books, Loan: 14 days, Fine: Rs. 5.00/day)");
        System.out.println("  2. Premium Member (Max: 6 books, Loan: 30 days, Fine: Rs. 2.00/day)");
        int typeChoice = ValidationUtil.readIntInRange(scanner, "Select [1 or 2]: ", 1, 2);

        try {
            Member member;
            if (typeChoice == 2) {
                String tier = ValidationUtil.readNonEmptyString(scanner, "Enter Premium Tier (e.g. Gold, Platinum, VIP): ");
                member = new PremiumMember(memberId, name, phone, tier);
            } else {
                member = new Member(memberId, name, phone);
            }

            library.addMember(member);
            System.out.println("\n  [SUCCESS] Member successfully registered in the system!");
            System.out.printf("  Registered: %s as %s%n", member.getName(), member.getMembershipType());
        } catch (DuplicateEntityException | IllegalArgumentException e) {
            System.out.println("\n  [FAILED TO ADD MEMBER] " + e.getMessage());
        }
    }

    /**
     * Menu Option 3: Borrow Book
     */
    private static void handleBorrowBook(Scanner scanner) {
        System.out.println(">>> [3. BORROW BOOK]");
        String memberId = ValidationUtil.readNonEmptyString(scanner, "Enter Member ID: ");
        String bookId = ValidationUtil.readNonEmptyString(scanner, "Enter Book ID: ");
        LocalDate borrowDate = ValidationUtil.readDateWithDefaultToday(
                scanner, "Enter Borrow Date (dd-MM-yyyy, or press Enter for today): ");

        try {
            BorrowTransaction transaction = library.borrowBook(memberId, bookId, borrowDate);
            System.out.println("\n  [SUCCESS] Book borrowing transaction approved!\n");
            // Fulfills Expected Output 1: Show borrow receipt & updated stock
            System.out.print(transaction.generateReceipt());
        } catch (MemberNotFoundException | BookNotFoundException |
                 BorrowLimitExceededException | InsufficientStockException e) {
            System.out.println("\n  [BORROW TRANSACTION REJECTED]");
            System.out.println("  Reason: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n  [UNEXPECTED ERROR] " + e.getMessage());
        }
    }

    /**
     * Menu Option 4: Return Book
     */
    private static void handleReturnBook(Scanner scanner) {
        System.out.println(">>> [4. RETURN BOOK]");
        System.out.println("Return Options:");
        System.out.println("  1. Return using Transaction ID (e.g. TXN-1001)");
        System.out.println("  2. Return using Member ID and Book ID");
        int retMode = ValidationUtil.readIntInRange(scanner, "Select mode [1 or 2]: ", 1, 2);

        try {
            ReturnTransaction returnTxn;
            if (retMode == 1) {
                String txnId = ValidationUtil.readNonEmptyString(scanner, "Enter Transaction ID: ");
                LocalDate returnDate = ValidationUtil.readDateWithDefaultToday(
                        scanner, "Enter Return Date (dd-MM-yyyy, or press Enter for today): ");
                returnTxn = library.returnBook(txnId, returnDate);
            } else {
                String memberId = ValidationUtil.readNonEmptyString(scanner, "Enter Member ID: ");
                String bookId = ValidationUtil.readNonEmptyString(scanner, "Enter Book ID: ");
                LocalDate returnDate = ValidationUtil.readDateWithDefaultToday(
                        scanner, "Enter Return Date (dd-MM-yyyy, or press Enter for today): ");
                returnTxn = library.returnBookByMemberAndBook(memberId, bookId, returnDate);
            }

            System.out.println("\n  [SUCCESS] Book return processed successfully!\n");
            // Fulfills Expected Output: Show return receipt, updated stock, and fines (if applicable)
            System.out.print(returnTxn.generateReceipt());

        } catch (InvalidTransactionException e) {
            System.out.println("\n  [RETURN TRANSACTION REJECTED]");
            System.out.println("  Reason: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n  [UNEXPECTED ERROR] " + e.getMessage());
        }
    }

    /**
     * Menu Option 5: Display Available Books
     */
    private static void handleDisplayAvailableBooks() {
        System.out.println(">>> [5. DISPLAY AVAILABLE BOOKS]");
        List<Book> availableBooks = library.getAvailableBooks();

        if (availableBooks.isEmpty()) {
            System.out.println("  [!] No books are currently available in stock.");
            return;
        }

        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-8s | %-42s | %-22s | %-15s | %-5s | %-5s |%n",
                "BOOK ID", "TITLE", "AUTHOR", "ISBN", "AVAIL", "TOTAL");
        System.out.println("-----------------------------------------------------------------------------------------------------------------");

        for (Book b : availableBooks) {
            System.out.printf("| %-8s | %-42.42s | %-22.22s | %-15s | %-5d | %-5d |%n",
                    b.getBookId(),
                    b.getTitle(),
                    b.getAuthor() != null ? b.getAuthor().getName() : "Unknown",
                    b.getIsbn(),
                    b.getAvailableCopies(),
                    b.getTotalCopies());
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.printf("  Total Available Titles: %d%n", availableBooks.size());
    }

    /**
     * Menu Option 6: Exit
     */
    private static void handleExit() {
        System.out.println("================================================================================");
        System.out.println("   Thank you for using the Library Management System. Have a productive day!   ");
        System.out.println("================================================================================");
    }
}
