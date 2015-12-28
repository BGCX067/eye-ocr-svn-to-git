/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common;

import drjava.util.StringUtil;
import drjava.util.TextView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogView extends TextView {
  public void logText(String text) {
    setText(getText() + wrap(nlToBrWithIndent(StringUtil.escapeHtml(text))));
  }

  public void logHtml(String html) {
    setText(getText() + html);
  }

  protected void fillPopupMenu(JPopupMenu popup) {
    super.fillPopupMenu(popup);

    JMenuItem miClear = new JMenuItem("Clear");
    miClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setText("");
      }
    });
    popup.add(miClear);

  }

  /*public void setAntiAliasOn() {
    SwingUtilities2.AATextInfo aaTextInfo = SwingUtilities2.AATextInfo.getAATextInfo(true);
    label.putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, aaTextInfo);
  }*/
}