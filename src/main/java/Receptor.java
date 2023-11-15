import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Receptor {
    public static void main(String[] args) {
        int puerto = 5050;

        try {
            // Establecer conexión a la base de datos MySQL
            Connection conexion = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/sistema",
                    "root", // Reemplaza con tu nombre de usuario de MySQL
                    "" // Reemplaza con tu contraseña de MySQL
            );

            // Crear el socket del servidor
            ServerSocket receptorSocket = new ServerSocket(puerto);
            System.out.println("Escuchando en el puerto " + puerto);

            while (true) {
                // Esperar a que una cámara se conecte
                Socket camaraSocket = receptorSocket.accept();

                // Obtener la dirección IP y puerto de la cámara
                String direccionCamara = camaraSocket.getInetAddress().getHostAddress();
                int puertoCamara = camaraSocket.getPort();
                System.out.println("Conexión establecida con la cámara en la dirección: " +
                        direccionCamara + ", puerto: " + puertoCamara);

                // Manejar la conexión en un nuevo hilo
                Thread camaraThread = new Thread(() -> {
                    try {
                        // Obtener el stream de entrada después de la conexión
                        BufferedReader entrada = new BufferedReader(new InputStreamReader(camaraSocket.getInputStream()));

                        // Leer datos de la cámara
                        String patenteRecibida;
                        while ((patenteRecibida = entrada.readLine()) != null) {
                            System.out.println("Patente recibida de la cámara en " + direccionCamara +
                                    ", puerto " + puertoCamara + ": " + patenteRecibida);

                            // Guardar la patente en la base de datos
                            guardarPatenteEnBaseDatos(conexion, patenteRecibida);
                        }

                        // Cerrar conexión
                        camaraSocket.close();
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                });

                camaraThread.start();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void guardarPatenteEnBaseDatos(Connection conexion, String patente) throws SQLException {
        // Guardar la patente en la base de datos
        String query = "INSERT INTO patentes (patente) VALUES (?)";
        try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1, patente);
            preparedStatement.executeUpdate();
        }
    }
}
