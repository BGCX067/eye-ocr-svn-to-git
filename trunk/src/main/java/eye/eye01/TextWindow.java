package eye.eye01;

import drjava.util.GUIUtil;

import javax.swing.*;

public class TextWindow {
  public static void show(String text, String title) {
    JFrame frame = new JFrame(title);
    frame.setSize(600, 300);
    GUIUtil.centerOnScreen(frame);

    JTextArea textArea = new JTextArea(text);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    frame.getContentPane().add(GUIUtil.withInset(new JScrollPane(textArea), 10));

    frame.setVisible(true);
  }
}
