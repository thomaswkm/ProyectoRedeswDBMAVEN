import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Receptor {
    public static void main(String[] args) {
        int puerto = 5050;

        try {
            Connection conexion = DriverManager.getConnection(
                    "jdbc:mysql://172.17.0.2:3306/sistema", "root", "pw");

            ServerSocket receptorSocket = new ServerSocket(puerto);
            System.out.println("Escuchando en el puerto " + puerto);

            while (true) {
                Socket socket = receptorSocket.accept();
                // Determinar si es una conexión de cámara o cliente
                String tipoEntidad = obtenerTipoEntidad(socket);

                if ("Camara".equals(tipoEntidad)) {
                    CamaraHandler camaraHandler = new CamaraHandler(conexion, socket);
                    new Thread(camaraHandler).start();
                } else if ("Cliente".equals(tipoEntidad)) {
                    ClienteHandler clienteHandler = new ClienteHandler(conexion, socket);
                    new Thread(clienteHandler).start();
                } else {
                    // Manejar otro tipo de conexión o desconectar
                    socket.close();
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }


    public static String obtenerTipoEntidad(Socket socket) {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return entrada.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}