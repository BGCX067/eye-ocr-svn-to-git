/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocr;

import drjava.util.Tree;
import eye.eye01.EyeGuiUtil;
import eye.eye03.*;
import eyedev._01.*;
import eyedev._18.WithFratboySegmenter;
import eyedev._21.ImageInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import prophecy.common.image.BWImage;

/**
 *
 * @author mupeng
 */
public class OCR {

    private Logger l = Logger.getLogger(getClass());
    private static ImageReader recognizer = null;
    private static OCR ocr = new OCR();

    private OCR() {
        try {
            File file = getFile("memory/recognizers/alpha.eye");
            System.out.println(file.getAbsoluteFile());
            RecognizerOnDisk rod = new RecognizerOnDisk(file, "alpha");
            RecognizerInfo recognizerInfo = rod.getRecognizerInfo();
            Tree code = recognizerInfo.getCode();
//            System.out.println("Recognizer: " + EyeGuiUtil.shortenCode(code.toString()));

            recognizer = OCRUtil.makeImageReader(code);
            if (recognizerInfo.getInputType() == RecognizerInputType.character) {
                recognizer = new WithFratboySegmenter(recognizer);


            }
            recognizer.setCollectDebugInfo(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static OCR getInstance() {
        return ocr;
    }

    private File getFile(String fileName) {
        File f = new File(fileName);
        try {
            if (f.exists()) {
                l.debug(f.getAbsoluteFile());

            } else {

                f = new File(OCR.class.getClassLoader().getResource(fileName).getPath());
                l.debug(f.getAbsoluteFile());
            }

        } catch (Exception e) {
        }
        return f;
    }

    /**
     *
     * @param url
     * @return
     */
    public String recognize(URL url) {
        l.debug("Starting recognition");
        String ret = null;


        try {
            BufferedImage image = ImageIO.read(url.openStream());
            if (image == null) {
                l.debug("exception null :" + url);
                ret = "";
            } else {
                InputImage inputImage = new InputImage(new BWImage(image));

                if (inputImage == null) {
                    l.debug("InputImage is null");
                    ret = "";
                } else {

                    RecognizedText rt = recognizer.extendedReadImage(inputImage);
                    ret = rt.text;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
