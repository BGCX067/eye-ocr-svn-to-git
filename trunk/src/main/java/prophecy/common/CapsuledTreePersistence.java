/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common;

import drjava.util.Tree;
import drjava.util.TreePersistence;

public class CapsuledTreePersistence implements TreePersistence {
  private TreePersistence basePersistence;

  public CapsuledTreePersistence(TreePersistence basePersistence) {
    this.basePersistence = basePersistence;
  }

  public Tree load() {
    return extract(basePersistence.load());
  }

  public Tree load(Tree defaultTree) {
    Tree tree = basePersistence.load();
    return tree == null ? defaultTree : extract(tree);
  }

  public void store(Tree tree) {
    basePersistence.store(capsule(tree));
  }

  private Tree capsule(Tree tree) {
    return new Tree().add("data", tree);
  }

  private Tree extract(Tree tree) {
    if (tree == null) return null;
    return tree.get("data");
  }
}