import javafx.fxml.FXML;

public class HelloController {
    importjavafx.fxml.FXML;
importjavafx.scene.control.Label;

    publicclass HelloController {
        @FXML
        privateLabel welcomeText;

        @FXML
        protected void onHelloButtonClick() {
            welcomeText.setText("Welcome to JavaFX Application!");
        }
    }
}
