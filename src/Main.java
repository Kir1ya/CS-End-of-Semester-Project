import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to the image file: ");
        String imagePath = scanner.nextLine();

        BufferedImage image = loadImage(imagePath);

        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();

            int maxAxis = Math.max(width, height);
            if (maxAxis > 64) {
                int newWidth = width;
                int newHeight = height;

                if (width > height) {
                    newWidth = 64;
                    newHeight = (int) Math.round((double) height / width * newWidth); //SF height
                } else {
                    newHeight = 64;
                    newWidth = (int) Math.round((double) width / height * newHeight); //SF length
                }

                BufferedImage scaledImage = scaleImage(image, newWidth, newHeight);
                image = scaledImage; // Original image -> Scaled image
            }

            int[] rArray = new int[32];
            int[] gArray = new int[32];
            int[] bArray = new int[32];

            generateRGBHistogram(image, rArray, gArray, bArray);

            System.out.println("Condensed RGB Histogram:");
            printHistogram(rArray, "Red");
            printHistogram(gArray, "Green");
            printHistogram(bArray, "Blue");
        }
    }

    public static BufferedImage loadImage(String imagePath) {
        try {
            File inputFile = new File(imagePath);
            return ImageIO.read(inputFile);
        } catch (IOException e) {
            System.out.println("Error loading the image: " + e.getMessage()); //e.getMessage = error message
            return null;
        }
    }

    public static BufferedImage scaleImage(BufferedImage originalImage, int newWidth, int newHeight) {
        //render
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        graphics2D.dispose();
        //render
        return scaledImage;
    }

    public static void generateRGBHistogram(BufferedImage image, int[] rArray, int[] gArray, int[] bArray) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                int redIndex = (int) (red / 8.0);
                int greenIndex = (int) (green / 8.0);
                int blueIndex = (int) (blue / 8.0);

                rArray[redIndex]++;
                gArray[greenIndex]++;
                bArray[blueIndex]++;
            }
        }
    }

    public static void printHistogram(int[] histogram, String color) {
        System.out.println(color + " values:");
        int maxCount = findMaxCount(histogram);

        for (int i = 0; i < histogram.length; i++) {
            int barLength = (int) ((float) histogram[i] / maxCount * 32);
            String bar = lineOutput('|', barLength);
            System.out.printf("%3d-%3d: %s%n", i * 8, (i + 1) * 8 - 1, bar);
        }
        System.out.println();
    }

    public static int findMaxCount(int[] histogram) {
        int max = 0;
        for (int count : histogram) {
            if (count > max) {
                max = count;
            }
        }
        return max;
    }

    public static String lineOutput(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}