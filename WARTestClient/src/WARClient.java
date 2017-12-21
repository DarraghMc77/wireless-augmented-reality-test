import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;

public class WARClient {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8011);
        OutputStream outputStream = socket.getOutputStream();

        BufferedImage image = ImageIO.read(new File("/Users/Darragh/eclipse-workspace/WARTestClient/src/dog.jpg"));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);

        byte[] size = ("SIZE " + Integer.toString(byteArrayOutputStream.size())).getBytes( Charset.forName("UTF-8"));
        System.out.println(size);
        outputStream.write(size);
        outputStream.write(byteArrayOutputStream.toByteArray());
        outputStream.flush();
        System.out.println("Flushed: " + System.currentTimeMillis());

        Thread.sleep(12000);
        DataInputStream is = new DataInputStream(socket.getInputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        System.out.println(in.readLine());
//		TODO: Deserialize json into object
        in.close();
        
        Thread.sleep(120000);
        System.out.println("Closing: " + System.currentTimeMillis());
        socket.close();
    }
}