import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AppCliente {
    private static final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        String receptorIp = System.getenv("RECEPTOR_IP");
        String receptorPort = System.getenv("RECEPTOR_PORT");

        if (receptorIp == null || receptorPort == null) {
            System.out.println("Debes proporcionar RECEPTOR_IP y RECEPTOR_PORT como variables de entorno.");
            System.exit(1);
        }

        int puertoReceptor = Integer.parseInt(receptorPort);

        try {
            Socket socketTCP = new Socket(receptorIp, puertoReceptor);
            OutputStream salida = socketTCP.getOutputStream();

            // Inicia el hilo para manejar los mensajes
            new Thread(() -> handleMessages(socketTCP)).start();

            menu(salida);  // Pasa el OutputStream al método del menú

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void menu(OutputStream salida) {
        mostrarMenu();
        switch (recuperarRespuesta()) {
            case 1:
                buscarPatente(salida);
                break;
            case 2:
                System.exit(0);
            default:
                System.out.println("Opción no válida");
        }

        menu(salida);
    }

    public static void buscarPatente(OutputStream salida) {
        System.out.println("Ingrese la patente que desea buscar:");
        Scanner scanner = new Scanner(System.in);
        String patente = scanner.nextLine();

        try {
            // Envía la patente al servidor para la búsqueda
            salida.write(patente.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int recuperarRespuesta() {
        int respuesta = -1;
        try {
            respuesta = new Scanner(System.in).nextInt();
        } catch (Exception e) {
            System.out.println("Ingresa un número");
        }
        return respuesta;
    }

    public static void mostrarMenu() {
        System.out.println("Menú\n1) Buscar Patente\n2) Salir");
    }

    private static void handleMessages(Socket socket) {
        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            while (true) {
                if (scanner.hasNextLine()) {  // Check if there is a line to read
                    String message = scanner.nextLine();
                    System.out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
