/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.Map;

public class TreeTest extends TestCase {
  public void testMatchTrees() {
    assertEquals(0, TreeUtil.matchTrees(new Tree("a"), new Tree("a")).size());
    assertEquals(null, TreeUtil.matchTrees(new Tree("a"), new Tree("b")));
    assertEquals(0, TreeUtil.matchTrees(new Tree("*"), new Tree("b")).size());
  }

  public void testKinder() {
    assertNull(TreeUtil.matchTrees(
      new Tree("a"),
      new Tree("a").add("c", new Tree("d"))));
    assertNull(TreeUtil.matchTrees(
      new Tree("a").add("c", new Tree("e")),
      new Tree("a").add("c", new Tree("d"))));
    assertNull(TreeUtil.matchTrees(
      new Tree("a").add("b", new Tree("e")),
      new Tree("a").add("c", new Tree("d"))));
    assertNotNull(TreeUtil.matchTrees(
      new Tree("a").add("c", new Tree("d")),
      new Tree("a").add("c", new Tree("d"))));
  }

  public void testStar() {
    assertNotNull(TreeUtil.matchTrees(
      new Tree("a", new Tree("*")),
      new Tree("a", new Tree("b")).add("c", new Tree("d"))));
  }

  public void testMatchTreesWithVars() {
    Map<String, Tree> map = TreeUtil.matchTrees(new Tree("?X"), new Tree("a"));
    assertEquals(1, map.size());
    assertEquals(new Tree("a"), map.get("?X"));
  }

  public void testSubstitute() {
    assertEquals(new Tree("b", new Tree("a")),
      TreeUtil.substitute(new Tree("b", new Tree("?A")), Collections.singletonMap("?A", new Tree("a"))));
  }

  public void testTextToTree1() {
    assertEquals(new Tree("a", new Tree("b", new Tree("c"), new Tree("d")), new Tree("e")),
      TreeUtil.textToTree("a(b(c, d), e)"));
  }

  public void testTextToTree2() {
    // das soll nu mal ein token sein
    assertEquals(new Tree("a-1"), TreeUtil.textToTree("a-1"));
  }

  // passt so nicht zum Rest der Grammatik
  /*public void testTextToTree3() {
    assertEquals(new Baum("").hinzu("a", new Baum("b", new Baum("c"))), Baumfreund.textToTree("(a=(b=c))"));
  }*/

  public void testTextToTreeAndBack3() {
    Tree tree = new Tree("#").add("a", new Tree("#").add("b", new Tree("c")));
    assertEquals(tree, TreeUtil.textToTree("#(a=#(b=c))"));
    assertEquals("#(a=#(b=c))", tree.toString());
    assertEquals("#(\r\n  a=#(\r\n    b=c))", tree.toMultiLineString());
  }

  public void testTextToTree4() {
    assertEquals(new Tree("\"").add("a b", new Tree("c")),
      TreeUtil.textToTree("\"\\\"\"(\"a b\"=c)"));
  }

  public void testTextToTree5() {
    assertEquals(new Tree("a").add(new Tree("b")).add(new Tree("c")),
      TreeUtil.textToTree("\"a\"(\"b\", \"c\")"));
  }

  public void testTextToTree6() {
    assertEquals(new Tree("a").add(new Tree("b")),
      TreeUtil.textToTree("\"a\" \"b\""));
  }

  public void testToString() {
    assertEquals("\"\\\"\"(\"a b\"=c)", new Tree("\"").add("a b", new Tree("c")).toString());
  }

  public void testSimplifiedToStringBug() {
    Tree tree = new Tree("a").add(new Tree("b").add(new Tree("c")));
    assertEquals("a(b c)", tree.toString());
  }

  public void testQualifiedNameToString() {
    assertEquals("ab.cde", new Tree("ab.cde").toString());
  }

  public void testQualifiedNameFromString() {
    assertEquals(new Tree("ab.cde"), Tree.parse("ab.cde"));
  }

  public void testIntToString() {
    assertEquals("25", new Tree("25").toString());
  }

  public void testIntFromString() {
    assertEquals(new Tree("25"), TreeUtil.textToTree("25"));
  }

  public void testIntPlusArg() {
    Tree tree = new Tree("25").add(new Tree("a"));
    assertEquals("25 a", tree.toString());
    assertEquals(tree, TreeUtil.textToTree("25 a"));
  }

  /*public void testQuoteUnquote() {
    String text = "\"tag\\\"";
    assertEquals(text, Baum.unquote(Baumfreund.textToTree((Baum.quote(text).toString()))));
  }*/

  public void testQuoteLinebreak() {
    String s = Tree.quote("a\nb").toString();
    assertEquals("\"\\\"a\\\\nb\\\"\"", s);
    assertEquals("parsed", "\"a\\nb\"", TreeUtil.textToTree(s).getName());
    assertEquals("unquoted", "a\nb", TreeUtil.textToTree(s).unquote());
  }

  public void testCRLF() {
    assertEquals("\"\\r\\n\"", new Tree("\r\n").toString());
    assertEquals(new Tree("\r\n"), TreeUtil.textToTree("\"\\r\\n\""));
  }

  public void testAttachedTreeifier() {
    final Object object = new Object();
    Attachments.add(object, new Treeifier() {
      public Tree objectToTree(Object o) {
        return o == object ? new Tree("ok") : null;
      }
    });
    assertEquals(new Tree("ok"), TreeUtil.objectToTree(object));
  }

  public void testGetInt() {
    assertEquals(55, new Tree().add(55).getInt(0));
  }
}