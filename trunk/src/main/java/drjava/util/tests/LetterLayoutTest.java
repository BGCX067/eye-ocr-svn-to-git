/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util.tests;

import drjava.util.XTestCase;
import drjava.util.LetterLayout;

import javax.swing.*;
import java.awt.*;

public class LetterLayoutTest extends XTestCase {
  private JLabel cA, cB, cC, cD;
  private JPanel panel;
  private LetterLayout layout;

  public void setUp() {
    cA = new JLabel();
    cA.setMinimumSize(new Dimension(20, 20));
    cB = new JLabel();
    cB.setMinimumSize(new Dimension(20, 20));
    cC = new JLabel();
    cC.setMinimumSize(new Dimension(20, 20));
    cD = new JLabel();
    cD.setMinimumSize(new Dimension(20, 20));
  }

  public void testOneComponent() {
    layout = new LetterLayout("A");
    panel = new JPanel(layout);
    panel.add("A", cA);
    panel.setSize(100, 100);
    assertEquals("minimumLayoutSize", new Dimension(20, 20), layout.minimumLayoutSize(panel));
    panel.doLayout();
    assertEquals("A", new Rectangle(0, 0, 100, 100), cA.getBounds());
  }

  public void testAAB() {
    layout = new LetterLayout("AAB");
    panel = new JPanel(layout);
    panel.add("A", cA);
    panel.add("B", cB);
    panel.setSize(100, 100);
    assertEquals("minimumLayoutSize", new Dimension(40, 20), layout.minimumLayoutSize(panel));
    panel.doLayout();
    assertEquals("A", new Rectangle(0, 0, 80, 100), cA.getBounds());
    assertEquals("B", new Rectangle(80, 0, 20, 100), cB.getBounds());
  }

  public void testAAB_AAB() {
    panel = new JPanel(new LetterLayout("AAB", "AAB"));
    panel.add("A", cA);
    panel.add("B", cB);
    panel.setSize(100, 100);
    panel.doLayout();
    assertEquals("A", new Rectangle(0, 0, 80, 100), cA.getBounds());
    assertEquals("B", new Rectangle(80, 0, 20, 100), cB.getBounds());
  }

  public void testA_A_B() {
    panel = new JPanel(new LetterLayout("A", "A", "B"));
    panel.add("A", cA);
    panel.add("B", cB);
    panel.setSize(100, 100);
    panel.doLayout();
    assertEquals("A", new Rectangle(0, 0, 100, 80), cA.getBounds());
    assertEquals("B", new Rectangle(0, 80, 100, 20), cB.getBounds());
  }

  public void testAAB_CCB_CCB() {
    panel = new JPanel(new LetterLayout("AAB", "CCB", "CCB"));
    panel.add("A", cA);
    panel.add("B", cB);
    panel.add("C", cC);
    panel.setSize(100, 100);
    panel.doLayout();
    assertEquals("A", new Rectangle(0, 0, 80, 20), cA.getBounds());
    assertEquals("B", new Rectangle(80, 0, 20, 100), cB.getBounds());
    assertEquals("C", new Rectangle(0, 20, 80, 80), cC.getBounds());
  }

  public void testEmptySpace() {
    panel = new JPanel(new LetterLayout("AAB"));
    panel.add("B", cB);
    panel.setSize(100, 100);
    panel.doLayout();
    assertEquals("B", new Rectangle(80, 0, 20, 100), cB.getBounds());
  }

  public void testSpacing() {
    layout = new LetterLayout("AAB", "AAB", "CCC");
    layout.setSpacing(10, 5);
    panel = new JPanel(layout);
    panel.add("A", cA);
    panel.add("B", cB);
    panel.add("C", cC);
    panel.setSize(100, 100);
    panel.doLayout();
    System.out.println("A="+cA.getBounds());
    System.out.println("B="+cB.getBounds());
    System.out.println("C="+cC.getBounds());
    assertEquals("A", new Rectangle(0, 0, 70, 75), cA.getBounds());
    assertEquals("B", new Rectangle(80, 0, 20, 75), cB.getBounds());
    assertEquals("C", new Rectangle(0, 80, 100, 20), cC.getBounds());
    assertEquals(new Dimension(50, 45), panel.getMinimumSize());
  }

  public void testInsets() {
    layout = new LetterLayout("A");
    panel = new JPanel(layout);
    panel.setBorder(BorderFactory.createEmptyBorder(3, 1, 2, 9));
    panel.add("A", cA);
    panel.setSize(100, 100);
    assertEquals("minimumLayoutSize", new Dimension(30, 25), layout.minimumLayoutSize(panel));
    panel.doLayout();
    assertEquals("A", new Rectangle(1, 3, 90, 95), cA.getBounds());
  }

