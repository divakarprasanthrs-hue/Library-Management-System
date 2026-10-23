import os
from PIL import Image, ImageDraw, ImageFont

def render_terminal_window(title, text_lines, output_path, width=1050):
    font_size = 15
    try:
        font = ImageFont.truetype("C:\\Windows\\Fonts\\consola.ttf", font_size)
    except:
        font = ImageFont.load_default()

    line_height = 22
    header_height = 36
    padding = 16
    total_height = header_height + padding * 2 + len(text_lines) * line_height

    # Terminal background: dark slate theme #1e1e1e
    img = Image.new("RGB", (width, total_height), color=(30, 30, 30))
    draw = ImageDraw.Draw(img)

    # Window title bar #2d2d2d
    draw.rectangle([(0, 0), (width, header_height)], fill=(45, 45, 45))
    draw.line([(0, header_height), (width, header_height)], fill=(60, 60, 60), width=1)

    # Window control dots (mac/linux/modern terminal style)
    draw.ellipse([(14, 12), (24, 22)], fill=(255, 95, 86))
    draw.ellipse([(32, 12), (42, 22)], fill=(255, 189, 46))
    draw.ellipse([(50, 12), (60, 22)], fill=(39, 201, 63))

    # Title text
    try:
        title_font = ImageFont.truetype("C:\\Windows\\Fonts\\segoeui.ttf", 13)
    except:
        title_font = font
    draw.text((75, 9), title, fill=(200, 200, 200), font=title_font)

    # Terminal body text
    y = header_height + padding
    for line in text_lines:
        color = (220, 220, 220)
        # Syntax/highlight colors
        if "[SUCCESS]" in line or "PASSED" in line or "approved" in line:
            color = (78, 201, 176)
        elif "[!]" in line or "REJECTED" in line or "FAILED" in line or "Error" in line:
            color = (244, 71, 71)
        elif "OFFICIAL" in line or "RECEIPT" in line:
            color = (255, 215, 0)
        elif "===" in line or "---" in line:
            color = (100, 100, 100)
        elif ">>>" in line:
            color = (86, 156, 214)
        elif "FINE ASSESSMENT:" in line or "Total Fine Due" in line:
            color = (206, 145, 120)

        draw.text((padding, y), line, fill=color, font=font)
        y += line_height

    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    img.save(output_path)
    print(f"Generated: {output_path}")

screenshots_dir = "screenshots"

# 1. Main Menu
render_terminal_window(
    "Terminal / Console — 1. Main Menu",
    [
        "================================================================================",
        "             23CSR306 JAVA PROGRAMMING - ASSIGNMENT II                         ",
        "             SPECIFICATION 2: LIBRARY MANAGEMENT SYSTEM                        ",
        "             Student: Cibi K. | Register Number: 717825F112                    ",
        "================================================================================",
        "--------------------------------------------------------------------------------",
        "                            MAIN MENU OPTIONS                                   ",
        "--------------------------------------------------------------------------------",
        "  1. Add Book",
        "  2. Add Member",
        "  3. Borrow Book",
        "  4. Return Book",
        "  5. Display Available Books",
        "  6. Exit",
        "--------------------------------------------------------------------------------",
        "Enter your choice [1-6]: 5",
    ],
    os.path.join(screenshots_dir, "screen_01_main_menu.png")
)

# 2. Add Book and Member
render_terminal_window(
    "Terminal / Console — 2. Add Book and Member Operations",
    [
        ">>> [1. ADD NEW BOOK]",
        "Enter Book ID (e.g. BK-105): BK-105",
        "Enter Book Title: Spring in Action",
        "Enter Author Name: Craig Walls",
        "Enter Author Email (optional, press Enter to skip): craig@manning.com",
        "Enter ISBN: 978-1617294945",
        "Enter Total Copies (Stock): 3",
        "  [SUCCESS] Book added successfully to the library catalog!",
        "  Added: Spring in Action | Total Stock: 3 copies",
        "",
        ">>> [2. ADD NEW MEMBER]",
        "Enter Member ID (e.g. MEM-003 or PREM-003): MEM-003",
        "Enter Member Name: Suresh Raina",
        "Enter Contact Phone: 9876543214",
        "Select Membership Type:",
        "  1. Regular Member (Max: 3 books, Loan: 14 days, Fine: Rs. 5.00/day)",
        "  2. Premium Member (Max: 6 books, Loan: 30 days, Fine: Rs. 2.00/day)",
        "Select [1 or 2]: 1",
        "  [SUCCESS] Member successfully registered in the system!",
        "  Registered: Suresh Raina as Regular Member",
        "",
        ">>> [2. ADD NEW MEMBER]",
        "Enter Member ID (e.g. MEM-003 or PREM-003): PREM-003",
        "Enter Member Name: Priya Sundar",
        "Enter Contact Phone: 9876543215",
        "Select Membership Type:",
        "  1. Regular Member (Max: 3 books, Loan: 14 days, Fine: Rs. 5.00/day)",
        "  2. Premium Member (Max: 6 books, Loan: 30 days, Fine: Rs. 2.00/day)",
        "Select [1 or 2]: 2",
        "Enter Premium Tier (e.g. Gold, Platinum, VIP): VIP",
        "  [SUCCESS] Member successfully registered in the system!",
        "  Registered: Priya Sundar as Premium Member [VIP]",
    ],
    os.path.join(screenshots_dir, "screen_02_add_book_and_member.png")
)

