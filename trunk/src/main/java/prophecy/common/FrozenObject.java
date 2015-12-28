/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common;

import drjava.util.*;

import java.util.ArrayList;
import java.util.List;

public class FrozenObject implements ToTree {
  private String className;
  private String data;
  private List<Tree> notes = new ArrayList<Tree>();

  public FrozenObject(Class clazz, String data) {
    className = clazz.getName();
    this.data = data;
  }

  public FrozenObject(String className, String data) {
    this.className = className;
    this.data = data;
  }

  public FrozenObject(Class clazz, Tree data) {
    className = clazz.getName();
    this.data = data.toString();
  }

  public FrozenObject(String className, Tree data) {
    this.className = className;
    this.data = data.toString();
  }

  public String getClassName() {
    return className;
  }

  public String getData() {
    return data;
  }

  public List<Tree> getNotes() {
    return notes;
  }

  public void addNote(Tree note) {
    notes.add(note);
  }

  public Result<Object> unfreeze() {
    try {
      Class c = Class.forName(className);
      Object o = c.newInstance();
      if (o instanceof Unfreezer)
        o = ((Unfreezer) o).unfreeze(data);
      return new Result<Object>(o);
    } catch (Throwable e) {
      return new NoResult<Object>(e);
    }
  }

  public Tree toTree() {
    Tree tree = new Tree()
      .addUnquotedString(className)
      .addUnquotedString(data);
    if (notes.size() != 0)
      tree.add("notes", new Tree().addAll(notes));
    return tree;
  }

  /** the result tree is no longer interpretable by TreeUtil.treeToObject - but you might not
   *  need that anyway. This saves a few bytes compared to the regular toTree()  */
  public Tree toShortenedTree() {
    if (className.endsWith("$Unfreeze") && notes.size() == 0) {
      String baseClassName = className.substring(0, className.length() - "$Unfreeze".length());
      return new Tree(baseClassName).addUnquotedString(data);
    } else {
      Tree tree = toTree();
      tree.setName("");
      return tree;
    }
  }

  public FrozenObject(String treeText) {
    this(TreeUtil.textToTree(treeText));
  }

  public FrozenObject(Tree tree) {
    if (tree.namelessChildrenCount() == 1) {
      // short version
      className = tree.getName() + "$Unfreeze";
      data = tree.getUnquotedString(0);
    } else {
      // long version
      className = tree.getUnquotedString(0);
      data = tree.getUnquotedString(1);
      Tree notes = tree.get("notes");
      if (notes != null)
        this.notes.addAll(notes.arguments());
    }
  }

  public String toString() {
    /*return "FrozenObject[" + className + ", " + data + "]";*/
    return toTree().toString();
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FrozenObject that = (FrozenObject) o;

    if (className != null ? !className.equals(that.className) : that.className != null)
      return false;
    return !(data != null ? !data.equals(that.data) : that.data != null);

  }

  public int hashCode() {
    int result;
    result = (className != null ? className.hashCode() : 0);
    result = 31 * result + (data != null ? data.hashCode() : 0);
    return result;
  }
}