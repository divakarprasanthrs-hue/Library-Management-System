package com.library.exception;

/**
 * Thrown when a member attempts to borrow more books than permitted by their membership tier.
 * Enforces Business Rule 3: Tier borrowing quotas.
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class BorrowLimitExceededException extends LibraryException {
    public BorrowLimitExceededException(String message) {
        super(message);
    }
}
