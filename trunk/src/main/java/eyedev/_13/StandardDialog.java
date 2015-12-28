package eyedev._13;

import drjava.util.GUIUtil;
import drjava.util.LetterLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class StandardDialog extends JFrame {
  protected JPanel buttons, mainPanel;

  public StandardDialog(String title) {
    this();
    setTitle(title);
  }

  public StandardDialog() {
    setSize(400, 400);
    GUIUtil.centerOnScreen(this);

    buttons = new JPanel(LetterLayout.stalactite());

    mainPanel = new JPanel(new BorderLayout());

    getContentPane().setLayout(new LetterLayout("MMB").setBorder(10));
    getContentPane().add("M", mainPanel);
    getContentPane().add("B", buttons);
  }

  public void addButton(JButton button) {
    buttons.add(button);
  }

  public JButton addButton(String text, ActionListener actionListener) {
    JButton button = new JButton(text);
    if (actionListener != null)
      button.addActionListener(actionListener);
    buttons.add(button);
    return button;
  }

  public void centerAndShow() {
    GUIUtil.centerOnScreen(this);
    setVisible(true);
  }

  public JPanel getButtons() {
    return buttons;
  }

  public JPanel getMainPanel() {
    return mainPanel;
  }
}
