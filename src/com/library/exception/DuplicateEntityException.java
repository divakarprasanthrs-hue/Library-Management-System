package com.library.exception;

/**
 * Thrown when attempting to register a book or member with an ID that already exists.
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class DuplicateEntityException extends LibraryException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}
