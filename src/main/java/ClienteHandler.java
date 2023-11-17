import java.io.*;
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
            BufferedWriter salida = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String patente = entrada.readLine();

            // Obtener el recuento de coincidencias en la base de datos
            int count = obtenerCoincidencias(patente);

            // Enviar la respuesta al cliente
            salida.write("La patente ha sido vista: " + count + " veces\n");
            salida.flush();  // Make sure to flush the buffer to send the response immediately

            // Cerrar conexión
            socket.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private int obtenerCoincidencias(String patente) throws SQLException {
        int count = 0;
        String query = "SELECT COUNT(*) FROM patentes WHERE patente = ?";
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
