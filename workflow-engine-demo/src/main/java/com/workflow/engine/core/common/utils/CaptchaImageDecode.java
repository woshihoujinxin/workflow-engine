package com.workflow.engine.core.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CaptchaImageDecode {

    public static final Logger log = LoggerFactory.getLogger(CaptchaImageDecode.class);

    public static boolean isBackgroundColor(int colorInt) {
        Color color = new Color(colorInt);
        long data = color.getRed() + color.getGreen() + color.getBlue();
        if (data == 621 || data == 616) {
            return true;
        }
        return false;

    }

    /**
     * 图片二值化
     *
     * @param file
     * @throws IOException
     */
    private static void binaryImage(File file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file);
        int h = bufferedImage.getHeight();
        int w = bufferedImage.getWidth();

        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                if (isBackgroundColor(bufferedImage.getRGB(x, y))) {
                    bufferedImage.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    bufferedImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        bufferedImage.flush();
        ImageIO.write(bufferedImage, "png", file);
        removeLine(file);
    }

    /**
     * 去除干扰线
     *
     * @param file
     * @throws IOException
     */
    private static void removeLine(File file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file);
        int h = bufferedImage.getHeight();
        int w = bufferedImage.getWidth();
        for (int y = 0; y < h; y++) {

            for (int x = 0; x < w; x++) {
                int topLeftXIndex = x;
                int topLeftYIndex = y - 1 < 0 ? 0 : y - 1;
                if (isBlack(bufferedImage.getRGB(x, y))) {
                    Color color = new Color(bufferedImage.getRGB(topLeftXIndex, topLeftYIndex));
                    Color color2 = new Color(bufferedImage.getRGB(topLeftXIndex, y + 1 < h ? y + 1 : h - 1));
                    if ((color.getRed() + color.getGreen() + color.getBlue() == 765) && (color2.getRed() + color2.getGreen() + color2.getBlue() == 765)) {
                        bufferedImage.setRGB(x, y, Color.WHITE.getRGB());
                    } else {
                        bufferedImage.setRGB(x, y, Color.BLACK.getRGB());
                    }
                }
            }
        }

        // 矩阵打印
            /*for (int y = 0; y < h; y++) {
		        for (int x = 0; x < w; x++) {
		            if (isBlack(bufferedImage.getRGB(x, y))) {
		                System.out.print("*");
		            } else {
		                System.out.print(" ");
		            }
		        }
		        System.out.println();
		    }*/
        bufferedImage.flush();
        ImageIO.write(bufferedImage, "png", file);
    }

    private static boolean isBlack(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= 300) {
            return true;
        }
        return false;
    }

    public static String pictureToText(String url, String filePath, String fileName) {
        String captcha = "";
        try {
            HttpHelper.downImage(url, filePath, fileName);
            log.info("图片下载成功");
            File file = new File(filePath + fileName);
            if (file.exists()) {
                binaryImage(file);
                captcha = OCRUtil.recognizeText(file);
            }
        } catch (Exception e) {

//			e.printStackTrace();
//			throw new SystemException("ocr 异常", e);
        }
        return captcha;
    }

    public static void main(String[] args) {
        CaptchaImageDecode c = new CaptchaImageDecode();
        try {
            c.binaryImage(new File("D:\\captcha\\temp\\1460617852023.png"));
//			 File file=new File ("D:\\captcha\\temp\\1453447199694.png");
//			 FileUtils.moveFileToDirectory(file, new File(ConfigUtils.getStringByKey("captchaDirError")), true);
        } catch (IOException e) {
            // TODO AutoVO-generated catch block
            e.printStackTrace();
        }
    }

}
