package chess;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChessApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(getClass().getResource("Testing.fxml"));
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        Scene scene = new Scene(parent, 750, 630);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chessboard");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(ChessApp.class, args);
    } 
}
