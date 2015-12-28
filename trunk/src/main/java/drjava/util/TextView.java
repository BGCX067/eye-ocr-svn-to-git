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
import java.util.regex.Pattern;

public class TextView extends JScrollPane {
  protected JLabel label = new JLabel();
  private String text = "";
  private Color bgColor = Color.white;

  public TextView() {
    label = new JLabel();
    label.setOpaque(false);
    //label.setFont(new Font("Courier", )); TODO
    label.setBackground(Color.white);
    setViewportView(wrapLabel(label));
    setTextBackground(Color.white);

    new PopupMenuHelper() {
      protected void fillMenu(JPopupMenu menu, Point point) {
        fillPopupMenu(menu);
      }
    }.install(label);

    label.getParent().addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        rewrap();
      }
    });
  }

  private void rewrap() {
    String pattern = "xwrap width=\\d+";
    String replacement = "xwrap width=" + getWrapWidth();
    String text = Pattern.compile(pattern).matcher(label.getText()).replaceAll(replacement);
    label.setText(text);
  }

  protected void fillPopupMenu(JPopupMenu popup) {
    JMenuItem miCopy = new JMenuItem("Copy");
    miCopy.setToolTipText("Copy contents to clipboard");
    miCopy.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        copy();
      }
    });

    popup.add(miCopy);
  }

  public TextView(String text) {
    this();
    setText(text);
  }

  public TextView(String text, boolean wrap) {
    this();
    setText(wrap ? wrap(text) : text);
  }

  public void setText(String text, boolean wrap) {
    setText(wrap ? wrap(text) : text);
  }

  public void setText(String html) {
    text = html;
    label.setText(makeHtml(html));
    //setAlignmentY(JLabel.TOP_ALIGNMENT);
    label.setVerticalAlignment(JLabel.TOP);
    //setVerticalTextPosition(TOP);
  }

  protected String makeHtml(String html) {
    String col = hex8(bgColor.getRed()) + hex8(bgColor.getBlue()) + hex8(bgColor.getGreen());
    return "<html><body bgcolor=#" + col + ">" + html + "</body></html>";
  }

  private String hex8(int x) {
    String s = Integer.toHexString(x).toUpperCase();
    if (s.length() < 2) s = "0" + s;
    return s;
  }

  int getWrapWidth() {
    return label.getParent().getWidth();
  }

  protected String wrap(String html) {
    return "<table cellspacing=0 cellpadding=0 xwrap width=" + getWrapWidth() + "><tr><td>" + html + "</td></tr></table>";
  }

  public void showNothing() {
    setText(" ");
  }

  private void scrollDownNow() {
    getViewport().scrollRectToVisible(new Rectangle(0, 1000000, 1, 1));
  }

  public void scrollDown() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        scrollDownNow();
      }
    });
  }

  public boolean isScrolledDown(int tolerance) {
    int max = getVerticalScrollBar().getMaximum(),
      vis = getVerticalScrollBar().getVisibleAmount(),
      pos = getVerticalScrollBar().getValue();
    boolean result = pos + vis + tolerance >= max;
    //System.out.println("max=" + max + ", pos+vis+" + tolerance + "=" + (pos+vis+tolerance) + ", result=" + result);
    return result;
  }

  public String getText() {
    return text;
  }

  public void copy() {
    SwingUtil.copyTextToClipboard(getText());
  }

  public static String nlToBrWithIndent(String s) {
    return s.replace("\t", "  ").replace(" ", "&nbsp;").replace("\n", "<br>");
  }

  public void setInset(int inset) {
    label.setBorder(BorderFactory.createEmptyBorder(inset, inset, inset, inset));
  }

  public void setTextFont(Font font) {
    label.setFont(font);
  }

  public JLabel getLabel() {
    return label;
  }

  protected JComponent wrapLabel(JLabel label) {
    return label;
  }

  public void setContents(String text) {
    setText(text);
  }

  /** only works properly if label text is changed after this */
  public void setTextBackground(Color color) {
    //label.setBackground(color);
    bgColor = color;
    getViewport().setBackground(color);
  }
}
