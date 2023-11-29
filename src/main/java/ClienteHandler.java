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
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter salida = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String patente;
            while ((patente = entrada.readLine()) != null) {
                if (patente.equals("exit")) {
                    // Si el cliente envía "exit", salir del bucle
                    break;
                }

                int count = obtenerCoincidencias(patente);

                salida.write("La patente ha sido vista: " + count + " veces\n");
                salida.newLine();  // Agregar nueva línea para indicar el final del mensaje
                salida.flush();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
