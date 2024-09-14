module chess {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens chess to javafx.fxml, javafx.graphics;
    exports chess;
}
