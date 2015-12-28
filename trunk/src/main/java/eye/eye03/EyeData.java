package eye.eye03;

import drjava.util.Tree;
import eyedev._01.OCRUtil;
import eyedev._18.TrainingExamples;
import eyedev._21.ImageInfoDB;
import prophecy.common.ClassData;
import prophecy.common.StringProperty;

import java.awt.*;

public class EyeData {
  public ClassData classData;
  public Recognizers recognizers;
  public TrainingExamples trainingExamples;
  public ImageInfoDB imageInfoDB;

  public EyeData() {
    classData = ClassData.get(this);
    recognizers = new Recognizers();
    trainingExamples = new TrainingExamples();
    imageInfoDB = new ImageInfoDB();
  }

  public void setLastImagePath(String path) {
    classData.setUnquotedString("lastImagePath", path);
    classData.save();
  }

  public String getLastImagePath() {
    return classData.getUnquotedString("lastImagePath");
  }

  public void setDefaultRecognizerName(String s) {
    classData.setUnquotedString("defaultRecognizerName", s);
    classData.save();
  }

  public String getDefaultRecognizerName() {
    return classData.getUnquotedString("defaultRecognizerName");
  }

  public Rectangle getMainWindowBounds() {
    Tree t = classData.get("mainWindowBounds");
    if (t == null) return null;
    return new Rectangle(t.getInt(0), t.getInt(1), t.getInt(2), t.getInt(3));
  }

  public void setMainWindowBounds(Rectangle r, boolean maximized) {
    classData.setBool("mainWindowMaximized", maximized);
    if (!maximized)
      classData.set("mainWindowBounds", OCRUtil.rectToTree(r));
    classData.save();
  }

  public boolean getMainWindowMaximized() {
    return classData.getBool("mainWindowMaximized", false);
  }

  public boolean getAutoRecognize() {
    return classData.getBool("autoRecognize", true);
  }

  public void setAutoRecognize(boolean autoRecognize) {
    classData.setBool("autoRecognize", autoRecognize);
    classData.save();
  }

  public void setMainWindowSplit(float dividerLocation) {
    //System.out.println("dividerLocation: " + dividerLocation);
    classData.setFloat("mainWindowSplit", dividerLocation);
    classData.save();
  }

  public float getMainWindowSplit() {
    return classData.getFloat("mainWindowSplit", 0.7f);
  }

  public StringProperty welcomeDialogShownForVersion() {
    return new StringProperty(classData, "welcomeDialogShownForVersion", "");
  }
}
