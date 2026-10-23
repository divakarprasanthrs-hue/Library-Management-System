package com.library.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a return transaction in the Library Management System.
 * Enforces:
 * - Business Rule 4: Fines are applied for late returns.
 * - Business Rule 5: Returning transactions must link directly to books.
 * Demonstrates Aggregation and Polymorphic Fine Calculation.
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class ReturnTransaction {
    private String returnTransactionId;
    private BorrowTransaction borrowTransaction; // Direct link to borrow record & book
    private LocalDate returnDate;
    private long daysOverdue;
    private double fineAmount;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Default constructor.
     */
    public ReturnTransaction() {
    }

    /**
     * Parameterized constructor.
     * Automatically computes overdue days and applies member-specific fine rate.
     *
     * @param returnTransactionId Unique return transaction identifier
     * @param borrowTransaction   The linked active borrow transaction
     * @param returnDate          Actual date the book is returned
     */
    public ReturnTransaction(String returnTransactionId, BorrowTransaction borrowTransaction, LocalDate returnDate) {
        if (returnTransactionId == null || returnTransactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Return Transaction ID cannot be null or empty.");
        }
        if (borrowTransaction == null) {
            throw new IllegalArgumentException("Borrow transaction reference cannot be null.");
        }
        if (returnDate == null) {
            throw new IllegalArgumentException("Return date cannot be null.");
        }

        this.returnTransactionId = returnTransactionId.trim();
        this.borrowTransaction = borrowTransaction;
        this.returnDate = returnDate;

        // Calculate overdue days
        LocalDate dueDate = borrowTransaction.getDueDate();
        if (returnDate.isAfter(dueDate)) {
            this.daysOverdue = ChronoUnit.DAYS.between(dueDate, returnDate);
            // Polymorphic fine calculation based on member's rate
            this.fineAmount = this.daysOverdue * borrowTransaction.getMember().getDailyFineRate();
        } else {
            this.daysOverdue = 0;
            this.fineAmount = 0.00;
        }
    }

    public String getReturnTransactionId() {
        return returnTransactionId;
    }

    public BorrowTransaction getBorrowTransaction() {
        return borrowTransaction;
    }

    public Book getBook() {
        return borrowTransaction.getBook();
    }

    public Member getMember() {
        return borrowTransaction.getMember();
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public long getDaysOverdue() {
        return daysOverdue;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public boolean hasFine() {
        return fineAmount > 0;
    }

    /**
     * Generates a formal return receipt.
     * Fulfills Expected Outputs: Show return receipt, updated stock, and fines (if applicable).
     *
     * @return Formatted receipt string
     */
    public String generateReceipt() {
        Book book = getBook();
        Member member = getMember();

        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("                 OFFICIAL RETURN RECEIPT                    \n");
        sb.append("============================================================\n");
        sb.append(String.format(" Return Txn ID      : %s\n", returnTransactionId));
        sb.append(String.format(" Original Borrow Txn: %s\n", borrowTransaction.getTransactionId()));
        sb.append(String.format(" Return Date        : %s\n", returnDate.format(DATE_FORMATTER)));
        sb.append(String.format(" Scheduled Due Date : %s\n", borrowTransaction.getDueDate().format(DATE_FORMATTER)));
        sb.append("------------------------------------------------------------\n");
        sb.append(" MEMBER DETAILS:\n");
        sb.append(String.format(" Member ID          : %s\n", member.getMemberId()));
        sb.append(String.format(" Member Name        : %s\n", member.getName()));
        sb.append(String.format(" Membership Tier    : %s\n", member.getMembershipType()));
        sb.append(String.format(" Remaining Borrows  : %d / %d books\n",
                member.getCurrentlyBorrowedCount(), member.getMaxBooksAllowed()));
        sb.append("------------------------------------------------------------\n");
        sb.append(" BOOK DETAILS:\n");
        sb.append(String.format(" Book ID            : %s\n", book.getBookId()));
        sb.append(String.format(" Title              : %s\n", book.getTitle()));
        sb.append(String.format(" Author             : %s\n", book.getAuthor().getName()));
        sb.append(String.format(" Updated Stock      : %d available (Total: %d)\n",
                book.getAvailableCopies(), book.getTotalCopies()));
        sb.append("------------------------------------------------------------\n");
        sb.append(" FINE ASSESSMENT:\n");
        if (daysOverdue > 0) {
            sb.append(String.format(" Overdue Duration   : %d day(s) late\n", daysOverdue));
            sb.append(String.format(" Daily Fine Rate    : Rs. %.2f/day\n", member.getDailyFineRate()));
            sb.append(String.format(" Total Fine Due     : Rs. %.2f [APPLIED]\n", fineAmount));
        } else {
            sb.append(" Overdue Duration   : 0 days (Returned on time)\n");
            sb.append(" Total Fine Due     : Rs. 0.00 (No fine applicable)\n");
        }
        sb.append("============================================================\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnTransaction that = (ReturnTransaction) o;
        return Objects.equals(returnTransactionId, that.returnTransactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnTransactionId);
    }

    @Override
    public String toString() {
        return String.format("[%s] Book: %s | Member: %s | Fine: Rs.%.2f (%d days overdue)",
                returnTransactionId, getBook().getTitle(), getMember().getName(), fineAmount, daysOverdue);
    }
}
