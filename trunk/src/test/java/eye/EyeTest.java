/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eye;

import com.ocr.OCR;
import drjava.util.Tree;
import eye.eye01.EyeGuiUtil;
import eye.eye03.*;
import eyedev._01.*;
import eyedev._18.WithFratboySegmenter;
import eyedev._21.ImageInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.junit.Test;
import prophecy.common.gui.RightAlignedLine;
import prophecy.common.image.BWImage;

/**
 *
 * @author mupeng
 */
public class EyeTest {

//    @Test
    public void test() throws IOException {

        try {
            String link = "http://price.360buyimg.com/gp508983,2.png";
            URL url = new URL(link);
            BufferedImage srcImage = ImageIO.read(url.openStream());
            File file = getFile("memory/recognizers/alpha.eye");
            System.out.println(file.getAbsoluteFile());
            RecognizerOnDisk rod = new RecognizerOnDisk(file, "alpha");
            RecognizerInfo recognizerInfo = rod.getRecognizerInfo();
            Tree code = recognizerInfo.getCode();
//            System.out.println("Recognizer: " + EyeGuiUtil.shortenCode(code.toString()));

            ImageReader recognizer = OCRUtil.makeImageReader(code);
//            String txt = OCRUtil.makeImageReader(code).readImage(new BWImage(srcImage));
            if (recognizerInfo.getInputType() == RecognizerInputType.character) {
                recognizer = new WithFratboySegmenter(recognizer);


            }
            recognizer.setCollectDebugInfo(true);
            
            String txt = recognizer.readImage(new BWImage(srcImage));

            System.out.println(txt);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    private File getFile(String fileName){
        File f = new File(fileName);
        try{
            if(f.exists()){
                System.out.println(f.getAbsoluteFile());
                
            }else{
                
                f = new File(EyeTest.class.getClassLoader().getResource(fileName).getPath());
                
            }
                
        }catch(Exception e){
            
        }
        return f;
    }
    
//     private void loadProperty(String file, Map<String, String> prop) throws IOException {
//        InputStream is = null;
//        File f = new File(file);
//        try {
//            if (f.exists()) {
//                is = new FileInputStream(f);
//                log("load from file: " + f.getAbsolutePath());
//            } else {
//                is = Configure.class.getClassLoader().getResourceAsStream(file);
//                log("load from resource: " + Configure.class.getClassLoader().getResource(file));
//            }
//            loadProperty(is, prop);
//        } finally {
//            if (null != is) {
//                is.close();
//            }
//        }
//    }

    @Test
    public void testEye() {



        try {
//            File f = new File("E:\\opensource\\eye-alpha-10\\examples\\donnie_small.jpg");
//            image = ImageIO.read(f);
            String link = "http://price.360buyimg.com/gp508983,2.png";
            URL url = new URL(link);
            OCR ocr = OCR.getInstance();
            String ret = ocr.recognize(url);
            System.out.println(ret);
        } catch (Exception e) {
        }

    }
}
