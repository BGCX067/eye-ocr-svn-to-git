/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import static drjava.util.TreeUtil.objectToTree;
import static drjava.util.TreeUtil.treeToObject;

import java.io.*;

public class FileTreePersistence implements TreePersistence {
  private String fileName;
  private boolean multiLine = true;

  public FileTreePersistence(String fileName) {
    this.fileName = fileName;
  }

  public FileTreePersistence(File file) {
    this(file.getPath());
  }

  public FileTreePersistence(File file, boolean multiLine) {
    this(file.getPath());
    this.multiLine = multiLine;
  }

  public Tree load() {
    try {
      String text = FileUtil.loadTextFile(fileName);
      return text == null ? null : TreeUtil.textToTree(text);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Tree load(Tree defaultTree) {
    Tree tree = load();
    return tree != null ? tree : defaultTree;
  }

  public void store(Tree tree) {
    try {
      FileUtil.saveTextFile(fileName, multiLine ? tree.toMultiLineString() : tree.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void store(Object object) {
    store(objectToTree(object));
  }

  public Object loadObject() {
    return treeToObject(load());
  }

  public Object loadObject(Object defaultObject) {
    Object object = loadObject();
    return object != null ? object : defaultObject;
  }

  public String toString() {
    return fileName;
  }
}