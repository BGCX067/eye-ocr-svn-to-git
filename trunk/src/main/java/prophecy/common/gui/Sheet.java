/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common.gui;

import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import drjava.util.Errors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A sheet is a very practical Swing container. It is basically a panel that grows from top
 * to bottom as you add components to it. It automatically scrolls when needed.
 *
 * This can be very useful for arranging text and form elements like you would on a web page.
 */
public class Sheet {
  protected JPanel panel;
  private LetterLayout sheetLayout;
  private String title;
  private Dimension size;
  private JScrollPane scrollPane;

  public Sheet() {
    this("");
  }

  public Sheet(String title) {
    this.title = title;

    sheetLayout = makeLayout();
    panel = new JPanel(sheetLayout);
  }

  protected LetterLayout makeLayout() {
    return LetterLayout.stalactite().setSpacing(10).setBorder(10);
  }

  public JPanel getPanel() {
    return panel;
  }

  public LetterLayout getSheetLayout() {
    return sheetLayout;
  }

  public void addSpacer() {
    addComponent(new JPanel());
  }

  public JButton addButton(String text) {
    JButton button = new JButton(text);
    addComponent(button);
    return button;
  }

  public JButton addButton(String text, ActionListener actionListener) {
    JButton button = addButton(text);
    button.addActionListener(actionListener);
    return button;
  }

  public void addButton(String text, Class mainClass) {
    addComponent(startButton(text, mainClass));
  }

  public JLabel addLabel(String text) {
    JLabel label = GUIUtil.centeredLabel(text);
    addComponent(label);
    return label;
  }

  public JLabel addLeftAlignedLabel(String text) {
    JLabel label = new JLabel(text);
    label.setToolTipText(text);
    addComponent(label);
    return label;
  }

  public void addComponent(JComponent component) {
    panel.add(component);
    panel.revalidate();
    panel.repaint();
  }

  protected static JButton startButton(String text, final Class mainClass) {
    JButton button = new JButton(text);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        try {
          mainClass.getMethod("main", new String[0].getClass()).invoke(null, new Object[] {new String[0]});
        } catch (Throwable e) {
          Errors.add(e);
        }
      }
    });
    return button;
  }

  public void addHeading(String text) {
    addSpacer();
    addLabel(text);
  }

  public JFrame asFrame() {
    JFrame frame = new JFrame(title);
    frame.getContentPane().add(new JScrollPane(panel));
    if (size != null)
      frame.setSize(size);
    else
      frame.pack();
    return frame;
  }

  public JDialog asDialog(Frame owner) {
    JDialog dialog = new JDialog(owner, title);
    dialog.getContentPane().add(new JScrollPane(panel));
    if (size != null)
      dialog.setSize(size);
    else
      dialog.pack();
    GUIUtil.centerOnScreen(dialog);
    return dialog;
  }

  public void setSize(int width, int height) {
    size = new Dimension(width, height);
  }

  public JLabel addBlueHeading(String text) {
    JLabel label = GUIUtil.centeredLabel(text);
    label.setForeground(Color.white);
    label.setBackground(Color.blue);
    label.setOpaque(true);
    addComponent(label);
    return label;
  }

  public JScrollPane getScrollPane() {
    if (scrollPane == null)
      scrollPane = new JScrollPane(getPanel());
    return scrollPane;
  }

  public JComponent asComponent() {
    return getScrollPane();
  }

  public Snapshot snapshot() {
    return new Snapshot();
  }

  public String getTitle() {
    return title;
  }

  public JFrame asFrame(String title) {
    JFrame frame = asFrame();
    frame.setTitle(title);
    return frame;
  }

  public class Snapshot {
    private int componentCount;

    public Snapshot() {
      componentCount = getPanel().getComponentCount();
    }

    public void restore() {
      Sheet.this.restore(componentCount);
    }

    public String toString() {
      return String.valueOf(componentCount);
    }
  }

  private void restore(int componentCount) {
    while (getPanel().getComponentCount() > componentCount)
      getPanel().remove(getPanel().getComponentCount()-1);
    getPanel().revalidate();
  }

  public void addPair(JComponent left, JComponent right) {
    GridLayout layout = new GridLayout(1, 2);
    layout.setHgap(10);
    JPanel panel = new JPanel(layout);
    panel.add(left == null ? new JPanel() : left);
    panel.add(right == null ? new JPanel() : right);
    addComponent(panel);
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public JScrollPane addScrollable(JComponent component, int height) {
    JScrollPane scrollPane = new JScrollPane(component);
    GUIUtil.setMinimumHeight(scrollPane, height);
    addComponent(scrollPane);
    return scrollPane;
  }

  public JScrollPane addScrollable(JComponent component, int width, int height) {
    JScrollPane scrollPane = new JScrollPane(component);
    scrollPane.setMinimumSize(new Dimension(width, height));
    addComponent(scrollPane);
    return scrollPane;
  }

  public JFrame showFrame() {
    return GUIUtil.showFrame(asFrame());
  }

  public void clear() {
    restore(0);
  }

  public void disposeFrame() {
    Window window = SwingUtilities.windowForComponent(panel);
    if (window != null)
      window.dispose();
  }
}