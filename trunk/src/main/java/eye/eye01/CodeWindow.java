package eye.eye01;

import drjava.util.GUIUtil;

import javax.swing.*;

public class CodeWindow {
  public static void show(String text, String title, String innerTitle) {
    JFrame frame = new JFrame(title);
    frame.setSize(600, 300);
    GUIUtil.centerOnScreen(frame);

    JTextArea textArea = new JTextArea(text);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    JComponent component = new JScrollPane(textArea);
    if (innerTitle != null && innerTitle.length() != 0)
      component = GUIUtil.withTitle(innerTitle, component);
    frame.getContentPane().add(GUIUtil.withInset(component, 10));

    frame.setVisible(true);
  }
}
