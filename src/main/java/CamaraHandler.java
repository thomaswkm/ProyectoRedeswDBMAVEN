import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CamaraHandler implements Runnable {
    private final Connection conexion;
    private final Socket camaraSocket;

    public CamaraHandler(Connection conexion, Socket camaraSocket) {
        this.conexion = conexion;
        this.camaraSocket = camaraSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(camaraSocket.getInputStream()));
            String direccionCamara = camaraSocket.getInetAddress().getHostAddress();
            int puertoCamara = camaraSocket.getPort();
            System.out.println("Conexión establecida con la cámara en la dirección: " +
                    direccionCamara + ", puerto: " + puertoCamara);

            String patenteRecibida;
            while ((patenteRecibida = entrada.readLine()) != null) {
                System.out.println("Patente recibida de la cámara en " + direccionCamara +
                        ", puerto " + puertoCamara + ": " + patenteRecibida);

                guardarPatenteEnBaseDatos(conexion, patenteRecibida);
            }

            // Cerrar conexión
            camaraSocket.close();
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
