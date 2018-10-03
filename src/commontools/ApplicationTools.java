/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commontools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import javafx.embed.swing.SwingFXUtils;

import javax.swing.JLabel;
import trainingdata.ImageData;

/**
 *
 * @author User
 */
public class ApplicationTools {

    public static LocalDateTime getTime() {
        return LocalDateTime.now();
    }

    public static String formatTime(LocalDateTime now) {
        return now.getDayOfMonth() + "-" + now.getMonthValue() + "-" + now.getYear() + " " + now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();
    }

    public static String timeElasped(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        String result = "";
        //System.out.println("DURATION: " + fromDateTime.toString() + " TO " + toDateTime.toString());
        LocalDateTime tempDateTime = LocalDateTime.from(fromDateTime);

        long years = tempDateTime.until(toDateTime, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears(years);

        long months = tempDateTime.until(toDateTime, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);

        long days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);

        long hours = tempDateTime.until(toDateTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until(toDateTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);

        long seconds = tempDateTime.until(toDateTime, ChronoUnit.SECONDS);

        result = years + " Y " + months + " M " + days + " D " + hours + " H " + minutes + " m " + seconds + " s";
        return result;
    }

    public static long[] timeElapsedLong(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        LocalDateTime tempDateTime = LocalDateTime.from(fromDateTime);
        long years = tempDateTime.until(toDateTime, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears(years);

        long months = tempDateTime.until(toDateTime, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);

        long days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);

        long hours = tempDateTime.until(toDateTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until(toDateTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);

        long seconds = tempDateTime.until(toDateTime, ChronoUnit.SECONDS);

        long[] res = {years, months, days, hours, minutes, seconds};
        return res;
    }

    public static Matrix textToMatrix(String text) {
        Matrix output = null;
        String[] rows = text.split("\n");
        if (rows.length > 0) {
            String[] columns = rows[0].split(" ");
            if (columns.length > 0) {
                int r = rows.length;
                int c = columns.length;
                Matrix m = new Matrix(r, c);
                for (int i = 0; i < r; i++) {
                    String[] column = rows[i].split(" ");
                    if (column.length == c) {
                        for (int j = 0; j < c; j++) {
                            float data = Float.parseFloat(column[j]);
                            m.data[i][j] = data;
                        }
                    } else {
                        return null;
                    }

                }
                return m;
            }
        }
        return output;
    }

    public static String matrixToText(Matrix m) {
        StringBuilder output = new StringBuilder();
        float[][] data = m.data;
        for (int i = 0; i < m.rows; i++) {
            for (int j = 0; j < m.columns; j++) {
                output.append(data[i][j]);
                if (j != m.columns - 1) {
                    output.append(" ");
                }
            }
            output.append("\n");
        }
        return output.toString();
    }

    public static float[] imageToPixel(BufferedImage image) {
        image = ImageData.rgbToGrayScale(image);
        float[] output = new float[image.getWidth() * image.getHeight()];
        int current = 0;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                output[current] = (image.getRGB(i, j) & 0xFF) / 255; //Get grayscal value of each pixel
            }

        }
        return output;
    }

    public static BufferedImage getCopy(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics g = result.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;

    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
    public static javafx.scene.image.Image bufferedToImage(BufferedImage img){
        return SwingFXUtils.toFXImage(img, null);
    }
    public static BufferedImage getEdges(BufferedImage image){
        BufferedImage gray_image = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gray_image.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        CannyEdgeDetector ced = new CannyEdgeDetector();
        ced.setSourceImage(gray_image);
        ced.process();
        return ced.getEdgesImage();
    }
    public static int[] getRGB(int value){        
        int red = (value&0xff);
        int green = (value&0xff00) >> 8;
        int blue = (value&0xff0000) >> 16;
        int[] result = {red, green, blue};
        return result;
    }
}
