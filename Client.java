import javax.swing.*;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);

            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png");
            fileChooser.setFileFilter(filter);
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                BufferedImage image = ImageIO.read(selectedFile);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                baos.flush();
                byte[] imageData = baos.toByteArray();
                baos.close();

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(imageData);
                outputStream.flush();

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                int[] redOnly = (int[]) inputStream.readObject();
                int[] greenOnly = (int[]) inputStream.readObject();
                int[] blueOnly = (int[]) inputStream.readObject();

                inputStream.close();
                outputStream.close();
                socket.close();

                BufferedImage redComponent = createImage(redOnly, image.getWidth(), image.getHeight());
                BufferedImage greenComponent = createImage(greenOnly, image.getWidth(), image.getHeight());
                BufferedImage blueComponent = createImage(blueOnly, image.getWidth(), image.getHeight());

                displayImages(redComponent, greenComponent, blueComponent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage createImage(int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }

    private static void displayImages(BufferedImage redComponent, BufferedImage greenComponent, BufferedImage blueComponent) {
        JFrame frame = new JFrame("RGB Components");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CustomPanel panel = new CustomPanel(redComponent, greenComponent, blueComponent);
        frame.getContentPane().add(panel);

        frame.setVisible(true);
    }
}

class CustomPanel extends JPanel {
    private BufferedImage redComponent;
    private BufferedImage greenComponent;
    private BufferedImage blueComponent;

    public CustomPanel(BufferedImage redComponent, BufferedImage greenComponent, BufferedImage blueComponent) {
        this.redComponent = redComponent;
        this.greenComponent = greenComponent;
        this.blueComponent = blueComponent;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(redComponent, 0, 0, getWidth()/3, getHeight(), null);
        g.drawImage(greenComponent, getWidth()/3, 0, getWidth()/3, getHeight(), null);
        g.drawImage(blueComponent, 2*getWidth()/3, 0, getWidth()/3, getHeight(), null);

    }

}
