package com.library.exception;

/**
 * Thrown when a member attempts to borrow a book with zero available stock.
 * Enforces Business Rule 1: Books can only be borrowed if available in stock.
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class InsufficientStockException extends LibraryException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
