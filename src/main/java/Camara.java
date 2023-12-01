import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class Camara {
    public static void main(String[] args) {
        int puertoTCP = 5050;
        int puertoUDP = 5051;

        while (true) {
            try {
                // Conexión TCP
                while (!isServerAvailable("receptor", puertoTCP)) {
                    System.out.println("Esperando a que el receptor de datos esté disponible...");
                    Thread.sleep(10000);
                }

                Socket socketTCP = new Socket("receptor", puertoTCP);
                OutputStream salida = socketTCP.getOutputStream();
                salida.write("Camara\n".getBytes());

                // Enviar mensaje UDP
                enviarMensajeUDP("receptor", puertoUDP, "status up");

                // Generar y enviar patentes cada cierto tiempo
                while (true) {
                    String enviarPatente = generarPatenteChilena() + "\n";
                    salida.write(enviarPatente.getBytes());
                    enviarMensajeUDP("receptor", puertoUDP, "camara: "+ InetAddress.getLocalHost() + " STATUS: UP");
                    int tiempoEspera = new Random().nextInt(36000);
                    Thread.sleep(tiempoEspera);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static String generarPatenteChilena() {
        char[] letras = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
                'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                'W', 'X', 'Y', 'Z'};
        int[] numeros = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        StringBuilder patente = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            char letra = letras[new Random().nextInt(letras.length)];
            patente.append(letra);
        }

        for (int i = 0; i < 3; i++) {
            int numero = numeros[new Random().nextInt(numeros.length)];
            patente.append(numero);
        }

        return patente.toString();
    }

    public static boolean isServerAvailable(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static void enviarMensajeUDP(String host, int puerto, String mensaje) throws IOException {
        try (DatagramSocket socketUDP = new DatagramSocket()) {
            InetAddress direccionDestino = InetAddress.getByName(host);
            byte[] buffer = mensaje.getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, direccionDestino, puerto);
            socketUDP.send(packet);
        }
    }
}