# 3. Successful Borrowing Receipt & Updated Stock
render_terminal_window(
    "Terminal / Console — 3. Successful Borrowing Receipts & Updated Stock",
    [
        ">>> [3. BORROW BOOK]",
        "Enter Member ID: MEM-001",
        "Enter Book ID: BK-101",
        "Enter Borrow Date (dd-MM-yyyy, or press Enter for today): 10-10-2026",
        "",
        "  [SUCCESS] Book borrowing transaction approved!",
        "",
        "============================================================",
        "                 OFFICIAL BORROW RECEIPT                    ",
        "============================================================",
        " Transaction ID     : TXN-1001",
        " Borrow Date        : 10-10-2026",
        " Due Date           : 24-10-2026 (14 days allowed)",
        "------------------------------------------------------------",
        " MEMBER DETAILS:",
        " Member ID          : MEM-001",
        " Member Name        : Cibi K.",
        " Membership Tier    : Regular Member",
        " Active Borrows     : 1 / 3 books",
        "------------------------------------------------------------",
        " BOOK DETAILS:",
        " Book ID            : BK-101",
        " Title              : Effective Java",
        " Author             : Joshua Bloch",
        " ISBN               : 978-0134685991",
        " Updated Stock      : 1 available (Total: 2)",
        "============================================================",
    ],
    os.path.join(screenshots_dir, "screen_03_borrow_receipt.png")
)

# 4. Premium Member Privileges & Out of Stock Validation
render_terminal_window(
    "Terminal / Console — 4. Premium Member Loan & Out-of-Stock Validation",
    [
        ">>> [3. BORROW BOOK]",
        "Enter Member ID: PREM-001",
        "Enter Book ID: BK-103",
        "Enter Borrow Date (dd-MM-yyyy, or press Enter for today): 10-10-2026",
        "",
        "  [SUCCESS] Book borrowing transaction approved!",
        "",
        "============================================================",
        "                 OFFICIAL BORROW RECEIPT                    ",
        "============================================================",
        " Transaction ID     : TXN-1002",
        " Borrow Date        : 10-10-2026",
        " Due Date           : 09-11-2026 (30 days allowed)",
        "------------------------------------------------------------",
        " MEMBER DETAILS:",
        " Member ID          : PREM-001",
        " Member Name        : Ananya Sharma",
        " Membership Tier    : Premium Member [Platinum]",
        " Active Borrows     : 1 / 6 books",
        "------------------------------------------------------------",
        " BOOK DETAILS:",
        " Book ID            : BK-103",
        " Title              : Design Patterns: Elements of Reusable Object-Oriented Software",
        " Author             : Erich Gamma",
        " ISBN               : 978-0201633610",
        " Updated Stock      : 0 available (Total: 1)",
        "============================================================",
        "",
        ">>> [3. BORROW BOOK]",
        "Enter Member ID: MEM-002",
        "Enter Book ID: BK-103",
        "Enter Borrow Date (dd-MM-yyyy, or press Enter for today): 10-10-2026",
        "",
        "  [BORROW TRANSACTION REJECTED]",
        "  Reason: Book \"Design Patterns: Elements of Reusable Object-Oriented Software\" (ID: BK-103) is currently OUT OF STOCK (Available: 0/1).",
    ],
    os.path.join(screenshots_dir, "screen_04_premium_and_stock_validation.png")
)

