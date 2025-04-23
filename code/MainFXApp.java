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

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String keyword = searchField.getText();
            methodHouse.searchBooksByKeyword(keyword);
        });

        VBox root = new VBox(10, searchField, searchButton);
        root.setPrefSize(400, 200);

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