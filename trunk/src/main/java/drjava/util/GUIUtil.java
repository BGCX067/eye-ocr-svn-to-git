/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUIUtil {
  private static int mainFrames;
  private static boolean mainApp;
  private static CustomGUI customGUI = new CustomGUI();

  public static void centerOnScreen(Window window) {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    window.setLocation((screen.width - window.getWidth()) / 2, (screen.height - window.getHeight()) / 2);
  }

  public static JFrame showMainFrame(JFrame frame) {
    mainApp |= MainApp.setMainApp("GUIUtil.showMainFrame");
    if (mainApp) {
      frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          if (mainFrames > 0) {
            if (--mainFrames == 0)
              System.exit(0);
          }
        }
      });
      ++mainFrames;
    }
    showFrame(frame);
    return frame;
  }

  public static void revalidateAndRepaint(JComponent component) {
    if (component.isShowing()) {
      component.revalidate();
      component.repaint();
    }
  }

  public static JFrame showFrame(JFrame frame) {
    getCustomGUI().prepareForDisplay(frame);
    frame.setVisible(true);
    return frame;
  }

  public static JPanel withTitle(JComponent titleComponent, JComponent c) {
    titleComponent.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Button.borderColor")));
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.add(BorderLayout.NORTH, titleComponent);
    panel.add(BorderLayout.CENTER, c);
    return panel;
  }

  public static JComponent withTitle(String title, JComponent c) {
    return withTitle(makeInnerTitlePanel(title), c);
  }

  public static JPanel makeInnerTitlePanel(String title) {
    Line titlePanel = new Line();
    titlePanel.add(new JLabel(title));
    return titlePanel;
  }

  public static JLabel centeredLabel(String text) {
    return new JLabel("<html><center>" + text + "</center></html>", JLabel.CENTER);
  }

  public static JComponent withLabel(String text, JComponent component) {
    return withLabel(new JLabel(text), component);
  }

  public static JComponent withLabel(JComponent label, JComponent component) {
    JPanel panel = new JPanel(new LetterLayout("LCC").setSpacing(10));
    panel.add("L", label);
    panel.add("C", component);
    return panel;
  }

  public static JButton newButton(String text, ActionListener actionListener) {
    JButton button = new JButton(text);
    button.addActionListener(actionListener);
    return button;
  }

  public static void setMinimumWidth(JComponent component, int width) {
    component.setMinimumSize(new Dimension(width, component.getMinimumSize().height));
  }

  public static JComponent setMinimumHeight(JComponent component, int height) {
    component.setMinimumSize(new Dimension(component.getMinimumSize().width, height));
    return component;
  }

  public static JTextArea fixTabKeys(JTextArea textArea) {
    /*
    Action nextFocusAction = new AbstractAction("Move Focus Forwards") {
        public void actionPerformed(ActionEvent evt) {
            ((Component)evt.getSource()).transferFocus();
        }
    };
    Action prevFocusAction = new AbstractAction("Move Focus Backwards") {
        public void actionPerformed(ActionEvent evt) {
            ((Component)evt.getSource()).transferFocusBackward();
        }
    };
    textArea.getActionMap().put(nextFocusAction.getValue(Action.NAME), nextFocusAction);
    textArea.getActionMap().put(prevFocusAction.getValue(Action.NAME), prevFocusAction);
     */

    /*textArea.addKeyListener(new KeyListener() {
      public void keyTyped(KeyEvent e) {
        if (e.getKeyCode() == e.VK_TAB) {
          System.out.println("Tab pressed");
        }
      }

      public void keyPressed(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
      }

      public void keyReleased(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
      }
    });*/
    //TODO
    return textArea;
  }

  public static CustomGUI getCustomGUI() {
    return customGUI;
  }

  public static void setCustomGUI(CustomGUI customGUI) {
    GUIUtil.customGUI = customGUI;
  }

  public static JComponent withInset(JComponent component) {
    return withInset(component, 10);
  }

  public static JComponent withInset(JComponent component, int inset) {
    JPanel panel = new JPanel(new LetterLayout("X").setBorder(inset));
    panel.add("X", component);
    return panel;
  }

  public static JFrame showMainFrame(String title, Component component) {
    JFrame frame = new JFrame(title);
    frame.getContentPane().add(component);
    frame.setSize(600, 600);
    return showMainFrame(frame);
  }

  public static JFrame showFrame(String title, Component component) {
    JFrame frame = new JFrame(title);
    frame.getContentPane().add(component);
    frame.setSize(600, 600);
    return showFrame(frame);
  }

  public static JFrame showMainFrame(String title, Component component, Dimension size) {
    JFrame frame = new JFrame(title);
    frame.setSize(size);
    frame.getContentPane().add(component);
    return showMainFrame(frame);
  }
}
