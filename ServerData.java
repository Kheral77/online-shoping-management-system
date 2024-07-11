public class ServerData
{
    Packagecom. example.onlineshopingmanagementsystem.project.serverside;

importcom.example.onlineshopingmanagementsystem.project.clientside.ClientGUI.*;

importjava.sql.*;
importjava.util.ArrayList;
importjava.util.List;

    public class Server Data {
        private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/onlineshoppingmanagementsystem";
        private static final String USER = "root";
        private static final String PASSWORD = "12345678";

        // Save product data to the database
        public static void saveProduct(Product product) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO products (name, description, price) VALUES (?, ?, ?)")) {

                preparedStatement.setString(1, product.getName());
                preparedStatement.setString(2, product.getDescription());
                preparedStatement.setDouble(3, product.getPrice());

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void submitOrders() {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                 PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM orders");
                 ResultSet resultSet = selectStatement.executeQuery()) {

                // Check if the orders table has existing data
                if (resultSet.next()) {
                    // Orders table has data, delete existing data
                    try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM orders")) {
                        deleteStatement.executeUpdate();
                    }
                }

                // Now, insert new data into the orders table
                try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO orders (name, description, price) VALUES (?, ?, ?)")) {
                    // Assuming you are selecting data from the 'products' table
                    try (PreparedStatement productsSelectStatement = connection.prepareStatement("SELECT * FROM products");
                         ResultSet productsResultSet = productsSelectStatement.executeQuery()) {

                        while (productsResultSet.next()) {
                            String name = productsResultSet.getString("name");
                            String description = productsResultSet.getString("description");
                            double price = productsResultSet.getDouble("price");

                            insertStatement.setString(1, name);
                            insertStatement.setString(2, description);
                            insertStatement.setDouble(3, price);

                            insertStatement.executeUpdate();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }



        public static void deleteAllData() throws SQLException {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                 Statement statement = connection.createStatement()) {

                // Perform the delete operation
                statement.executeUpdate("DELETE FROM products;");

                // Perform the delete operation on the orders table
                statement.executeUpdate("DELETE FROM orders;");
            }
        }

        // Retrieve all products from the database
        public static List<Product> getAllProducts() {
            List<Product> productList = new ArrayList<>();

            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM products")) {

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    double price = resultSet.getDouble("price");

                    Product product = new Product(name, description, price);
                    productList.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return productList;
        }
    }
}
