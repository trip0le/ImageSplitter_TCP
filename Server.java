import java.io.*;
import java.net.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server started. Waiting for a client...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

            byte[] imageData = (byte[]) inputStream.readObject();
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight();
            int width = image.getWidth();

            int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

            int[] redOnly = new int[pixels.length];
            int[] greenOnly = new int[pixels.length];
            int[] blueOnly = new int[pixels.length];

            for (int i = 0; i < pixels.length; i++) {
                int red = (pixels[i] >> 16) & 0xFF;
                int green = (pixels[i] >> 8) & 0xFF;
                int blue = pixels[i] & 0xFF;

                redOnly[i] = (red << 16) | 0x000000;

                greenOnly[i] = (0 << 16) | (green << 8) | 0x000000;

                blueOnly[i] = (0 << 16) | (0 << 8) | blue;
            }

            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStream.writeObject(redOnly);
            outputStream.writeObject(greenOnly);
            outputStream.writeObject(blueOnly);

            System.out.println("RGB components sent to the client.");

            inputStream.close();
            outputStream.close();
            clientSocket.close();
            serverSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
