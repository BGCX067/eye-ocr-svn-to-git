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
import javax.swing.text.View;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public class LetterLayout implements LayoutManager {
  private String[] lines;
  private Map<String, Component> map = new TreeMap<String, Component>();
  private RC[] rows;
  private RC[] cols;
  private Cell[][] cells;
  private int spacingX = 10, spacingY = 10;
  private int insetTop, insetBottom, insetLeft, insetRight;
  private int template;
  private boolean formWideLeftSide, formWideRightSide;

  private static final int STALACTITE = 1, LEFT_ALIGNED_ROW = 2, CENTERED_ROW = 3, FORM = 4, RIGHT_ALIGNED_ROW = 5;

  public void setLeftBorder(int border) {
    insetLeft = border;
  }

  public void setRightBorder(int border) {
    insetRight = border;
  }

  public static JComponent withBorder(JComponent component, int border) {
    JPanel panel = new JPanel(new LetterLayout("C").setBorder(border));
    panel.add("C", component);
    return panel;
  }

  static class DummyComponent extends JComponent {
  }

  /**
   * info about one matrix cell
   */
  static class Cell {
    boolean aux; // part of a larger cell, but not top-left corner
    int minWidth, minHeight;
    Component component;
    int colspan, rowspan;
    double weightX, weightY;
  }

  /**
   * info about one matrix row / column
   */
  static class RC {
    int min;
    double weightSum;
    int start;
    int minEnd;
  }

  private LetterLayout(int template) {
    this.template = template;
  }

  public LetterLayout(String... lines) {
    this.lines = lines;
  }

  public void removeLayoutComponent(Component component) {
    map.values().remove(component);
  }

  public void layoutContainer(Container container) {
    prepareLayout(container);

    // do layout

    Insets insets = getInsets(container);
    for (int r = 0; r < rows.length; r++) {
      for (int i = 0; i < cols.length;) {
        Cell cell = cells[i][r];
        if (cell.aux)
          ++i;
        else {
          if (cell.component != null) {
            int x1 = cols[i].start;
            int y1 = rows[r].start;
            int x2 = i + cell.colspan < cols.length ? cols[i + cell.colspan].start - spacingX : container.getWidth() - insets.right;
            int y2 = r + cell.rowspan < rows.length ? rows[r + cell.rowspan].start - spacingY : container.getHeight() - insets.bottom;
            //System.out.println("Layouting ("+i+","+r+"): "+x1+" "+y1+" "+x2+" "+y2);
            cell.component.setBounds(x1, y1, x2 - x1, y2 - y1);
          }
          i += cells[i][r].colspan;
        }
      }
    }
  }

  private void prepareLayout(Container container) {
    applyTemplate(container);

    int numRows = lines.length, numCols = lines[0].length();
    for (int i = 1; i < numRows; i++) if (lines[i].length() != numCols)
      throw new IllegalArgumentException("Lines have varying length");
    cells = new Cell[numCols][numRows];
    rows = new RC[numRows];
    cols = new RC[numCols];

    for (int r = 0; r < numRows; r++) rows[r] = new RC();
    for (int i = 0; i < numCols; i++) cols[i] = new RC();
    for (int r = 0; r < numRows; r++) for (int i = 0; i < numCols; i++) cells[i][r] = new Cell();

    // define cells

    for (int r = 0; r < numRows; r++) {
      String line = lines[r];
      for (int i = 0; i < numCols;) {
        Cell cell = cells[i][r];
        if (cell.aux) {
          ++i;
          continue;
        }
        char ch = line.charAt(i);
        int iNext = i;
        do ++iNext; while (iNext < numCols && ch == line.charAt(iNext));
        int rNext = r;
        do ++rNext; while (rNext < numRows && ch == lines[rNext].charAt(i));

        cell.weightX = numCols == 1 || iNext > i + 1 ? 1.0 : 0.0;
        cell.weightY = numRows == 1 || rNext > r + 1 ? 1.0 : 0.0;

        Component c = map.get(String.valueOf(ch));
        cell.component = c;
        if (c != null) {
          cell.minWidth = c.getMinimumSize().width + spacingX;
          cell.minHeight = getMinimumHeight(c) + spacingY;
        }
        cell.colspan = iNext - i;
        cell.rowspan = rNext - r;

        if (cell.colspan == 1)
          cols[i].min = Math.max(cols[i].min, cell.minWidth);
        if (cell.rowspan == 1)
          rows[r].min = Math.max(rows[r].min, cell.minHeight);

        for (int r2 = r; r2 < rNext; r2++)
          for (int i2 = i; i2 < iNext; i2++)
            if (r2 != r || i2 != i)
              cells[i2][r2].aux = true;

        i = iNext;
      }
    }

    // determine minStarts, weightSums

    while (true) {
      for (int i = 0; i < numCols; i++) {
        int minStart = i == 0 ? 0 : cols[i - 1].minEnd;
        double weightStart = i == 0 ? 0.0 : cols[i - 1].weightSum;
        for (int r = 0; r < numRows; r++) {
          Cell cell = cells[i][r];
          if (!cell.aux) {
            RC rc = cols[i + cell.colspan - 1];
            rc.minEnd = Math.max(rc.minEnd, minStart + cell.minWidth);
            rc.weightSum = Math.max(rc.weightSum, weightStart + cell.weightX);
          }
        }
      }

      for (int r = 0; r < numRows; r++) {
        int minStart = r == 0 ? 0 : rows[r - 1].minEnd;
        double weightStart = r == 0 ? 0.0 : rows[r - 1].weightSum;
        for (int i = 0; i < numCols; i++) {
          Cell cell = cells[i][r];
          if (!cell.aux) {
            RC rc = rows[r + cell.rowspan - 1];
            rc.minEnd = Math.max(rc.minEnd, minStart + cell.minHeight);
            rc.weightSum = Math.max(rc.weightSum, weightStart + cell.weightY);
          }
        }
      }

      if (allWeightsZero(cols)) {
        for (int r = 0; r < numRows; r++)
          for (int i = 0; i < numCols; i++)
            cells[i][r].weightX = 1.0;
        continue;
      }

      if (allWeightsZero(rows)) {
        for (int r = 0; r < numRows; r++)
          for (int i = 0; i < numCols; i++)
            cells[i][r].weightY = 1.0;
        continue;
      }

      break;
    }

    // determine row, col starts

    Insets insets = getInsets(container);
    determineStarts(cols, insets.left, container.getWidth() - insets.left - insets.right + spacingX, spacingX);
    determineStarts(rows, insets.top, container.getHeight() - insets.top - insets.bottom + spacingY, spacingY);
  }

  private boolean allWeightsZero(RC[] rcs) {
    for (int i = 0; i < rcs.length; i++)
      if (rcs[i].weightSum != 0.0)
        return false;
    return true;
  }

  private static int getMinimumHeight(Component c) {
    if (c instanceof JTextArea) {
      return (int) ((JTextArea) c).getUI().getRootView((JTextArea) c).getPreferredSpan(View.Y_AXIS);
    }
    return c.getMinimumSize().height;
  }

  private void applyTemplate(Container container) {
    if (template == STALACTITE) {
      Component[] components = container.getComponents();

      lines = new String[components.length + 2];
      map.clear();
      for (int i = 0; i < components.length; i++) {
        String s = String.valueOf((char) ('A' + i));
        map.put(s, components[i]);
        lines[i] = s;
      }
      lines[components.length] = lines[components.length + 1] = " ";
    } else if (template == FORM) {
      /* old method of calculating numRows:
      int numRows = 0;
      for (String key : map.keySet()) {
        if (key.length() == 1)
          numRows = Math.max(numRows, Character.toLowerCase(key.charAt(0))-'a');
      }*/
      Component[] components = container.getComponents();
      int numRows = components.length/2;

      lines = new String[numRows+2];
      map.clear();
      for (int row = 0; row < numRows; row++) {
        String upper = String.valueOf((char) ('A' + row));
        String lower = upper.toLowerCase();
        Component rightComponent = components[row * 2 + 1];
        if (rightComponent instanceof DummyComponent)
          upper = lower;
        lines[row] = (formWideLeftSide ? lower + lower : lower) + (formWideRightSide ? upper + upper : upper);
        map.put(lower, components[row*2]);
        if (!(rightComponent instanceof DummyComponent))
          map.put(upper, rightComponent);
      }
      lines[numRows] = lines[numRows+1] = (formWideLeftSide ? "  " : " ") + (formWideRightSide ? "  " : " ");
    } else if (template == LEFT_ALIGNED_ROW) {
      lines = new String[] { makeSingleRow(container) + "ZZ" };
    } else if (template == CENTERED_ROW) {
      lines = new String[] { "YY" + makeSingleRow(container) + "ZZ" };
    } else if (template == RIGHT_ALIGNED_ROW) {
      lines = new String[] { "ZZ" + makeSingleRow(container) };
    }
  }

  private String makeSingleRow(Container container) {
    Component[] components = container.getComponents();
    StringBuffer buf = new StringBuffer();
    map.clear();
    for (int i = 0; i < components.length; i++) {
      String s = String.valueOf((char) ('A' + i));
      map.put(s, components[i]);
      buf.append(s);
    }
    return buf.toString();
  }

  private static void determineStarts(RC[] rcs, int start, int totalSize, int spacing) {
    int minTotal = rcs[rcs.length - 1].minEnd;
    double weightSum = rcs[rcs.length - 1].weightSum;
    //System.out.println("totalSize="+totalSize+",minTotal="+minTotal+",weightSum="+weightSum);
    int spare = (int) ((totalSize - minTotal) / (weightSum == 0.0 ? 1.0 : weightSum));
    int x = start, minSum = 0;
    double prevWeightSum = 0.0;
    for (int i = 0; i < rcs.length; i++) {
      int width = rcs[i].minEnd - minSum + (int) ((rcs[i].weightSum - prevWeightSum) * spare) - spacing;
      //System.out.println("i="+i+",prevws="+prevWeightSum+",ws="+rcs[i].weightSum+",min="+rcs[i].min+",width="+width);
      rcs[i].start = x;
      x += width + spacing;
      prevWeightSum = rcs[i].weightSum;
      minSum = rcs[i].minEnd;
    }
  }

  public void addLayoutComponent(String s, Component component) {
    map.put(s, component);
  }

  public Dimension minimumLayoutSize(Container container) {
    prepareLayout(container);
    Insets insets = getInsets(container);
    Dimension result = new Dimension(
      insets.left + cols[cols.length - 1].minEnd + insets.right - spacingX,
      insets.top + rows[rows.length - 1].minEnd + insets.bottom - spacingY);
    return result;
  }

  private Insets getInsets(Container container) {
    Insets insets = container.getInsets();
    return new Insets(insets.top + insetTop,
      insets.left + insetLeft,
      insets.bottom + insetBottom,
      insets.right + insetRight);
  }

  public Dimension preferredLayoutSize(Container container) {
    return minimumLayoutSize(container);
  }

  public LetterLayout setSpacing(int x, int y) {
    spacingX = x;
    spacingY = y;
    return this;
  }

  public LetterLayout setSpacing(int spacing) {
    return setSpacing(spacing, spacing);
  }

  public LetterLayout setBorder(int top, int left, int bottom, int right) {
    insetTop = top;
    insetLeft = left;
    insetBottom = bottom;
    insetRight = right;
    return this;
  }

  public LetterLayout setBorder(int inset) {
    return setBorder(inset, inset, inset, inset);
  }

  public LetterLayout setTopBorder(int inset) {
    insetTop = inset;
    return this;
  }

  /**
   * layout components from top to bottom; add components without letters!
   */
  public static LetterLayout stalactite() {
    return new LetterLayout(STALACTITE);
  }

  /**
   * layout components from left to right; add components without letters!
   */
  public static LetterLayout leftAlignedRow() {
    return new LetterLayout(LEFT_ALIGNED_ROW);
  }

  public static LetterLayout leftAlignedRow(int spacing) {
    return leftAlignedRow().setSpacing(spacing);
  }

  /**
   * layout components from left to right, center in container; add components without letters!
   */
  public static LetterLayout centeredRow() {
    return new LetterLayout(CENTERED_ROW);
  }

  public static LetterLayout rightAlignedRow() {
    return new LetterLayout(RIGHT_ALIGNED_ROW);
  }

  public static JPanel rightAlignedRowPanel(JComponent... components) {
    return makePanel(new LetterLayout(RIGHT_ALIGNED_ROW), components);
  }

  private static JPanel makePanel(LetterLayout letterLayout, JComponent[] components) {
    JPanel panel = new JPanel(letterLayout);
    for (JComponent component : components) {
      panel.add(component);
    }
    return panel;
  }

  /**
   * layout components from top to bottom; two components per row
   */
  public static LetterLayout form() {
    LetterLayout letterLayout = new LetterLayout(FORM);
    letterLayout.formWideLeftSide = true;
    letterLayout.formWideRightSide = true;
    return letterLayout;
  }

  /**
   * layout components from top to bottom; two components per row
   * left column is small, right column is wide
   */
  public static LetterLayout formWideRightSide() {
    LetterLayout letterLayout = new LetterLayout(FORM);
    letterLayout.formWideRightSide = true;
    return letterLayout;
  }

  /** for double stalactite */
  /*public static String leftColumnLetter(int row) {
    return String.valueOf((char) ('a' + row));
  }*/

  /** for double stalactite */
  /*public static String rightColumnLetter(int row) {
    return String.valueOf((char) ('A' + row));
  }*/

  public static Component getDummyComponent() {
    return new DummyComponent();
  }

}