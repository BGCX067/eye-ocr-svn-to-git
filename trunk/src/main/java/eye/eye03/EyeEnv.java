package eye.eye03;

import drjava.util.Errors;
import prophecy.common.Prophecy;

import javax.swing.*;
import java.util.Locale;

public class EyeEnv {
  public static void init() {
    Locale.setDefault(Locale.ENGLISH);
    Errors.setPopup(true);

    /*if (!tryLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"))
      tryLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");*/
    Prophecy.systemLookAndFeel();
  }

  private static boolean tryLookAndFeel(String className) {
    try {
      UIManager.setLookAndFeel(className);
      return true;
    } catch (Throwable e) {
      //e.printStackTrace();
      return false;
    }
  }
}
