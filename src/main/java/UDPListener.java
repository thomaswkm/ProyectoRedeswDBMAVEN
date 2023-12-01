import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPListener implements Runnable {
    private final int puertoUDP;

    public UDPListener(int puertoUDP) {
        this.puertoUDP = puertoUDP;
    }

    @Override
    public void run() {
        try (DatagramSocket socketUDP = new DatagramSocket(puertoUDP)) {
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(packet);

                String mensaje = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Mensaje UDP recibido: " + mensaje);

                // Aquí puedes agregar lógica adicional según el contenido del mensaje UDP
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