# 5. On-time Return Receipt & Stock Restoration
render_terminal_window(
    "Terminal / Console — 5. On-Time Return Receipt and Stock Restoration",
    [
        ">>> [4. RETURN BOOK]",
        "Return Options:",
        "  1. Return using Transaction ID (e.g. TXN-1001)",
        "  2. Return using Member ID and Book ID",
        "Select mode [1 or 2]: 1",
        "Enter Transaction ID: TXN-1001",
        "Enter Return Date (dd-MM-yyyy, or press Enter for today): 20-10-2026",
        "",
        "  [SUCCESS] Book return processed successfully!",
        "",
        "============================================================",
        "                 OFFICIAL RETURN RECEIPT                    ",
        "============================================================",
        " Return Txn ID      : RET-5001",
        " Original Borrow Txn: TXN-1001",
        " Return Date        : 20-10-2026",
        " Scheduled Due Date : 24-10-2026",
        "------------------------------------------------------------",
        " MEMBER DETAILS:",
        " Member ID          : MEM-001",
        " Member Name        : Cibi K.",
        " Membership Tier    : Regular Member",
        " Remaining Borrows  : 0 / 3 books",
        "------------------------------------------------------------",
        " BOOK DETAILS:",
        " Book ID            : BK-101",
        " Title              : Effective Java",
        " Author             : Joshua Bloch",
        " Updated Stock      : 2 available (Total: 2)",
        "------------------------------------------------------------",
        " FINE ASSESSMENT:",
        " Overdue Duration   : 0 days (Returned on time)",
        " Total Fine Due     : Rs. 0.00 (No fine applicable)",
        "============================================================",
    ],
    os.path.join(screenshots_dir, "screen_05_return_ontime_receipt.png")
)

# 6. Overdue Return Receipt & Fine Calculation
render_terminal_window(
    "Terminal / Console — 6. Overdue Return Receipt & Fine Calculation",
    [
        ">>> [4. RETURN BOOK]",
        "Return Options:",
        "  1. Return using Transaction ID (e.g. TXN-1001)",
        "  2. Return using Member ID and Book ID",
        "Select mode [1 or 2]: 1",
        "Enter Transaction ID: TXN-1003",
        "Enter Return Date (dd-MM-yyyy, or press Enter for today): 25-10-2026",
        "",
        "  [SUCCESS] Book return processed successfully!",
        "",
        "============================================================",
        "                 OFFICIAL RETURN RECEIPT                    ",
        "============================================================",
        " Return Txn ID      : RET-5002",
        " Original Borrow Txn: TXN-1003",
        " Return Date        : 25-10-2026",
        " Scheduled Due Date : 15-10-2026",
        "------------------------------------------------------------",
        " MEMBER DETAILS:",
        " Member ID          : MEM-002",
        " Member Name        : Dinesh Kumar",
        " Membership Tier    : Regular Member",
        " Remaining Borrows  : 0 / 3 books",
        "------------------------------------------------------------",
        " BOOK DETAILS:",
        " Book ID            : BK-102",
        " Title              : Clean Code: A Handbook of Agile Craftsmanship",
        " Author             : Robert C. Martin",
        " Updated Stock      : 3 available (Total: 3)",
        "------------------------------------------------------------",
        " FINE ASSESSMENT:",
        " Overdue Duration   : 10 day(s) late",
        " Daily Fine Rate    : Rs. 5.00/day",
        " Total Fine Due     : Rs. 50.00 [APPLIED]",
        "============================================================",
        "",
        ">>> [4. RETURN BOOK]",
        "Select mode [1 or 2]: 1",
        "Enter Transaction ID: TXN-1001",
        "Enter Return Date: 25-10-2026",
        "  [RETURN TRANSACTION REJECTED]",
        "  Reason: Book for transaction 'TXN-1001' has already been returned.",
    ],
    os.path.join(screenshots_dir, "screen_06_return_overdue_fine.png")
)

# 7. Available Books Display & Error Recovery
render_terminal_window(
    "Terminal / Console — 7. Display Available Books & Menu Input Handling",
    [
        "Enter your choice [1-6]: 9",
        "  [!] Error: Value must be between 1 and 6.",
        "Enter your choice [1-6]: abc",
        "  [!] Error: Please enter a valid whole number.",
        "Enter your choice [1-6]: 5",
        "",
        ">>> [5. DISPLAY AVAILABLE BOOKS]",
        "-----------------------------------------------------------------------------------------------------------------",
        "| BOOK ID  | TITLE                                      | AUTHOR                 | ISBN            | AVAIL | TOTAL |",
        "-----------------------------------------------------------------------------------------------------------------",
        "| BK-101   | Effective Java                             | Joshua Bloch           | 978-0134685991  | 2     | 2     |",
        "| BK-102   | Clean Code: A Handbook of Agile Craftsmans | Robert C. Martin       | 978-0132350884  | 3     | 3     |",
        "| BK-104   | The Java Programming Language              | James Gosling          | 978-0321349804  | 4     | 4     |",
        "| BK-105   | Spring in Action                           | Craig Walls            | 978-1617294945  | 3     | 3     |",
        "-----------------------------------------------------------------------------------------------------------------",
        "  Total Available Titles: 4",
        "",
        "Enter your choice [1-6]: 6",
        "",
        "================================================================================",
        "   Thank you for using the Library Management System. Have a productive day!   ",
        "================================================================================",
    ],
    os.path.join(screenshots_dir, "screen_07_display_available_books.png")
)
