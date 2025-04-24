import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminApp extends Application {

    private MethodHouse methodHouse;

    @Override
    public void start(Stage primaryStage) {
        try {
            methodHouse = new MethodHouse("library_project_v2_fixed.db");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        VBox root = new VBox(15);

        // Book Search Max button and Label
        Button maxBIdButton = new Button("Search Max Book ID");
        Label maxBIdLabel = new Label("Current MAX:\nRecommend Next ID:");

        maxBIdButton.setOnAction(e -> {
            int maxId = methodHouse.searchMaxBookID();
            maxBIdLabel.setText("Current Book MAX: " + maxId + "\nRecommend Next Book ID: " + (maxId + 1));
        });

        HBox topBox = new HBox(10, maxBIdButton, maxBIdLabel);
        
        // Call InsertDataPane to create a bookPane, for insert data to Book table
        InsertDataPane bookPane = new InsertDataPane(
            new String[]{"BOOK ID", "Title", "Author", "Category ID"},
            "Add BOOK"
        );
        
        bookPane.getActionButton().setOnAction(e -> {
            try {
                int bookId = Integer.parseInt(bookPane.getFieldText(0));
                String title = bookPane.getFieldText(1);
                String author = bookPane.getFieldText(2);
                int categoryId = Integer.parseInt(bookPane.getFieldText(3));
                methodHouse.addNewBook(bookId, title, author, categoryId);
            } catch (Exception ex) {
                System.out.println("❌ fail input：" + ex.getMessage());
            }
        });

        
        
        
        Button catergoryCheckButton = new Button("Check what category we have");
        catergoryCheckButton.setOnAction(e -> {
            methodHouse.printWholeTable("Categories");
        });
        
        
        // Call InsertDataPane to create a catergoryIdNamePane, for insert data to Categories table
        InsertDataPane catergoryIdNamePane = new InsertDataPane(
            new String[]{"Category ID", "Category Name"},
            "Add Category"
        );
        
        catergoryIdNamePane.getActionButton().setOnAction(e -> {
            try {
                int catergoryId = Integer.parseInt(catergoryIdNamePane.getFieldText(0));
                String categoryName = catergoryIdNamePane.getFieldText(1);
                methodHouse.addNewCategory(catergoryId, categoryName);
            } catch (Exception ex) {
                System.out.println("❌ fail input：" + ex.getMessage());
            }
        });
        
        // User Search Max button and Label
        Button maxUIdButton = new Button("Search Max User ID");
        Label maxUIdLabel = new Label("Current MAX:\nRecommend Next ID:");

        maxUIdButton.setOnAction(e -> {
            int maxId = methodHouse.searchMaxUserID();
            maxUIdLabel.setText("Current User MAX: " + maxId + "\nRecommend Next User ID: " + (maxId + 1));
        });

        HBox userBox = new HBox(10, maxUIdButton, maxUIdLabel);
        
        // Call InsertDataPane to create a catergoryIdNamePane, for insert data to Categories table
        InsertDataPane addUserPane = new InsertDataPane(
            new String[]{"User ID", "User Name", "User role"},
            "Add User"
        );
        
        addUserPane.getActionButton().setOnAction(e -> {
            try {
                int userId = Integer.parseInt(addUserPane.getFieldText(0));
                String userName = addUserPane.getFieldText(1);
                String role = addUserPane.getFieldText(2);
                methodHouse.addNewUser(userId, userName,role);
            } catch (Exception ex) {
                System.out.println("❌ fail input：" + ex.getMessage());
            }
        });
        
        // User Search Max button and Label
        Button maxLIdButton = new Button("Search Max Loan ID");
        Label maxLIdLabel = new Label("Current MAX:\nRecommend Next ID:");

        maxLIdButton.setOnAction(e -> {
            int maxId = methodHouse.searchMaxLoanID();
            maxLIdLabel.setText("Current Loan MAX: " + maxId + "\nRecommend Next Loan ID: " + (maxId + 1));
        });

        HBox LoanBox = new HBox(10, maxLIdButton, maxLIdLabel);
        
        
        InsertDataPane addLoanPane = new InsertDataPane(
            new String[]{"Loan ID", "Book ID", "User ID", "Loan Date(2000-01-01 format)",  "Due Date(2000-01-01 format)"},
            "Add Loan"
        );
        
        addLoanPane.getActionButton().setOnAction(e -> {
            try {
                int loanId = Integer.parseInt(addLoanPane.getFieldText(0));
                int bookId = Integer.parseInt(addLoanPane.getFieldText(1));
                int userId = Integer.parseInt(addLoanPane.getFieldText(2));
                String loanDate = addLoanPane.getFieldText(3);
                String dueDate = addLoanPane.getFieldText(4);

                methodHouse.addNewLoan(loanId, bookId,userId,loanDate,dueDate);
            } catch (Exception ex) {
                System.out.println("❌ fail input：" + ex.getMessage());
            }
        });
        
        
        
        root.getChildren().addAll(topBox, bookPane,catergoryCheckButton,
        catergoryIdNamePane,userBox, addUserPane,LoanBox,addLoanPane);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Book Manager (ctrl+T open console)");
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> methodHouse.close());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
