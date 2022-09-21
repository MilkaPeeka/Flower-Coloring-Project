import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static int blend(Color c1, Color c2){
        float ratio = 0.45F;
        float iRatio = 0.55F;

        int i1 = c1.getRGB();
        int i2 = c2.getRGB();

        int a1 = (i1 >> 24 & 0xff);
        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);

        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);

        int a = (int) ((a1 * iRatio) + (a2 * ratio));
        int r = (int) ((r1 * iRatio) + (r2 * ratio));
        int g = (int) ((g1 * iRatio) + (g2 * ratio));
        int b = (int) ((b1 * iRatio) + (b2 * ratio));

        return a << 24 | r << 16 | g << 8 | b;
    }

    public static long regular(BufferedImage img){
        long start_time = System.nanoTime();

        Color purple = new Color(190, 10, 160);
        int width = img.getWidth();
        int height = img.getHeight();

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                Color c = new Color(img.getRGB(x,y));
                if (c.getRed() >= 150 && c.getGreen() >= 150 && c.getBlue() >= 140)
                    img.setRGB(x,y,blend(c, purple));
            }
        }
        long end_time = System.nanoTime();

        return (end_time-start_time)/1000000;
    }


    public static long threaded(BufferedImage img) throws InterruptedException {
        long start_time = System.nanoTime();
        Color purple = new Color(190, 10, 160);
        Thread[] threads = new Thread[12];
        int width = img.getWidth() / threads.length;

        for (int i = 0; i<threads.length; i++){
            int finalI = i;
            threads[i] = new Thread(() -> change_part(img, finalI *width, (finalI +1)* width, purple));
            threads[i].start();
        }

        threads[threads.length-1].join();

        long end_time = System.nanoTime();
        return (end_time-start_time)/1000000;
    }

    public static void change_part(BufferedImage img, int startx, int endx, Color purple){
        for (int x = startx; x<endx; x++){
            for (int y = 0; y < img.getHeight(); y++){
                Color c = new Color(img.getRGB(x,y));
                if (c.getRed() >= 150 && c.getGreen() >= 150 && c.getBlue() >= 140)
                    img.setRGB(x,y,blend(c, purple));
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedImage img = ImageIO.read(new File("many-flowers.jpg"));
        System.out.println(regular(img));
        System.out.println(threaded(img));
        File output = new File("output.jpg");
        try {
            ImageIO.write(img, "jpg", output);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
