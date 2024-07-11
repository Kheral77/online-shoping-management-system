public class ClientGUI {
    Packagecom.example.onlineshopingmanagementsystem.project.clientside;

importjavafx.application.Application;
importjavafx.application.Platform;
importjavafx.beans.property.DoubleProperty;
importjavafx.beans.property.SimpleDoubleProperty;
importjavafx.beans.property.SimpleStringProperty;
importjavafx.beans.property.StringProperty;
importjavafx.collections.FXCollections;
importjavafx.collections.ObservableList;
importjavafx.geometry.Insets;
importjavafx.geometry.Pos;
importjavafx.scene.Scene;
importjavafx.scene.control.*;
importjavafx.scene.control.cell.PropertyValueFactory;
importjavafx.scene.layout.HBox;
importjavafx.scene.layout.VBox;
importjavafx.stage.Stage;
importjavafx.util.Pair;

importjava.io.Serializable;
importjava.util.ArrayList;
importjava.util.Optional;

    public classClientGUI extendsApplication {

        privateTextField nameField;
        privateTextArea descriptionArea;
        privateTextField priceField;
        privateLabel resultLabel;

        privateObservableList<Product> products;
        privateClientSocket clientSocket;

        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("Online Shop Management");

            // Create the TabPane
            TabPane tabPane = new TabPane();

            // Create the first tab for product fields
            Tab productFieldsTab = new Tab("Product");
            VBox productFieldsVBox = createProductFieldsVBox();
            productFieldsTab.setContent(productFieldsVBox);

            // Create the second tab for the cart table
            Tab cartTab = new Tab("Cart");
            Pair<TableView<Product>, VBox> cartComponents = createCartTableView();
            cartTab.setContent(cartComponents.getValue());

            // Add tabs to the TabPane
            tabPane.getTabs().addAll(productFieldsTab, cartTab);

            Scene scene = new Scene(tabPane, 600, 500);
            primaryStage.setScene(scene);

            // Initialize products list
            TableView<Product> cartTableView = cartComponents.getKey();
            products = FXCollections.observableArrayList();
            cartTableView.setItems(products);

            // Create a ClientSocket instance, passing 'this'  as a reference
            clientSocket = new ClientSocket(this);

            // Connect to the server using the clientSocket
            clientSocket.connectToServer();
            primaryStage.show();

        }

        private VBox createProductFieldsVBox() {
            nameField = new TextField();
            nameField.setPromptText("Enter name...");

            descriptionArea = new TextArea();
            descriptionArea.setPromptText("Enter description here...");
            descriptionArea.setPrefHeight(350);

            priceField = new TextField();
            priceField.setPromptText("Enter the price...");

            Button submitButton = new Button("Add to cart");
            submitButton.setOnAction(e -> sendDataToServer());

            resultLabel = new Label();

            // Create a VBox to hold the input fields, button, label, and apply styling
            VBox vBox = new VBox(10);
            vBox.setPadding(new Insets(20));
            vBox.setStyle("-fx-background-color: #f4f4f4;");
            submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #ffffff;");

            // Add components to the VBox in a vertical order
            vBox.getChildren().addAll(
                    new Label("Name:"), nameField,
                    new Label("Description:"), descriptionArea,
                    new Label("Price:"), priceField,
                    submitButton, resultLabel
            );

            Return  VBox;
        }
        private Pair<TableView<Product>, VBox> createCartTableView() {
            TableView<Product> tableView = new TableView<>();
            tableView.setEditable(true);
            TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
            TableColumn<Product, String> descriptionColumn = new TableColumn<>("Description");
            TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");

            nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
            priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
            tableView.getColumns().addAll(nameColumn, descriptionColumn, priceColumn);

            // Refresh button
            Button refreshButton = new Button("Refresh");
            refreshButton.setOnAction(e -> fetchDataFromServer());
            refreshButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #ffffff;");

            Button deleteAllButton = new Button("Delete All");
            deleteAllButton.setOnAction(e -> deleteAllData());
            deleteAllButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: #ffffff;");

            Button submitOrderButton = new Button("Submit Order");
            submitOrderButton.setOnAction(e -> submitOrder());
            submitOrderButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: #ffffff;");

            HBox buttonBox = new HBox(10);
            //buttonBox.setAlignment(Pos.BASELINE_RIGHT);
            buttonBox.getChildren().addAll(submitOrderButton, refreshButton, deleteAllButton);

            VBox vBox = new VBox(10);
            vBox.setPadding(new Insets(20));
            vBox.setStyle("-fx-background-color: #f4f4f4;");

            vBox.getChildren().addAll(tableView, buttonBox);

            return new Pair<>(tableView, vBox);

        }

        private void sendDataToServer() {
            // Retrieve data from input fields and display it
            String name = nameField.getText();
            String description = descriptionArea.getText();
            double price;

            try {
                if (name.isEmpty() || description.isEmpty()) {
                    throw new IllegalArgumentException("Name and description cannot be empty.");
                }
                price = Double.parseDouble(priceField.getText());
                // Send data to the server using the ClientSocket
                Product product = new Product(name, description, price);
                clientSocket.sendDataToServer(product);
                modal("Operation Successful", "Data sent to server: \nName: " + name + "\nDescription: " + description + "\nPrice: " + price);
                // Optionally, clear input fields
                nameField.clear();
                descriptionArea.clear();
                priceField.clear();
                // Add data to the TableView
                // products.add(new Product(name, description, price));
            } catch (NumberFormatException e) {
                resultLabel.setText("Invalid price. Please enter a numeric value.");
            } catch (IllegalArgumentException e) {
                resultLabel.setText(e.getMessage());
            }
        }
        private void deleteAllData() {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText("Delete All Data");
            confirmAlert.setContentText("Are you sure you want to delete all data?");

            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // User confirmed deletion, send request to the server or handle accordingly
                // For demonstration, let's assume you have a method deleteAllDataOnServer() in ClientSocket
                clientSocket.deleteAllDataOnServer();
            }
        }
        private void submitOrder() {
            clientSocket.submitOrder();
        }


        private void fetchDataFromServer() {
            // Request data from the server using the ClientSocket
            clientSocket.fetchDataFromServer();
        }
        public void receiveDataFromServer(ArrayList<Product> newData) {
            //System.out.println("Received data size: " + newData.size());
            // Update the TableView with the new data received from the server
            products.setAll(newData);
            //System.out.println("Data in TableView: " + newData);

            // Display a message indicating that data has been received
            //resultLabel.setText( "Data received from server.");
        }


        public void stop() {
            // Cleanup code (if any) when the application is closed
            clientSocket.disconnectFromServer();
        }

        // Model class for product
        public static class Product implements Serializable {
            private final String name;
            private final String description;
            private final double price;
            //private static final long serialVersionUID = -2973682404928746927L;

            public Product(String name, String description, double price) {
                this.name = name;
                this.description = description;
                this.price = price;
            }

            public String getName() {
                return name;
            }


            public String getDescription() {
                return description;
            }


            public double getPrice() {
                return price;
            }


            public StringProperty nameProperty() {
                return new SimpleStringProperty(name);
            }

            public StringProperty descriptionProperty() {
                return new SimpleStringProperty(description);
            }

            public DoubleProperty priceProperty() {
                return new SimpleDoubleProperty(price);
            }
        }

        public void modal(String title, String content) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        }
    }
}
