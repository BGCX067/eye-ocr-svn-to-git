package eye.eye02;

import drjava.util.Errors;
import drjava.util.Tree;
import eye.eye03.RecognizerInfo;
import eye.eye03.RecognizerInputType;
import eye.eye03.RecognizerOnDisk;
import eye.eye03.Recognizers;

import javax.swing.*;
import java.awt.*;

public class EyeDialogs {
  /** returns name of saved recognizer (or null if save was cancelled) */
  public static String saveRecognizer(Window ownerWindow, Recognizers recognizers, String code,
                                    String recognizerFontName, RecognizerInputType inputType,
                                    String proposedName, boolean showMessageBox) {
    try {
      if (proposedName == null && recognizerFontName != null && recognizerFontName.length() != 0)
        proposedName = recognizerFontName + " recognizer (user-made)";
      if (proposedName == null || proposedName.length() == 0)
        proposedName = "New recognizer";

      String name;
      do {
        name = JOptionPane.showInputDialog(ownerWindow, "Please enter a name for the new recognizer", proposedName);
        if (name == null) return null;
        name = name.trim();
      } while (name.length() == 0);

      RecognizerOnDisk existingRecognizer = recognizers.findByName(name);
      if (existingRecognizer != null) {
        if (JOptionPane.showConfirmDialog(ownerWindow,
          "A recognizer with this name already exists. Overwrite?",
          "Confirm overwrite",
          JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
          return null;
      }

      RecognizerInfo info = new RecognizerInfo(Tree.parse(code), name);
      info.setPreferredFontName(recognizerFontName);
      info.setInputType(inputType);
      recognizers.save(info);

      if (existingRecognizer != null)
        recognizers.delete(existingRecognizer);
      if (showMessageBox)
        JOptionPane.showMessageDialog(ownerWindow, "Recognizer saved.");
      return name;
    } catch (Throwable e) {
      Errors.report(e);
      return null;
    }
  }
}
