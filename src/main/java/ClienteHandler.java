import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteHandler implements Runnable {
    private final Connection conexion;
    private final Socket socket;

    public ClienteHandler(Connection conexion, Socket socket) {
        this.conexion = conexion;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // Manejar la conexión del cliente
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            String patente = entrada.readLine();

            // Obtener el recuento de coincidencias en la base de datos
            int count = obtenerCoincidencias(patente);

            // Enviar la respuesta al cliente
            salida.println("La patente ha sido vista: " + count + " veces");

            // Cerrar conexión
            socket.close();
        } catch (IOException | SQLException e ) {
            e.printStackTrace();
        }
    }

    private int obtenerCoincidencias(String patente) throws SQLException {
        int count = 0;
        String query = "SELECT COUNT(*) FROM patentes WHERE patente = "+patente;
        try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1, patente);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            }
        }
        return count;
    }
}