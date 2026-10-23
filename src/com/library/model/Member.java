package com.library.model;

import java.util.Objects;

/**
 * Base Member class representing a regular library member.
 * Demonstrates:
 * - Encapsulation (private fields and data integrity)
 * - Base class for Inheritance (extended by PremiumMember)
 * - Dynamic polymorphism through overridable borrowing privilege methods
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class Member {
    // Encapsulated fields
    private String memberId;
    private String name;
    private String phone;
    private int currentlyBorrowedCount;

    // Constants for Regular Membership policy
    public static final int REGULAR_MAX_BOOKS = 3;
    public static final int REGULAR_BORROW_DAYS = 14;
    public static final double REGULAR_DAILY_FINE = 5.00; // Rs. 5 per day

    /**
     * Default constructor.
     */
    public Member() {
        this.currentlyBorrowedCount = 0;
    }

    /**
     * Parameterized constructor.
     *
     * @param memberId Unique member identifier
     * @param name     Full name of the member
     * @param phone    Contact phone number
     */
    public Member(String memberId, String name, String phone) {
        setMemberId(memberId);
        setName(name);
        setPhone(phone);
        this.currentlyBorrowedCount = 0;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID cannot be null or empty.");
        }
        this.memberId = memberId.trim().toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = (phone != null) ? phone.trim() : "N/A";
    }

    public int getCurrentlyBorrowedCount() {
        return currentlyBorrowedCount;
    }

    public void setCurrentlyBorrowedCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Borrowed count cannot be negative.");
        }
        this.currentlyBorrowedCount = count;
    }

    /**
     * Increments the count of currently borrowed books.
     */
    public void incrementBorrowedCount() {
        this.currentlyBorrowedCount++;
    }

    /**
     * Decrements the count of currently borrowed books.
     */
    public void decrementBorrowedCount() {
        if (this.currentlyBorrowedCount > 0) {
            this.currentlyBorrowedCount--;
        }
    }

    // ==========================================================
    // POLYMORPHIC PRIVILEGE METHODS (Overridden in PremiumMember)
    // ==========================================================

    /**
     * Maximum number of books allowed to be borrowed concurrently.
     *
     * @return 3 for regular members
     */
    public int getMaxBooksAllowed() {
        return REGULAR_MAX_BOOKS;
    }

    /**
     * Number of allowed loan days for borrowed books.
     *
     * @return 14 days for regular members
     */
    public int getBorrowDurationDays() {
        return REGULAR_BORROW_DAYS;
    }

    /**
     * Fine rate per day overdue in Rupees.
     *
     * @return 5.00 for regular members
     */
    public double getDailyFineRate() {
        return REGULAR_DAILY_FINE;
    }

    /**
     * Returns the description of the membership tier.
     *
     * @return "Regular Member"
     */
    public String getMembershipType() {
        return "Regular Member";
    }

    /**
     * Checks if the member has reached their borrowing limit.
     *
     * @return true if currently borrowed books < max allowed
     */
    public boolean canBorrow() {
        return currentlyBorrowedCount < getMaxBooksAllowed();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(memberId, member.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | Contact: %s | Borrowed: %d/%d books",
                memberId, name, getMembershipType(), phone, currentlyBorrowedCount, getMaxBooksAllowed());
    }
}
