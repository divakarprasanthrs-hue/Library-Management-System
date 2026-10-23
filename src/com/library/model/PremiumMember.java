package com.library.model;

/**
 * Represents a Premium Member in the Library Management System.
 * Demonstrates:
 * - Inheritance (extends Member)
 * - Polymorphism (overrides getMaxBooksAllowed, getBorrowDurationDays, getDailyFineRate)
 * - Enforces Business Rule 3: Premium members may borrow more books and for longer duration.
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class PremiumMember extends Member {
    private String membershipTier; // e.g. Gold, Platinum, VIP

    // Constants for Premium Membership policy
    public static final int PREMIUM_MAX_BOOKS = 6;         // Double borrowing capacity
    public static final int PREMIUM_BORROW_DAYS = 30;      // Extended loan duration
    public static final double PREMIUM_DAILY_FINE = 2.00;  // Discounted fine rate (Rs. 2/day)

    /**
     * Default constructor.
     */
    public PremiumMember() {
        super();
        this.membershipTier = "Gold";
    }

    /**
     * Parameterized constructor.
     *
     * @param memberId       Unique member identifier
     * @param name           Full name of the member
     * @param phone          Contact phone number
     * @param membershipTier Tier designation (e.g. "Gold", "Platinum", "VIP")
     */
    public PremiumMember(String memberId, String name, String phone, String membershipTier) {
        super(memberId, name, phone);
        setMembershipTier(membershipTier);
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        if (membershipTier == null || membershipTier.trim().isEmpty()) {
            this.membershipTier = "Gold";
        } else {
            this.membershipTier = membershipTier.trim();
        }
    }

    // ==========================================================
    // POLYMORPHIC OVERRIDES (Business Rule 3: Premium Privileges)
    // ==========================================================

    /**
     * Overridden borrowing limit: Premium members can borrow up to 6 books.
     */
    @Override
    public int getMaxBooksAllowed() {
        return PREMIUM_MAX_BOOKS;
    }

    /**
     * Overridden loan duration: Premium members receive 30 days loan period.
     */
    @Override
    public int getBorrowDurationDays() {
        return PREMIUM_BORROW_DAYS;
    }

    /**
     * Overridden daily fine rate: Discounted rate of Rs. 2.00 per overdue day.
     */
    @Override
    public double getDailyFineRate() {
        return PREMIUM_DAILY_FINE;
    }

    /**
     * Overridden membership description.
     */
    @Override
    public String getMembershipType() {
        return "Premium Member [" + membershipTier + "]";
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | Contact: %s | Borrowed: %d/%d books (Loan: %d days, Fine: Rs.%.2f/day)",
                getMemberId(), getName(), getMembershipType(), getPhone(),
                getCurrentlyBorrowedCount(), getMaxBooksAllowed(),
                getBorrowDurationDays(), getDailyFineRate());
    }
}
