package eye.eye03;

import drjava.util.LetterLayout;

import javax.swing.*;

public class MiddleComponent extends JPanel {
  public MiddleComponent(JComponent component) {
    setLayout(new LetterLayout(" ", " ", "C", " ", " "));
    add("C", component);
  }
}
