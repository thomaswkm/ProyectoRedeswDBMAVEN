import java.io.*;
import java.net.Socket;

public class AppCliente {
    public static void main(String[] args) {
        String servidor = "0.0.0.0";
        int puerto = 5050;

        try {
            Socket socket = new Socket(servidor, puerto);
            BufferedWriter salida = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));

            // Enviar mensaje inicial para identificar al cliente
            salida.write("Cliente\n");
            salida.flush();

            String patente;
            while (true) {
                System.out.print("Ingrese la patente (o 'exit' para salir): ");
                patente = entrada.readLine();

                salida.write(patente + "\n");
                salida.flush();

                if ("exit".equals(patente)) {
                    break;
                }

                // Leer la respuesta del servidor
                String respuesta = recibirRespuesta(socket);
                System.out.println(respuesta);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String recibirRespuesta(Socket socket) throws IOException {
        BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return entrada.readLine();
    }
}
