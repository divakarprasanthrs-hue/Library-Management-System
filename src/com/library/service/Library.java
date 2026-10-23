package com.library.service;

import com.library.exception.*;
import com.library.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Service orchestrator and central repository for the Library Management System.
 * Enforces all core business rules:
 * 1. Books can only be borrowed if they are available in stock.
 * 2. When borrowed, stock reduces; when returned, stock updates.
 * 3. Premium members may borrow more books and for longer duration.
 * 4. Fines are applied for late returns.
 * 5. Borrowing and returning transactions must link directly to books.
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public class Library {
    private String libraryName;
    private final Map<String, Book> books;
    private final Map<String, Member> members;
    private final Map<String, BorrowTransaction> borrowTransactions;
    private final List<ReturnTransaction> returnTransactions;

    private final AtomicInteger borrowSeq = new AtomicInteger(1000);
    private final AtomicInteger returnSeq = new AtomicInteger(5000);

    /**
     * Default constructor initializing empty collections.
     */
    public Library() {
        this("Central University Library");
    }

    /**
     * Parameterized constructor.
     *
     * @param libraryName Name of the library institution
     */
    public Library(String libraryName) {
        this.libraryName = libraryName;
        this.books = new LinkedHashMap<>();
        this.members = new LinkedHashMap<>();
        this.borrowTransactions = new LinkedHashMap<>();
        this.returnTransactions = new ArrayList<>();
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    // ==========================================================
    // 1. BOOK MANAGEMENT
    // ==========================================================

    /**
     * Adds a new book to the library catalog.
     * Validates uniqueness of Book ID.
     *
     * @param book The book entity to register
     * @throws DuplicateEntityException If book ID already exists
     */
    public synchronized void addBook(Book book) throws DuplicateEntityException {
        if (book == null) {
            throw new IllegalArgumentException("Book object cannot be null.");
        }
        String id = book.getBookId().toUpperCase();
        if (books.containsKey(id)) {
            throw new DuplicateEntityException("A book with ID '" + id + "' already exists in the catalog.");
        }
        books.put(id, book);
    }

    /**
     * Finds a book by its unique ID.
     *
     * @param bookId Unique book ID
     * @return The Book instance, or null if not found
     */
    public Book findBookById(String bookId) {
        if (bookId == null) return null;
        return books.get(bookId.trim().toUpperCase());
    }

    /**
     * Retrieves all books currently registered in the catalog.
     *
     * @return List of all books
     */
    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    /**
     * Retrieves all books with at least 1 copy available in stock.
     * Fulfills Menu Option: Display Available Books.
     *
     * @return List of currently available books
     */
    public List<Book> getAvailableBooks() {
        return books.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }

    // ==========================================================
    // 2. MEMBER MANAGEMENT
    // ==========================================================

    /**
     * Registers a new member (Regular or Premium) into the library registry.
     * Validates uniqueness of Member ID.
     *
     * @param member The member entity to register
     * @throws DuplicateEntityException If member ID already exists
     */
    public synchronized void addMember(Member member) throws DuplicateEntityException {
        if (member == null) {
            throw new IllegalArgumentException("Member object cannot be null.");
        }
        String id = member.getMemberId().toUpperCase();
        if (members.containsKey(id)) {
            throw new DuplicateEntityException("A member with ID '" + id + "' is already registered.");
        }
        members.put(id, member);
    }

    /**
     * Finds a member by unique Member ID.
     *
     * @param memberId Unique member identifier
     * @return The Member instance, or null if not found
     */
    public Member findMemberById(String memberId) {
        if (memberId == null) return null;
        return members.get(memberId.trim().toUpperCase());
    }

    /**
     * Retrieves all members registered in the library.
     *
     * @return List of all members
     */
    public List<Member> getAllMembers() {
        return new ArrayList<>(members.values());
    }

    // ==========================================================
    // 3. BORROWING PROCESS (Business Rules 1, 2, 3, 5)
    // ==========================================================

    /**
     * Processes borrowing of a book by a member.
     * Enforces:
     * - Member existence
     * - Member borrowing limit (polymorphic quota check)
     * - Book existence
     * - Book stock availability (stock > 0)
     * - Stock reduction
     * - Transaction generation directly linked to Book and Member
     *
     * @param memberId   Borrowing member's identifier
     * @param bookId     Identifier of the requested book
     * @param borrowDate Date of borrow transaction
     * @return The completed BorrowTransaction
     * @throws MemberNotFoundException      if member does not exist
     * @throws BookNotFoundException        if book does not exist
     * @throws BorrowLimitExceededException if member reached max book quota
     * @throws InsufficientStockException   if book has 0 available copies
     */
    public synchronized BorrowTransaction borrowBook(String memberId, String bookId, LocalDate borrowDate)
            throws MemberNotFoundException, BookNotFoundException,
                   BorrowLimitExceededException, InsufficientStockException {

        // Validate member
        Member member = findMemberById(memberId);
        if (member == null) {
            throw new MemberNotFoundException("Member with ID '" + memberId + "' does not exist.");
        }

        // Validate borrowing quota (Polymorphic: 3 for Regular, 6 for Premium)
        if (!member.canBorrow()) {
            throw new BorrowLimitExceededException(String.format(
                    "Borrowing limit reached: %s '%s' has already borrowed %d/%d books.",
                    member.getMembershipType(), member.getName(),
                    member.getCurrentlyBorrowedCount(), member.getMaxBooksAllowed()));
        }

        // Validate book
        Book book = findBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID '" + bookId + "' does not exist in catalog.");
        }

        // Validate stock availability (Business Rule 1)
        if (!book.isAvailable()) {
            throw new InsufficientStockException(String.format(
                    "Book \"%s\" (ID: %s) is currently OUT OF STOCK (Available: 0/%d).",
                    book.getTitle(), book.getBookId(), book.getTotalCopies()));
        }

        // Reduce stock (Business Rule 2)
        book.borrowCopy();

        // Increment member's borrow count
        member.incrementBorrowedCount();

        // Generate linked transaction (Business Rule 5)
        String txnId = "TXN-" + borrowSeq.incrementAndGet();
        BorrowTransaction transaction = new BorrowTransaction(txnId, book, member, borrowDate);

        borrowTransactions.put(txnId, transaction);
        return transaction;
    }

    // ==========================================================
    // 4. RETURNING PROCESS (Business Rules 2, 4, 5)
    // ==========================================================

    /**
     * Processes return of a borrowed book using its transaction ID.
     * Enforces:
     * - Identification of actual outstanding borrow record
     * - Rejection of unborrowed or already returned transactions
     * - Overdue fine calculation based on member policy
     * - Stock restoration
     * - Member quota restoration
     *
     * @param transactionId Unique borrow transaction identifier
     * @param returnDate    Date when the book is returned
     * @return The completed ReturnTransaction
     * @throws InvalidTransactionException if transaction not found or already returned
     */
    public synchronized ReturnTransaction returnBook(String transactionId, LocalDate returnDate)
            throws InvalidTransactionException {

        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty.");
        }

        BorrowTransaction borrowTxn = borrowTransactions.get(transactionId.trim());
        if (borrowTxn == null) {
            throw new InvalidTransactionException("Borrow transaction '" + transactionId + "' was not found.");
        }

        if (borrowTxn.isReturned()) {
            throw new InvalidTransactionException("Book for transaction '" + transactionId + "' has already been returned.");
        }

        // Mark borrow transaction as returned
        borrowTxn.setReturned(true);

        // Update book stock (Business Rule 2)
        Book book = borrowTxn.getBook();
        book.returnCopy();

        // Decrement member's active borrow count
        Member member = borrowTxn.getMember();
        member.decrementBorrowedCount();

        // Generate return record and compute fine (Business Rule 4 & 5)
        String retTxnId = "RET-" + returnSeq.incrementAndGet();
        ReturnTransaction returnTxn = new ReturnTransaction(retTxnId, borrowTxn, returnDate);

        returnTransactions.add(returnTxn);
        return returnTxn;
    }

    /**
     * Convenient helper to return a book by Member ID and Book ID.
     * Finds the active borrow transaction for this member and book combination.
     *
     * @param memberId   ID of the member returning
     * @param bookId     ID of the book being returned
     * @param returnDate Actual date of return
     * @return The completed ReturnTransaction
     * @throws InvalidTransactionException if no active borrow record exists
     */
    public synchronized ReturnTransaction returnBookByMemberAndBook(String memberId, String bookId, LocalDate returnDate)
            throws InvalidTransactionException {

        BorrowTransaction activeTxn = borrowTransactions.values().stream()
                .filter(t -> !t.isReturned()
                        && t.getMember().getMemberId().equalsIgnoreCase(memberId.trim())
                        && t.getBook().getBookId().equalsIgnoreCase(bookId.trim()))
                .findFirst()
                .orElseThrow(() -> new InvalidTransactionException(String.format(
                        "No active borrowing record found for Member ID '%s' and Book ID '%s'.",
                        memberId, bookId)));

        return returnBook(activeTxn.getTransactionId(), returnDate);
    }

    /**
     * Retrieves all active (unreturned) borrow transactions.
     *
     * @return List of active borrow transactions
     */
    public List<BorrowTransaction> getActiveBorrowTransactions() {
        return borrowTransactions.values().stream()
                .filter(t -> !t.isReturned())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all borrow transactions history.
     *
     * @return List of all borrow transactions
     */
    public List<BorrowTransaction> getAllBorrowTransactions() {
        return new ArrayList<>(borrowTransactions.values());
    }

    /**
     * Retrieves all return transactions history.
     *
     * @return List of return transactions
     */
    public List<ReturnTransaction> getAllReturnTransactions() {
        return new ArrayList<>(returnTransactions);
    }

    // ==========================================================
    // 5. SAMPLE DATA PRE-POPULATION
    // ==========================================================

    /**
     * Pre-populates the library with realistic books and members
     * for immediate demonstration and testing.
     */
    public void seedInitialData() {
        try {
            // Authors
            Author author1 = new Author("James Gosling", "james@java.org");
            Author author2 = new Author("Robert C. Martin", "unclebob@cleancoder.com");
            Author author3 = new Author("Joshua Bloch", "bloch@effectivejava.com");
            Author author4 = new Author("Erich Gamma", "gamma@designpatterns.org");

            // Books (Variety of stock counts including 1 copy to easily demo out-of-stock)
            addBook(new Book("BK-101", "Effective Java", author3, "978-0134685991", 2));
            addBook(new Book("BK-102", "Clean Code: A Handbook of Agile Craftsmanship", author2, "978-0132350884", 3));
            addBook(new Book("BK-103", "Design Patterns: Elements of Reusable Object-Oriented Software", author4, "978-0201633610", 1));
            addBook(new Book("BK-104", "The Java Programming Language", author1, "978-0321349804", 4));

            // Members (Regular and Premium)
            addMember(new Member("MEM-001", "Cibi K.", "9876543210"));
            addMember(new Member("MEM-002", "Dinesh Kumar", "9876543211"));
            addMember(new PremiumMember("PREM-001", "Ananya Sharma", "9876543212", "Platinum"));
            addMember(new PremiumMember("PREM-002", "Rahul Menon", "9876543213", "Gold"));
        } catch (DuplicateEntityException e) {
            // Should not happen with hardcoded seed data
            System.err.println("Seed data error: " + e.getMessage());
        }
    }
}
