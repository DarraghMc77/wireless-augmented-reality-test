import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class WARClient {
    
    public static void main(String[] args) throws Exception {
        
        long start1 = System.currentTimeMillis();
        long count = 1;
        Socket socket = new Socket("192.168.6.131", 8046);
        long average = 0;
        
        try {
            while(true) {
                long start2 = System.currentTimeMillis();
                //                    System.out.println("top of while");
                OutputStream outputStream = socket.getOutputStream();
                BufferedImage image = ImageIO.read(new File("/Users/Darragh/eclipse-workspace/WARTestClient/src/dog.jpg"));
                
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", byteArrayOutputStream);
                
                byte[] size = ("SIZE " + Integer.toString(byteArrayOutputStream.size())).getBytes(Charset.forName("UTF-8"));
                System.out.println(size);
                outputStream.write(size);
                outputStream.write(byteArrayOutputStream.toByteArray());
                outputStream.flush();
                System.out.println("Flushed: " + System.currentTimeMillis());
                
                //                Thread.sleep(12000);
                DataInputStream is = new DataInputStream(socket.getInputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                System.out.println(in.readLine());
                average = (start2 - System.currentTimeMillis());
                System.out.println("time: " + (average));
                //                count += 1;
                //                TODO: Deserialize json into object
                //                 in.close();
                
                Scanner reader = new Scanner(System.in);  // Reading from System.in
                System.out.println("Enter a number: ");
                int n = reader.nextInt();
                if(n==1) {
                    break;
                }
                
            }
        }
        
        finally{
            System.out.println("Closing: " + System.currentTimeMillis());
            socket.close();
        }
        
        
        //        Thread.sleep(120000);
        
    }
    
}
