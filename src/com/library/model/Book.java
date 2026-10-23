package com.library.model;

import java.util.Objects;

/**
 * Represents a Book in the Library Management System.
 * Demonstrates:
 * - Encapsulation (private fields, controlled mutators with business rule validation)
 * - Aggregation (Book HAS-A Author)
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class Book {
    private String bookId;
    private String title;
    private Author author;
    private String isbn;
    private int totalCopies;
    private int availableCopies;

    /**
     * Default constructor.
     */
    public Book() {
    }

    /**
     * Parameterized constructor.
     *
     * @param bookId      Unique identifier for the book
     * @param title       Title of the book
     * @param author      Author of the book (Aggregation)
     * @param isbn        ISBN code of the book
     * @param totalCopies Total inventory copies in the library
     */
    public Book(String bookId, String title, Author author, String isbn, int totalCopies) {
        setBookId(bookId);
        setTitle(title);
        setAuthor(author);
        setIsbn(isbn);
        if (totalCopies <= 0) {
            throw new IllegalArgumentException("Total copies must be greater than zero.");
        }
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be null or empty.");
        }
        this.bookId = bookId.trim().toUpperCase();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        this.title = title.trim();
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null.");
        }
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = (isbn != null) ? isbn.trim() : "N/A";
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative.");
        }
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        if (availableCopies < 0 || availableCopies > totalCopies) {
            throw new IllegalArgumentException("Available copies must be between 0 and total copies (" + totalCopies + ").");
        }
        this.availableCopies = availableCopies;
    }

    /**
     * Checks if at least one copy of this book is available for borrowing.
     *
     * @return true if availableCopies > 0, false otherwise
     */
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    /**
     * Decreases the available copies by 1 when borrowed.
     * Enforces Business Rule 1 & 2: Books can only be borrowed if available, stock reduces on borrow.
     *
     * @return true if borrow operation succeeded
     */
    public boolean borrowCopy() {
        if (availableCopies > 0) {
            availableCopies--;
            return true;
        }
        return false;
    }

    /**
     * Increases the available copies by 1 when returned.
     * Enforces Business Rule 2: Stock updates when returned.
     *
     * @return true if return operation succeeded
     */
    public boolean returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(bookId, book.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }

    @Override
    public String toString() {
        return String.format("[%s] \"%s\" by %s | ISBN: %s | Available: %d/%d",
                bookId, title, (author != null ? author.getName() : "Unknown"), isbn, availableCopies, totalCopies);
    }
}
