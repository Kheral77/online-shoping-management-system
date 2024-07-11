public class ClientHandler {
    packagecom.example.onlineshopingmanagementsystem.project.serverside;

importcom.example.onlineshopingmanagementsystem.project.clientside.ClientGUI.Product;

importjava.io.* ;
importjava.net.Socket ;
importjava.net.SocketException ;
importjava.sql.SQLException ;
importjava.util.List ;

importstaticcom.example.onlineshopingmanagementsystem.project.serverside.ServerData.*;

    public classClientHandlerimplements Runnable {
        privatefinal ServerSocketsServer;
        privateObjectInputStream inputStream;
        privateObjectOutputStream outputStream;
        privatefinal Socket clientSocket;

        publicClientHandler(Socket clientSocket, ServerSocketsServer) {
            this.clientSocket = clientSocket;
            this.server = server;

            try {
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                System.err.println("Error initializing streams: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            handleClient();
        }
        public void handleClient() {
            try {
                // Handle communication with the client (e.g., read and broadcast data)
                Object input;
                while (null != (input = inputStream.readObject())) {
                    if (input instanceof Product) {
                        System.out.println("Received data from client: " + input);
                        Product product = (Product) input;
                        saveProduct(product);
                        // Broadcast the updated product list to all clients
                        List<Product> productList = ServerData.getAllProducts();
                        server.broadcastData(productList);
                    } else if (input.equals("FETCH_DATA")) {
                        System.out.println("Client requested data: " + input);
                        // Retrieve all products from the database
                        List<Product> productList = ServerData.getAllProducts();
                        // Send the product list back to the client
                        sendData(productList);
                    }
                    else if (input.equals("DELETE_ALL_DATA")) {
                        // Handle the request to delete all data
                        deleteAllData();
                        System.out.println("Received request to delete all data from client.");
                        // Retrieve all products from the database
                        List<Product> productList = ServerData.getAllProducts();
                        // Send the product list back to the client
                        sendData(productList);
                    }
                    else if (input.equals("SUBMIT_ALL_DATA")) {
                        System.out.println("Client submitted data.");
                        submitOrders();
                    }
                }
            } catch (EOFException e) {
                System.out.println("Client disconnected.");
            } catch (SocketException e) {
                System.err.println("SocketException: Connection reset. Client may have disconnected.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                server.removeClient(this);
                closeResources();
            }
        }

        public void sendData(Object data) {
            try {
                outputStream.writeObject(data);
                outputStream.flush();
            } catch (IOException e) {
                System.err.println("Error sending data to client: " + e.getMessage());
            }
        }


        private void closeResources() {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
