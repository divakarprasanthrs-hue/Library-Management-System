package com.library.model;

import java.util.Objects;

/**
 * Represents an author of a book in the Library Management System.
 * Demonstrates Encapsulation with private attributes and accessor methods.
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class Author {
    private String name;
    private String email;

    /**
     * Default constructor.
     */
    public Author() {
    }

    /**
     * Parameterized constructor to initialize author details.
     *
     * @param name  Author's full name
     * @param email Author's contact email
     */
    public Author(String name, String email) {
        setName(name);
        setEmail(email);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = (email != null) ? email.trim() : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(name, author.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        if (email != null && !email.isEmpty()) {
            return name + " (" + email + ")";
        }
        return name;
    }
}
