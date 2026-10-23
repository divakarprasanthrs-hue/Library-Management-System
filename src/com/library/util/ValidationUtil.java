package com.library.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Utility class providing robust console input validation and formatting helpers.
 * Ensures the console application never crashes unexpectedly on malformed user input.
 *
 * Course: 23CSR306 - Java Programming
 * Student: Cibi K. (Reg. No: 717825F112)
 */
public final class ValidationUtil {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private ValidationUtil() {
        // Prevent instantiation
    }

    /**
     * Reads a non-empty string from the scanner.
     *
     * @param scanner Scanner instance
     * @param prompt  User prompt message
     * @return Validated non-empty trimmed string
     */
    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line != null && !line.trim().isEmpty()) {
                return line.trim();
            }
            System.out.println("  [!] Error: Input cannot be blank. Please try again.");
        }
    }

    /**
     * Reads an integer within a specified range [min, max].
     *
     * @param scanner Scanner instance
     * @param prompt  User prompt message
     * @param min     Minimum allowed value
     * @param max     Maximum allowed value
     * @return Validated integer
     */
    public static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("  [!] Error: Value must be between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Error: Please enter a valid whole number.");
            }
        }
    }

    /**
     * Reads a strictly positive integer (> 0).
     *
     * @param scanner Scanner instance
     * @param prompt  User prompt message
     * @return Validated positive integer
     */
    public static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
                System.out.println("  [!] Error: Number must be greater than zero.");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Error: Please enter a valid numeric value.");
            }
        }
    }

    /**
     * Reads a date in dd-MM-yyyy format, or defaults to today if user presses Enter.
     *
     * @param scanner Scanner instance
     * @param prompt  User prompt message
     * @return Parsed LocalDate
     */
    public static LocalDate readDateWithDefaultToday(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Error: Invalid date format. Use dd-MM-yyyy (e.g., 25-10-2026), or press Enter for today.");
            }
        }
    }
}
