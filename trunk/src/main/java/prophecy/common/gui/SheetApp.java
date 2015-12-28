/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common.gui;

import drjava.util.LetterLayout;
import prophecy.common.gui.Sheet;
import prophecy.common.Application;

import javax.swing.*;
import java.awt.event.ActionListener;

public class SheetApp extends Application {
  protected Sheet sheet;

  public SheetApp(String title) {
    sheet = new Sheet(title);
  }

  public void showFrame() {
    JFrame frame = sheet.asFrame();
    showFinalFrame(frame);
  }

  public void addSpacer() {
    sheet.addSpacer();
  }

  public JButton addButton(String text) {
    return sheet.addButton(text);
  }

  public JButton addButton(String text, ActionListener actionListener) {
    return sheet.addButton(text, actionListener);
  }

  public void addButton(String text, Class mainClass) {
    sheet.addButton(text, mainClass);
  }

  public JLabel addLabel(String text) {
    return sheet.addLabel(text);
  }

  public void addComponent(JComponent component) {
    sheet.addComponent(component);
  }

  public void addHeading(String text) {
    sheet.addHeading(text);
  }

  public JLabel addBlueHeading(String text) {
    return sheet.addBlueHeading(text);
  }

  public LetterLayout getSheetLayout() {
    return sheet.getSheetLayout();
  }

  public void setSize(int width, int height) {
    sheet.setSize(width, height);
  }

  public JPanel getPanel() {
    return sheet.getPanel();
  }

  public JScrollPane addScrollable(JComponent component, int height) {
    return sheet.addScrollable(component, height);
  }

  public JScrollPane addScrollable(JComponent component, int width, int height) {
    return sheet.addScrollable(component, width, height);
  }
}