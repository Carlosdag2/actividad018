module com.empresa.datastructures_javafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.empresa.datastructures_javafx to javafx.fxml;
    exports com.empresa.datastructures_javafx;
}