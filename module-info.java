module com.example.onlineshopingmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.onlineshopingmanagementsystem to javafx.fxml;
    exports com.example.onlineshopingmanagementsystem;
}