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
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/** a JSplitPane that scales evenly when resized */
public class ScalingSplitPane extends JSplitPane implements AncestorListener {
  private double dividerLocationToSet = Double.NaN;
  private Runnable afterResizing;

  public ScalingSplitPane() {
    init();
  }

  public ScalingSplitPane(int newOrientation) {
    super(newOrientation);
    init();
  }

  public ScalingSplitPane(int newOrientation, boolean newContinuousLayout) {
    super(newOrientation, newContinuousLayout);
    init();
  }

  public ScalingSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
    super(newOrientation, newLeftComponent, newRightComponent);
    init();
  }

  public ScalingSplitPane(int newOrientation, boolean newContinuousLayout, Component newLeftComponent, Component newRightComponent) {
    super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
    init();
  }

  public ScalingSplitPane(int orientation, double dividerLocation) {
    this(orientation);
    setDividerLocationLater(dividerLocation);
  }

  public ScalingSplitPane(double dividerLocation) {
    setDividerLocationLater(dividerLocation);
  }

  private void init() {
    addContainerListener(new ContainerListener() {
      public void componentAdded(ContainerEvent e) {
        Component child = e.getChild();
        if (child instanceof JComponent)
          ((JComponent) child).addAncestorListener(ScalingSplitPane.this);
      }

      public void componentRemoved(ContainerEvent e) {
        Component child = e.getChild();
        if (child instanceof JComponent)
          ((JComponent) child).removeAncestorListener(ScalingSplitPane.this);
      }
    });

    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        //System.out.println("ScalingSplitPane.componentResized: size="+getSize()+",loc="+dividerLocationToSet);
        setDividerLocation();
      }
    });
  }

  private void setDividerLocation() {
    if (Double.isNaN(dividerLocationToSet)) return;
    //System.out.println("ScalingSplitPane.setDividerLocation: size="+getSize()+",loc="+dividerLocationToSet);
    setDividerLocation(dividerLocationToSet);
    if (afterResizing != null) {
      afterResizing.run();
      afterResizing = null;
    }
    dividerLocationToSet = Double.NaN;
  }

  public void ancestorAdded(AncestorEvent event) {
  }

  public void ancestorRemoved(AncestorEvent event) {
  }

  public void ancestorMoved(AncestorEvent event) {
    setResizeWeight(getRelativeDividerLocation());
  }

  public double getRelativeDividerLocation() {
    double min = 0;
    double max = (getOrientation() == VERTICAL_SPLIT ? getHeight() : getWidth()) - getDividerSize();
    return max <= min ? 0.5 : Math.max(0.0, Math.min(1.0, (getDividerLocation() - min) / (max-min)));
  }

  public void setDividerLocationLater(final double location) {
    dividerLocationToSet = location;
  }

  public void setAfterResizing(Runnable afterResizing) {
    this.afterResizing = afterResizing;
  }
}