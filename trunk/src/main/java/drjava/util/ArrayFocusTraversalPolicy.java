/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.awt.*;
import java.util.List;
import java.util.Arrays;

public class ArrayFocusTraversalPolicy extends FocusTraversalPolicy {
  private List<Component> components;

  public ArrayFocusTraversalPolicy(Component... components) {
    this.components = Arrays.asList(components);
  }

  public Component getComponentAfter(Container container, Component component) {
    int idx = components.indexOf(component);
    return idx >= 0 && !components.isEmpty() ? components.get((idx+1) % components.size()) : null;
  }

  public Component getComponentBefore(Container container, Component component) {
    int idx = components.indexOf(component);
    return idx >= 0 && !components.isEmpty() ? components.get((idx+components.size()-1) % components.size()) : null;
  }

  public Component getFirstComponent(Container container) {
    return !components.isEmpty() ? components.get(0) : null;
  }

  public Component getLastComponent(Container container) {
    return !components.isEmpty() ? components.get(components.size()-1) : null;
  }

  public Component getDefaultComponent(Container container) {
    return getFirstComponent(container);
  }
}