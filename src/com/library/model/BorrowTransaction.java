package com.library.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a borrowing transaction in the Library Management System.
 * Enforces Business Rule 5: Borrowing transactions must link directly to books.
 * Demonstrates Aggregation/Association (holds direct references to Book and Member).
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class BorrowTransaction {
    private String transactionId;
    private Book book;          // Direct link to Book (Business Rule 5)
    private Member member;      // Direct link to Member
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean returned;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Default constructor.
     */
    public BorrowTransaction() {
    }

    /**
     * Parameterized constructor.
     *
     * @param transactionId Unique transaction ID
     * @param book          The book being borrowed (direct reference)
     * @param member        The member borrowing the book (direct reference)
     * @param borrowDate    Date of borrow
     */
    public BorrowTransaction(String transactionId, Book book, Member member, LocalDate borrowDate) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty.");
        }
        if (book == null) {
            throw new IllegalArgumentException("Book reference cannot be null.");
        }
        if (member == null) {
            throw new IllegalArgumentException("Member reference cannot be null.");
        }
        if (borrowDate == null) {
            throw new IllegalArgumentException("Borrow date cannot be null.");
        }

        this.transactionId = transactionId.trim();
        this.book = book;
        this.member = member;
        this.borrowDate = borrowDate;
        // Due date is determined polymorphically by the member's allowed duration
        this.dueDate = borrowDate.plusDays(member.getBorrowDurationDays());
        this.returned = false;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    /**
     * Generates a formal borrow receipt for the transaction.
     * Fulfills Expected Output: Show borrow receipt with updated stock.
     *
     * @return Formatted receipt string
     */
    public String generateReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("                 OFFICIAL BORROW RECEIPT                    \n");
        sb.append("============================================================\n");
        sb.append(String.format(" Transaction ID     : %s\n", transactionId));
        sb.append(String.format(" Borrow Date        : %s\n", borrowDate.format(DATE_FORMATTER)));
        sb.append(String.format(" Due Date           : %s (%d days allowed)\n",
                dueDate.format(DATE_FORMATTER), member.getBorrowDurationDays()));
        sb.append("------------------------------------------------------------\n");
        sb.append(" MEMBER DETAILS:\n");
        sb.append(String.format(" Member ID          : %s\n", member.getMemberId()));
        sb.append(String.format(" Member Name        : %s\n", member.getName()));
        sb.append(String.format(" Membership Tier    : %s\n", member.getMembershipType()));
        sb.append(String.format(" Active Borrows     : %d / %d books\n",
                member.getCurrentlyBorrowedCount(), member.getMaxBooksAllowed()));
        sb.append("------------------------------------------------------------\n");
        sb.append(" BOOK DETAILS:\n");
        sb.append(String.format(" Book ID            : %s\n", book.getBookId()));
        sb.append(String.format(" Title              : %s\n", book.getTitle()));
        sb.append(String.format(" Author             : %s\n", book.getAuthor().getName()));
        sb.append(String.format(" ISBN               : %s\n", book.getIsbn()));
        sb.append(String.format(" Updated Stock      : %d available (Total: %d)\n",
                book.getAvailableCopies(), book.getTotalCopies()));
        sb.append("============================================================\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowTransaction that = (BorrowTransaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return String.format("[%s] Book: %s (%s) | Member: %s (%s) | Due: %s | Status: %s",
                transactionId, book.getTitle(), book.getBookId(),
                member.getName(), member.getMemberId(),
                dueDate.format(DATE_FORMATTER), (returned ? "RETURNED" : "ACTIVE"));
    }
}
