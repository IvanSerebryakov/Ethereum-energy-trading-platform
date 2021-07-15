package ConnectionUDP;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UDPClass {

    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    byte[] msg = new byte[256];

    private String newMsg;

    public DatagramSocket connectionUDP(int sourcePort){
        try {
//
            datagramSocket = new DatagramSocket(sourcePort);

        } catch (SocketException socketException) {
            System.out.println("Address already in use");
        }
        return datagramSocket;
    }

    public void sendingUDP(DatagramSocket datagramSocket, int destPort, String signal){
        try {
            datagramPacket = new DatagramPacket(
                    signal.getBytes(StandardCharsets.UTF_8),
                    signal.length(),
                    InetAddress.getLocalHost(),
                    destPort
            );
            datagramSocket.send(datagramPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receivingUDP(DatagramSocket datagramSocket, int destPort){

        try {
            datagramPacket = new DatagramPacket(
                    msg,
                    256,
                    InetAddress.getLocalHost(),
                    destPort);
            datagramSocket.receive(datagramPacket);
            //System.out.println("Datatatatapacketet " + Arrays.toString(datagramPacket.getData()));
            newMsg = new String(datagramPacket.getData(), 0, 256);

            System.out.println("----------- new msg in receiving UDP --------------- " + newMsg);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newMsg;
    }

}
