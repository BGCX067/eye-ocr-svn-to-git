package eye.eye05;

import drjava.util.GUIUtil;
import prophecy.common.gui.Sheet;

import javax.swing.*;

public class StartMenu {
  public static void main(String[] args) {
    new StartMenu();
  }

  StartMenu() {
    JFrame frame = new JFrame("Eye Alpha 5");
    frame.setSize(300, 500);

    Sheet sheet = new Sheet();

    sheet.addPair(new JButton("Instant recognition"), new JButton("Interactive recognition"));
    sheet.addPair(new JButton("Learn a font"), new JButton("Manage recognizers"));

    frame.getContentPane().add(sheet.getScrollPane());

    GUIUtil.centerOnScreen(frame);
    GUIUtil.showMainFrame(frame);
  }
}
