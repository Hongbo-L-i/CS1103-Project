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

    // TODO: 添加其他方法，例如：getUserLoanHistory(int userId)、showAvailableBooks() 等
}
