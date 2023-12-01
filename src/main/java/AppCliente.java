import java.io.*;
import java.net.Socket;

public class AppCliente {
    public static void main(String[] args) {
        String servidor = "receptor";
        int puerto = 5050;

        while (true) {
            try {
                while (!isServerAvailable(servidor, puerto)) {
                    System.out.println("Esperando a que el servidor esté disponible...");
                    Thread.sleep(10000);
                }

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
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static String recibirRespuesta(Socket socket) throws IOException {
        BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return entrada.readLine();
    }

    private static boolean isServerAvailable(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
