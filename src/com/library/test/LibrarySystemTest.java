package com.library.test;

import com.library.exception.*;
import com.library.model.*;
import com.library.service.Library;

import java.time.LocalDate;
import java.util.List;

/**
 * Automated Test Suite executing and verifying all 17 required scenarios
 * for Specification 2: Library Management System.
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class LibrarySystemTest {

    private static int totalPassed = 0;
    private static int totalFailed = 0;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("            LIBRARY MANAGEMENT SYSTEM - AUTOMATED VERIFICATION SUITE            ");
        System.out.println("            Student: Cibi K. | Register Number: 717825F112                     ");
        System.out.println("================================================================================\n");

        Library library = new Library("Karpagam Central Library Test");

        // Test 1: Application starts and initializes cleanly
        testCase(1, "Application initializes and starts cleanly", () -> {
            return library != null && library.getAllBooks().isEmpty();
        });

        // Test 2: Add a valid book
        Author author1 = new Author("Robert C. Martin", "unclebob@cleancoder.com");
        Book book1 = new Book("BK-201", "Clean Architecture", author1, "978-0134494164", 2);
        testCase(2, "Add a valid book to library catalog", () -> {
            try {
                library.addBook(book1);
                Book retrieved = library.findBookById("BK-201");
                return retrieved != null && retrieved.getTotalCopies() == 2 && retrieved.getAvailableCopies() == 2;
            } catch (Exception e) {
                return false;
            }
        });

        // Test 3: Add a regular member
        Member regMember = new Member("MEM-101", "Cibi K.", "9876543210");
        testCase(3, "Add a regular member to library registry", () -> {
            try {
                library.addMember(regMember);
                Member retrieved = library.findMemberById("MEM-101");
                return retrieved != null && retrieved.getMaxBooksAllowed() == 3
                        && retrieved.getBorrowDurationDays() == 14
                        && retrieved.getDailyFineRate() == 5.0;
            } catch (Exception e) {
                return false;
            }
        });

        // Test 4: Add a premium member
        PremiumMember premMember = new PremiumMember("PREM-101", "Ananya Sharma", "9876543212", "Platinum");
        testCase(4, "Add a premium member and verify tier attributes", () -> {
            try {
                library.addMember(premMember);
                Member retrieved = library.findMemberById("PREM-101");
                return retrieved != null && retrieved instanceof PremiumMember
                        && retrieved.getMaxBooksAllowed() == 6
                        && retrieved.getBorrowDurationDays() == 30
                        && retrieved.getDailyFineRate() == 2.0;
            } catch (Exception e) {
                return false;
            }
        });

        // Test 5: Borrow a book successfully
        final BorrowTransaction[] txnHolder = new BorrowTransaction[1];
        LocalDate borrowDate = LocalDate.of(2026, 10, 1);
        testCase(5, "Borrow a book successfully and generate receipt", () -> {
            try {
                txnHolder[0] = library.borrowBook("MEM-101", "BK-201", borrowDate);
                return txnHolder[0] != null
                        && txnHolder[0].getBook().getBookId().equals("BK-201")
                        && txnHolder[0].getMember().getMemberId().equals("MEM-101")
                        && !txnHolder[0].isReturned();
            } catch (Exception e) {
                return false;
            }
        });

        // Test 6: Verify that stock decreases after borrowing
        testCase(6, "Verify stock decreases from 2 to 1 after borrowing", () -> {
            Book b = library.findBookById("BK-201");
            return b.getAvailableCopies() == 1 && b.getTotalCopies() == 2;
        });

        // Test 7: Attempt to borrow when stock is unavailable
        Book singleCopyBook = new Book("BK-202", "Refactoring", author1, "978-0201485677", 1);
        testCase(7, "Attempt to borrow when stock is unavailable (throws InsufficientStockException)", () -> {
            try {
                library.addBook(singleCopyBook);
                library.borrowBook("PREM-101", "BK-202", borrowDate); // now 0 copies left
                // Second borrow should fail
                library.borrowBook("MEM-101", "BK-202", borrowDate);
                return false; // Should not reach here
            } catch (InsufficientStockException e) {
                return true; // Expected
            } catch (Exception e) {
                return false;
            }
        });

        // Test 8: Attempt to borrow with an invalid member ID
        testCase(8, "Attempt to borrow with invalid member ID (throws MemberNotFoundException)", () -> {
            try {
                library.borrowBook("NON_EXISTENT_MEM", "BK-201", borrowDate);
                return false;
            } catch (MemberNotFoundException e) {
                return true; // Expected
            } catch (Exception e) {
                return false;
            }
        });

        // Test 9: Attempt to exceed a regular member's borrowing limit (limit = 3)
        testCase(9, "Attempt to exceed regular member's borrowing limit (3 books)", () -> {
            try {
                Book b3 = new Book("BK-203", "Java Concurrency in Practice", author1, "978-0321349606", 5);
                Book b4 = new Book("BK-204", "Head First Java", author1, "978-0596009205", 5);
                Book b5 = new Book("BK-205", "Core Java Volume I", author1, "978-0135166307", 5);
                library.addBook(b3);
                library.addBook(b4);
                library.addBook(b5);

                // MEM-101 currently has 1 book (BK-201). Borrow 2 more:
                library.borrowBook("MEM-101", "BK-203", borrowDate);
                library.borrowBook("MEM-101", "BK-204", borrowDate);
                // Total borrowed = 3. Now attempt 4th book:
                library.borrowBook("MEM-101", "BK-205", borrowDate);
                return false; // Should have thrown exception
            } catch (BorrowLimitExceededException e) {
                return true; // Expected
            } catch (Exception e) {
                return false;
            }
        });

        // Test 10: Demonstrate premium-member borrowing privileges (higher limit = 6, 30 days)
        testCase(10, "Demonstrate premium-member borrowing privileges (quota 6 & 30 days loan)", () -> {
            try {
                // PREM-101 has 1 book (BK-202). Can borrow multiple books past regular limit of 3
                library.borrowBook("PREM-101", "BK-203", borrowDate);
                library.borrowBook("PREM-101", "BK-204", borrowDate);
                BorrowTransaction t4 = library.borrowBook("PREM-101", "BK-205", borrowDate);
                // Check due date is 30 days after borrow date
                boolean correctDueDate = t4.getDueDate().equals(borrowDate.plusDays(30));
                return premMember.getCurrentlyBorrowedCount() == 4 && correctDueDate;
            } catch (Exception e) {
                return false;
            }
        });

        // Test 11: Return a borrowed book successfully
        final ReturnTransaction[] retHolder = new ReturnTransaction[1];
        LocalDate onTimeReturnDate = LocalDate.of(2026, 10, 10); // Borrowed on Oct 1, due Oct 15
        testCase(11, "Return a borrowed book on time successfully", () -> {
            try {
                retHolder[0] = library.returnBook(txnHolder[0].getTransactionId(), onTimeReturnDate);
                return retHolder[0] != null && retHolder[0].getDaysOverdue() == 0 && retHolder[0].getFineAmount() == 0.0;
            } catch (Exception e) {
                return false;
            }
        });

        // Test 12: Verify that stock increases after return
        testCase(12, "Verify that stock increases back to 2 after return", () -> {
            Book b = library.findBookById("BK-201");
            return b.getAvailableCopies() == 2;
        });

        // Test 13: Demonstrate an overdue return and fine calculation
        testCase(13, "Demonstrate overdue return and fine calculation (polymorphic rate)", () -> {
            try {
                // Borrow BK-201 on Oct 1, regular member due date is Oct 15 (14 days)
                BorrowTransaction overdueBorrow = library.borrowBook("MEM-101", "BK-201", LocalDate.of(2026, 10, 1));
                // Return on Oct 20 (5 days overdue)
                LocalDate lateReturnDate = LocalDate.of(2026, 10, 20);
                ReturnTransaction retTxn = library.returnBook(overdueBorrow.getTransactionId(), lateReturnDate);
                // Overdue days = 5, Daily rate for regular member = Rs. 5.00 -> Fine = Rs. 25.00
                return retTxn.getDaysOverdue() == 5 && Math.abs(retTxn.getFineAmount() - 25.00) < 0.001;
            } catch (Exception e) {
                return false;
            }
        });

        // Test 14: Attempt to return a book without a valid outstanding borrowing transaction
        testCase(14, "Attempt to return unborrowed/already returned transaction (throws InvalidTransactionException)", () -> {
            try {
                // Attempt to return already returned transaction
                library.returnBook(txnHolder[0].getTransactionId(), onTimeReturnDate);
                return false;
            } catch (InvalidTransactionException e) {
                return true; // Expected
            } catch (Exception e) {
                return false;
            }
        });

        // Test 15: Input validation / duplicate ID protection
        testCase(15, "Prevent duplicate book or member IDs without crashing", () -> {
            try {
                library.addBook(new Book("BK-201", "Duplicate Test", author1, "000-000", 1));
                return false;
            } catch (DuplicateEntityException e) {
                return true; // Expected
            } catch (Exception e) {
                return false;
            }
        });

        // Test 16: Display available books list
        testCase(16, "Retrieve and verify available books list", () -> {
            List<Book> available = library.getAvailableBooks();
            return !available.isEmpty() && available.stream().allMatch(Book::isAvailable);
        });

        // Test 17: Application clean shutdown and integrity
        testCase(17, "Verify state integrity across entities and clean termination", () -> {
            boolean booksConsistent = library.getAllBooks().stream()
                    .allMatch(b -> b.getAvailableCopies() <= b.getTotalCopies() && b.getAvailableCopies() >= 0);
            boolean membersConsistent = library.getAllMembers().stream()
                    .allMatch(m -> m.getCurrentlyBorrowedCount() >= 0 && m.getCurrentlyBorrowedCount() <= m.getMaxBooksAllowed());
            return booksConsistent && membersConsistent;
        });

        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("TOTAL TESTS RUN : 17 | PASSED: %d | FAILED: %d%n", totalPassed, totalFailed);
        System.out.println("================================================================================");
    }

    private static void testCase(int num, String description, TestCaseAction action) {
        try {
            boolean pass = action.run();
            if (pass) {
                totalPassed++;
                System.out.printf("Test %02d: [PASS] %s%n", num, description);
            } else {
                totalFailed++;
                System.out.printf("Test %02d: [FAIL] %s%n", num, description);
            }
        } catch (Exception e) {
            totalFailed++;
            System.out.printf("Test %02d: [FAIL] %s (Exception: %s)%n", num, description, e.getMessage());
        }
    }

    @FunctionalInterface
    interface TestCaseAction {
        boolean run() throws Exception;
    }
}
