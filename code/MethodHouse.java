import java.sql.*;

public class MethodHouse {
    private Connection conn;

    // Connect Data Base
    public MethodHouse(String dbPath) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    // Close
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Search Book by keyword(title)
    public void searchBooksByKeyword(String keyword) {
        String sql = """
            SELECT Books.title, Loans_Status.loan_status
            FROM Books
            JOIN Loans_Status ON Books.book_id = Loans_Status.book_id
            WHERE LOWER(Books.title) LIKE ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nSearch Results:");
            while (rs.next()) {
                String title = rs.getString("title");
                String status = rs.getString("loan_status");
                System.out.println("- " + title + " (" + status + ")");
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void getUserLoanHistory(int userId) {
        String sql = """
            SELECT Books.title, Loans_History.loan_date, Loans_History.return_date
            FROM Loans_History
            JOIN Books ON Loans_History.book_id = Books.book_id
            WHERE Loans_History.user_id = ?
        """;
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
    
            System.out.println("\n[Loan History] User ID: " + userId);
            while (rs.next()) {
                String title = rs.getString("title");
                String loanDate = rs.getString("loan_date");
                String returnDate = rs.getString("return_date");
    
                String display = "- " + title + " loanDate: " + loanDate;
                display += (returnDate != null) ? "，returnDate: " + returnDate : "，Unreturned";
                System.out.println(display);
            }
    
        } catch (SQLException e) {
            System.out.println("Fail to find record " + e.getMessage());
        }
    }
    
    public void listBooksByCategory(String category) {
        String sql = """
        
            SELECT Books.title, Categories.category_name
            FROM Books
            JOIN Book_Category ON Books.book_id = Book_Category.book_id
            JOIN Categories ON Book_Category.category_id = Categories.category_id
            WHERE LOWER (category_name) LIKE ?

        """;
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + category.toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();
    
            System.out.println("\n Search Result: " );
            while (rs.next()) {
                String title = rs.getString("title");
                String category_name = rs.getString("category_name");
                
    
                String display = "- " + title + " (category_name: " + category_name +" )";
                
                System.out.println(display);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public void addNewBook(int bookId, String title, String author, int categoryId) {
        String insertBookSQL = "INSERT INTO Books (book_id, title, author) VALUES (?, ?, ?)";
        String insertCategorySQL = "INSERT INTO Book_Category (book_id, category_id) VALUES (?, ?)";
    
        try {
            // we want to commit if both insert success, so turn off AutoCommit
            conn.setAutoCommit(false); 
    
            // Step 1: Insert Book
            PreparedStatement pstmt1 = conn.prepareStatement(insertBookSQL);
            pstmt1.setInt(1, bookId);
            pstmt1.setString(2, title);
            pstmt1.setString(3, author);
            pstmt1.executeUpdate();
    
            // Step 2: Insert Book_category Relation
            PreparedStatement pstmt2 = conn.prepareStatement(insertCategorySQL);
            pstmt2.setInt(1, bookId);
            pstmt2.setInt(2, categoryId);
            pstmt2.executeUpdate();
            
            //call insertLoanStatus to set book loan status to avaliable
            insertLoanStatus(bookId);
            
            conn.commit(); // commit it if add sussecfully
            System.out.println("Book add. New Book ID: " + bookId);
    
        }
        catch (SQLException e) 
        {
            if (e.getMessage().contains("PRIMARY KEY")) {
                System.out.println("❌ Book ID exist,please try another ID.");
            } else {
                System.out.println("❌ fail to insert：" + e.getMessage());
            }
        
            try {
                conn.rollback(); // rollback when exception happened
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
        finally {
            try {
                conn.setAutoCommit(true); // turn on AutoCommit back
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int searchMaxId(String idColumnName, String tableName) {
        String sql = "SELECT MAX(" + idColumnName + ") FROM " + tableName;
        int result = -1; // if return -1, something wrong
    
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("❌ fail to find max id：" + e.getMessage());
        }
    
        return result;
    }

    
    public int searchMaxBookID() {
        return searchMaxId("book_id", "Books");
    }
    
    public int searchMaxUserID() {
        return searchMaxId("user_id", "Users");
    }
    
    public int searchMaxLoanID() {
        return searchMaxId("loan_id", "Loans_History");
    }

    
    public void printWholeTable(String tableName) {
        String sql = "SELECT * FROM " + tableName;
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
    
            System.out.println("\n--- Table: " + tableName + " ---");
    
            //Get Table information
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
    
            // print the table titles
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(meta.getColumnName(i) + "\t");
            }
            System.out.println("\n" + "-".repeat(40));
    
            // print content
            boolean hasRow = false;
            while (rs.next()) {
                hasRow = true;
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
    
            if (!hasRow) {
                System.out.println("No data in Table");
            }
    
        } catch (SQLException e) {
            System.out.println("Fail to read the table：" + e.getMessage());
        }
    }
    
    public void addNewCategory(int categoryId, String categoryName) {
        String insertCategoryIDSQL = "INSERT INTO Categories (category_id,category_name) VALUES (?, ?)";
    
        try {
            // we want to commit if all insert success, so turn off AutoCommit
            conn.setAutoCommit(false); 
    
            // Insert New Category to Category Table
            PreparedStatement pstmt1 = conn.prepareStatement(insertCategoryIDSQL);
            pstmt1.setInt(1, categoryId);
            pstmt1.setString(2, categoryName);
            pstmt1.executeUpdate();
    

            conn.commit(); // commit it if add sussecfully
            System.out.println("New Category add. New category ID & name: " + categoryId+" "+ categoryName);
    
        }
        catch (SQLException e) 
        {
            if (e.getMessage().contains("PRIMARY KEY")) {
                System.out.println("❌ Category ID exist,please try another ID.");
            } else {
                System.out.println("❌ fail to insert：" + e.getMessage());
            }
        
            try {
                conn.rollback(); // rollback when exception happened
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
        finally {
            try {
                conn.setAutoCommit(true); // Restore auto-commit mode
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void addNewUser(int userId, String userName, String role) {
        String insertCategoryIDSQL = "INSERT INTO Users (user_id, name, role) VALUES (?, ?, ?)";
    
        try {
            // we want to commit if all insert success, so turn off AutoCommit
            conn.setAutoCommit(false); 
    
            // Insert New Category to Category Table
            PreparedStatement pstmt1 = conn.prepareStatement(insertCategoryIDSQL);
            pstmt1.setInt(1, userId);
            pstmt1.setString(2, userName);
            pstmt1.setString(3, role);
            pstmt1.executeUpdate();
    

            conn.commit(); // commit it if add sussecfully
            System.out.println("New User add. New User ID & name & role: " + userId+" "+ userName + " "+ role);
    
        }
        catch (SQLException e) 
        {
            if (e.getMessage().contains("PRIMARY KEY")) {
                System.out.println("❌ User ID exist,please try another ID.");
            } else {
                System.out.println("❌ fail to insert：" + e.getMessage());
            }
        
            try {
                conn.rollback(); // rollback when exception happened
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
        finally {
            try {
                conn.setAutoCommit(true); // Restore auto-commit mode
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void addNewLoan(int loanId, int bookId, int userId, String loanDate, String dueDate) {
        String insertLoanHistory = "INSERT INTO Loans_History (loan_id, book_id, user_id, loan_date, due_date, return_date) VALUES (?, ?, ?, ?, ?, null)";
        String updateLoanStatus = "UPDATE Loans_Status SET loan_status = 'Loaned' WHERE book_id = ?";
    
        try {
            conn.setAutoCommit(false); 
    
            // Step 1: insert Loan History
            PreparedStatement pstmt1 = conn.prepareStatement(insertLoanHistory);
            pstmt1.setInt(1, loanId);
            pstmt1.setInt(2, bookId);
            pstmt1.setInt(3, userId);
            pstmt1.setString(4, loanDate);
            pstmt1.setString(5, dueDate);
            pstmt1.executeUpdate();
    
            // Step 2: update book status to Loaned
            PreparedStatement pstmt2 = conn.prepareStatement(updateLoanStatus);
            pstmt2.setInt(1, bookId);
            pstmt2.executeUpdate();
    
            conn.commit();
            System.out.println("✅ successfully add Loan history and update Book status");
    
            pstmt1.close();
            pstmt2.close();
        } catch (SQLException e) {
            System.out.println("❌fail to insert or update：" + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertLoanStatus(int bookId) {
        String sql = "INSERT INTO Loans_Status (book_id, loan_status) VALUES (?, 'Available')";
    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
            System.out.println("✅ Book ID " + bookId + " Add Book Status：Available");
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE") || e.getMessage().contains("PRIMARY KEY")) {
                System.out.println(" Status record exist. ");
            } else {
                System.out.println("❌ fail to insert Loan_Status ：" + e.getMessage());
            }
        }
    }

    
    
}
