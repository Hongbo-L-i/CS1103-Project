import java.sql.*;

public class MethodHouse {
    private Connection conn;

    // 构造器：连接数据库
    public MethodHouse(String dbPath) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    // 关闭连接
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

    // 示例功能：根据关键字模糊搜索书名和借阅状态
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

    
    // TODO: 添加其他方法，例如：getUserLoanHistory(int userId)、showAvailableBooks() 等
}
