package eye.eye01;

import drjava.util.Errors;
import drjava.util.GUIUtil;
import eyedev._18.TrainingExamplesDialog;
import prophecy.common.image.BWImage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

public class EyeGuiUtil {
  public static ActionListener actionListener(final Object object, String methodName) {
    final Method method;
    try {
      method = object.getClass().getMethod(methodName);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }

    return new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        try {
          method.invoke(object);
        } catch (Throwable t) {
          Errors.report(t);
        }
      }
    };
  }

  public static JFrame showImage(BWImage image) {
    return showImage(image, "Eye");
  }

  public static JFrame showImage(BWImage image, String title) {
    ScrollableImage scrollableImage = new ScrollableImage(image);
    JFrame frame = new JFrame(title);
    frame.setSize(500, 500);
    GUIUtil.centerOnScreen(frame);
    frame.getContentPane().add(scrollableImage);
    frame.setVisible(true);
    return frame;
  }

  public static JMenuItem makeMenuItem(String text, Object object, String methodName) {
    JMenuItem menuItem = new JMenuItem(text);
    menuItem.addActionListener(actionListener(object, methodName));
    return menuItem;
  }

  public static String shortenCode(String fullCode) {
    return shortenCode(fullCode, 10000);
  }

  public static String shortenCode(String fullCode, int maxCodeLength) {
    return fullCode.length() > maxCodeLength
      ? fullCode.substring(0, maxCodeLength) + "..." : fullCode;
  }
}