  public void testSetBorder() {
    layout = new LetterLayout("A");
    layout.setBorder(3, 1, 2, 9);
    panel = new JPanel(layout);
    panel.add("A", cA);
    panel.setSize(100, 100);
    assertEquals("minimumLayoutSize", new Dimension(30, 25), layout.minimumLayoutSize(panel));
    panel.doLayout();
    assertEquals("A", new Rectangle(1, 3, 90, 95), cA.getBounds());
  }

  public void testStalactite() {
    layout = LetterLayout.stalactite();
    panel = new JPanel(layout);
    panel.add(cA);
    panel.add(cB);
    panel.setSize(100, 100);
    panel.doLayout();
    assertEquals("A", new Rectangle(0, 0, 100, 20), cA.getBounds());
    assertEquals("B", new Rectangle(0, 20, 100, 20), cB.getBounds());
  }

  public void testLeftAlignedRow() {
    layout = LetterLayout.leftAlignedRow();
    panel = new JPanel(layout);
    panel.add(cA);
    panel.add(cB);
    panel.setSize(100, 100);
    panel.doLayout();
    assertEquals("A", new Rectangle(0, 0, 20, 100), cA.getBounds());
    assertEquals("B", new Rectangle(20, 0, 20, 100), cB.getBounds());
  }

  public void testCenteredRow() {
    layout = LetterLayout.centeredRow();
    panel = new JPanel(layout);
    panel.add(cA);
    panel.add(cB);
    panel.setSize(100, 100);
    panel.doLayout();
    assertEquals("A", new Rectangle(30, 0, 20, 100), cA.getBounds());
    assertEquals("B", new Rectangle(50, 0, 20, 100), cB.getBounds());
  }

  public void testCentering() {
    layout = new LetterLayout("  A  ");
    panel = new JPanel(layout);
    panel.add("A", cA);
    panel.setSize(100, 100);
    panel.doLayout();
    assertEquals("A", new Rectangle(40, 0, 20, 100), cA.getBounds());
  }

  public void testCenteringAtBottom() {
    layout = new LetterLayout(
      "BBBBB",
      "BBBBB",
      "  A  ");
    panel = new JPanel(layout);
    panel.add("A", cA);
    panel.add("B", cB);
    panel.setSize(100, 100);
    panel.doLayout();
    assertEquals("A", new Rectangle(40, 80, 20, 20), cA.getBounds());
    assertEquals("B", new Rectangle(0, 0, 100, 80), cB.getBounds());
  }

  public void testAB() {
    layout = new LetterLayout("AB");
    panel = new JPanel(layout);
    panel.add("A", cA);
    panel.add("B", cB);
    panel.setSize(100, 100);
    assertEquals("minimumLayoutSize", new Dimension(40, 20), layout.minimumLayoutSize(panel));
    panel.doLayout();
    assertEquals("A", new Rectangle(0, 0, 50, 100), cA.getBounds());
    assertEquals("B", new Rectangle(50, 0, 50, 100), cB.getBounds());
  }

  /*public void testAB_AB_CB() {
    cB.setMinimumSize(new Dimension(20, 100));
    panel = new JPanel(new LetterLayout("AB", "AB", "CB"));
    panel.add("A", cA);
    panel.add("B", cB);
    panel.add("C", cC);
    panel.setSize(100, 100);
    panel.doLayout();
    assertEquals("A", new Rectangle(0, 0, 50, 80), cA.getBounds());
  } TODO: fix */

  public void testForm() {
    layout = LetterLayout.form();
    panel = new JPanel(layout);
    panel.add(cA);
    panel.add(cB);
    panel.add(cC);
    panel.add(cD);
    panel.setSize(100, 100);
    panel.doLayout();
    assertEquals("A", new Rectangle(0, 0, 50, 20), cA.getBounds());
    assertEquals("B", new Rectangle(50, 0, 50, 20), cB.getBounds());
    assertEquals("C", new Rectangle(0, 20, 50, 20), cC.getBounds());
    assertEquals("D", new Rectangle(50, 20, 50, 20), cD.getBounds());
  }

  public void testFormWideRightSide() {
    layout = LetterLayout.formWideRightSide();
    panel = new JPanel(layout);
    panel.add(cA);
    panel.add(cB);
    panel.setSize(100, 100);
    panel.doLayout();
    assertEquals("A", new Rectangle(0, 0, 20, 20), cA.getBounds());
    assertEquals("B", new Rectangle(20, 0, 80, 20), cB.getBounds());
  }
}