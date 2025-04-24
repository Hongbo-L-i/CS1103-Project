import java.sql.*;

public class ReadOneTable {

    public static void main(String[] args) {
        String url = "jdbc:sqlite:library_project_v2_fixed.db";

        String[] tables = {
            "Users"
        };

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println(" Connected to database.");

                for (String table : tables) {
                    System.out.println("\n--- Table: " + table + " ---");
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);

                    // Get metadata to print column names
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(meta.getColumnName(i) + "\t");
                    }
                    System.out.println("\n" + "-".repeat(40));

                    // Print each row
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.print(rs.getString(i) + "\t");
                        }
                        System.out.println();
                    }

                    rs.close();
                    stmt.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
