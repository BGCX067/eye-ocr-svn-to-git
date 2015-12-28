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

public class PersistentTree extends Tree {
  private TreePersistence persistence;

  public PersistentTree(TreePersistence persistence) {
    this(persistence, true);
  }

  private PersistentTree(TreePersistence persistence, boolean load) {
    this.persistence = persistence;
    if (load)
      fromTree(persistence.load(new Tree()));
  }

  /** save this tree through persistence object.
   *  If this is a subtree, the whole tree is saved. */
  public void save() {
    persistence.store(this);
  }

  /** if no subtree with key "name" exists, an empty tree is created and filed under "name". It can
   *  then be modified as required (including its name).
   */
  public PersistentTree subTree(String name) {
    Tree tree = get(name);
    if (tree instanceof PersistentTree)
      return (PersistentTree) tree;
    else {
      // convert subtree to PersistentTree / create subtree
      PersistentTree pTree = new PersistentTree(new TreePersistence() {
        public Tree load() {
          throw new RuntimeException("not implemented");
        }

        public Tree load(Tree defaultTree) {
          throw new RuntimeException("not implemented");
        }

        public void store(Tree tree) {
          save();
        }
      }, false);
      if (tree != null)
        pTree.fromTree(tree); // copy contents
      add(name, pTree); // replace
      return pTree;
    }
  }

}