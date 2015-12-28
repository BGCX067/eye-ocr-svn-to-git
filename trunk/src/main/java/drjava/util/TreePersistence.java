/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import drjava.util.Tree;

import java.io.IOException;

public interface TreePersistence {
  Tree load();
  Tree load(Tree defaultTree);
  void store(Tree tree);
}