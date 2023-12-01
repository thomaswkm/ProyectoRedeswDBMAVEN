import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Receptor {
    public static void main(String[] args) {
        int puertoTCP = 5050;
        int puertoUDP = 5051;

        try {
            // Conexión a la base de datos
            while (!isDatabaseAvailable("dbapp", 3306)) {
                System.out.println("Esperando a que la base de datos esté disponible...");
                Thread.sleep(10000);
            }

            Connection conexion = DriverManager.getConnection(
                    "jdbc:mysql://dbapp:3306/sistema", "root", "pwdb");

            System.out.println("Conectando con la base de datos...");

            // Iniciar hilo para escuchar mensajes UDP
            UDPListener udpListener = new UDPListener(puertoUDP);
            new Thread(udpListener).start();

            // Iniciar servidor TCP
            ServerSocket receptorSocket = new ServerSocket(puertoTCP);
            System.out.println("Escuchando en el puerto " + puertoTCP);

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
                    socket.close();
                }
            }
        } catch (IOException | SQLException | InterruptedException e) {
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

    public static boolean isDatabaseAvailable(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            // Si se puede establecer la conexión, la base de datos está disponible
            return true;
        } catch (IOException e) {
            // Si hay una excepción, la base de datos no está disponible
            return false;
        }
    }
}