import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainFXApp extends Application {

    private MethodHouse methodHouse;

    @Override
    public void start(Stage primaryStage) {
        try {
            // 初始化 MethodHouse 并连接数据库
            methodHouse = new MethodHouse("library_project_v2_fixed.db");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        TextField searchField = new TextField();
        searchField.setPromptText("Please enter Key Words");

        Button searchButton = new Button("Book Search by title");
        searchButton.setOnAction(e -> {
            String keyword = searchField.getText();
            methodHouse.searchBooksByKeyword(keyword);
        });
        
        TextField searchField2 = new TextField();
        searchField2.setPromptText("Please enter UserID");
        
        Button userSearchButton = new Button("User Loan History Search");
        userSearchButton.setOnAction(e -> {
            try {
                int userId = Integer.parseInt(searchField2.getText());
                methodHouse.getUserLoanHistory(userId);
            } catch (NumberFormatException ex) {
                System.out.println("❌ Please enter valid User ID！");
            }
        });
        
        TextField searchField3 = new TextField();
        searchField3.setPromptText("Please enter Catergory Key Words");
        
        Button catergorySearchButton = new Button("Book Search by Catergory");
        catergorySearchButton.setOnAction(e -> {
            String category = searchField3.getText();
            methodHouse.listBooksByCategory(category);
        });


        VBox root = new VBox(10, searchField, searchButton,searchField2, userSearchButton,searchField3, catergorySearchButton);
        root.setPrefSize(400, 300);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Book Search");
        primaryStage.show();

        // close connection
        primaryStage.setOnCloseRequest(e -> methodHouse.close());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
