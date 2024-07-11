public class ClientSocket {
    Packagecom.example.onlineshopingmanagementsystem.project.clientside;

importjava.io.*;
importjava.net.Socket;
importjava.util.ArrayList ;

importstaticcom.example.onlineshopingmanagementsystem.project.clientside.ClientGUI.*;

    public classClient Socket {
        privateSocket socket;
        privateObjectOutputStream outputStream;
        privateObjectInputStream inputStream;
        privatefinal ClientGUI  clientGUI;

        public Client Socket (ClientGUI clientGUI) {
            this.ClientGUI = clientGUI;
        }

        public void connectToServer() {
            try {
                // Establish a connection to the server on port 6001
                socket = newSocket("localhost", 6001);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());

                // Assume additional initialization if needed
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void disconnectFromServer () {
            try {
                // Close the socket and streams
                if (socket != null) {
                    socket.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void sendDataToServer(Product data) {
            try {
                // Send data to the server using serialization
                outputStream.write Object(data);
                outputStream. flush ();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void deleteAllDataOnServer() {
            try {
                // Send the string "DELETE_ALL_DATA" to indicate a delete-all request
                outputStream.writeObject("DELETE_ALL_DATA");
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void submitOrder() {
            try {
                outputStream.writeObject("SUBMIT_ALL_DATA");
                outputStream.flush();
                clientGUI.modal("Operation Successful", "Your order is submitted to server.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void fetchDataFromServer() {
            try {
                // Send a request to the server for data
                outputStream.writeObject("FETCH_DATA");
                outputStream.flush();

                // Receive an ArrayList<ClientGUI.Product> in response
                Object response = input Stream.read Object();

                if (response instanceof ArrayList<?>) {
                    ArrayList<?>received Data = (ArrayList<?>) response;

                    if (!receivedData.isEmpty()) {
                        if (receivedData.getFirst() instanceof Product) {
                            @SuppressWarnings("unchecked")
                            ArrayList<Product> newData = (ArrayList<Product>) receivedData;
                            clientGUI.receiveDataFromServer(newData);
                            clientGUI.modal("Operation Successful", "Data received from server.");
                        } else {
                            System.err.println("Unexpected response format from server. ArrayList should contain ClientGUI.Product objects.");
                        }
                    } else
                    {
                        clientGUI.modal("Operation Successful", "Database is empty.");
                        @SuppressWarnings("Unchecked")
                        ArrayList<Product> newData = (ArrayList<Product>) receivedData;
                        clientGUI.receiveDataFromServer(newData);
                    }
                } else {
                    System.err.println("Unexpected response from server: " + response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                // Handle fetch data error (notify user or take appropriate action)
            }

        }



    }
}
