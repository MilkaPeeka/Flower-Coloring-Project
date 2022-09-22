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

        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);

        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);

        int r = (int) ((r1 * iRatio) + (r2 * ratio));
        int g = (int) ((g1 * iRatio) + (g2 * ratio));
        int b = (int) ((b1 * iRatio) + (b2 * ratio));

        return r << 16 | g << 8 | b;
    }

    public static long regular(BufferedImage img){
        Color purple = new Color(190, 10, 160);
        long start_time = System.nanoTime();
        change_part(img, 0, img.getWidth(), purple);
        long end_time = System.nanoTime();
        return (end_time-start_time)/1000000;
    }
    public static boolean is_in_flower_range(Color pixel, Color bottom_range, Color upper_range){
        boolean flag =  pixel.getBlue() >= bottom_range.getBlue() && pixel.getBlue() <= upper_range.getBlue()
                && pixel.getRed() >= bottom_range.getRed() && pixel.getRed() <= upper_range.getRed()
                && pixel.getGreen() >= bottom_range.getGreen() && pixel.getGreen() <= upper_range.getGreen();

        return flag;
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

        for (int i = 0; i<threads.length; i++) {
            threads[i].join();
        }
            long end_time = System.nanoTime();
        return (end_time-start_time)/1000000;
    }

    public static void change_part(BufferedImage img, int startx, int endx, Color purple){
        for (int x = startx; x<endx; x++){
            for (int y = 0; y < img.getHeight(); y++){
                Color c = new Color(img.getRGB(x,y));
                if (is_in_flower_range(c, new Color(80,80,110), new Color(255,255,255)))
                    img.setRGB(x,y,blend(c, purple));
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedImage img = ImageIO.read(new File("many-flowers.jpg"));
        //System.out.println(regular(img));
        System.out.println(threaded(img));
        File output = new File("output.jpg");
        try {
            ImageIO.write(img, "jpg", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
