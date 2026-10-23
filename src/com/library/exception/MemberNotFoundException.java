package com.library.exception;

/**
 * Thrown when a requested member identifier cannot be found in the library registry.
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class MemberNotFoundException extends LibraryException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
