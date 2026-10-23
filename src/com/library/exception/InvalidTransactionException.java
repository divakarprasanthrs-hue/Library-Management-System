package com.library.exception;

/**
 * Thrown when an invalid transaction state or operation is attempted (e.g., returning a book
 * that was never borrowed or already returned).
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class InvalidTransactionException extends LibraryException {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
