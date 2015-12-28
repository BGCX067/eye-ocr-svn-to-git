/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common.image;

import drjava.util.ToTree;
import drjava.util.Tree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AllImages extends ImageSet implements ToTree {
  private String dir;

  public AllImages(String dir) {
    init(dir);
  }

  private void init(String dir) {
    this.dir = dir;
    List<String> names = new ArrayList<String>();
    for (File file : new File(dir).listFiles()) {
      if (file.isFile() && file.getName().toLowerCase().endsWith(".jpg")) {
        names.add(file.getName().substring(0, file.getName().length()-4));
      }
    }
    init(dir, names.toArray(new String[names.size()]));
  }

  public Tree toTree() {
    return new Tree(getClass()).add("dir", dir);
  }

  public AllImages(Tree tree) {
    init(tree.getString("dir"));
  }
}