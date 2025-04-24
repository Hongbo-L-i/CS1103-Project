import javafx.scene.control.*;
import javafx.scene.layout.*;

public class InsertDataPane extends VBox {
    private TextField[] textFields;
    private Button actionButton;

    public InsertDataPane(String[] labels, String buttonText) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        textFields = new TextField[labels.length];

        // First Row：Label
        for (int i = 0; i < labels.length; i++) {
            grid.add(new Label(labels[i]), i, 0);
        }

        // Second Row：TextField
        for (int i = 0; i < labels.length; i++) {
            textFields[i] = new TextField();
            grid.add(textFields[i], i, 1);
        }

        // Third Row：button
        actionButton = new Button(buttonText);

        this.getChildren().addAll(grid, actionButton);
    }

    public TextField[] getTextFields() {
        return textFields;
    }

    public Button getActionButton() {
        return actionButton;
    }

    public String getFieldText(int index) {
        return textFields[index].getText();
    }
}
