/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.util.*;
import java.util.List;
import java.io.IOException;
import java.lang.reflect.*;
import java.awt.*;

public class TreeUtil {
  static private TreeGrammar treeGrammar = new TreeGrammar();
  private static Map<String, String> packageMigrationTable = new HashMap<String, String>();
  private static Treeifier stdTreeifier = new StdTreeifier();

  public static synchronized Tree textToTree(String s) {
    if (s == null) return null;
    try {
      return treeGrammar.parse(s);
    } catch (IOException e) {
      System.err.println("Complete source: " + s + "\n");
      throw new RuntimeException(e);
    }
    //Baum baum = textZuBaumImpl(s);
    /*if (!baum.toString().equals(s))
      System.out.println("kapott? " + s + " => " + baum.toString());*/
  }

  /*private static Baum textZuBaumImpl(String s) {
    String head = null;
    if (s.startsWith("\"")) {
      s = s.substring(1);
      int idx = s.indexOf('"');
      if (idx < 0) return new Baum(s);
      head = s.substring(0, idx);
      s = s.substring(idx+1);
    }

    int i1 = s.indexOf('('), i2 = s.indexOf(' ');
    if (i1 < 0 && i2 < 0) {
      if (head == null) head = s;
      return new Baum(head);
    }
    int idx = i1 < 0 ? i2 : (i2 < 0 ? i1 : (i1 < i2 ? i1 : i2));
    if (head == null) head = s.substring(0, idx);
    Baum baum = new Baum(head);
    s = s.substring(idx+1);
    if (s.endsWith(")")) s = s.substring(0, s.length()-1); // klammer zu weg
    if (s.trim().length() != 0) {
      String[] split = s.split(", *"); // TODO: nested trees
      for (String eq : split) {
        String[] eqSplit = eq.split("=");
        if (eqSplit.length == 2)
          baum.kinder().put(eqSplit[0], textZuBaumImpl(eqSplit[1]));
        else
          baum.kegel().add(textZuBaumImpl(eq));
      }
    }

    return baum;
  }*/

  public static boolean equal(Tree a, Tree b) {
    return matchTrees(a, b) != null;
  }

  public static Map<String, Tree> matchTrees(Tree a, Tree b) {
    if (a.nameIs("*") || b.nameIs("*")) return Collections.emptyMap();
    if (isVar(a.getName()))
      return Collections.singletonMap(a.getName(), b);
    if (isVar(b.getName()))
      return Collections.singletonMap(b.getName(), a);
    if (!a.nameIs(b.getName())) return null;

    // kegel

    int n = a.arguments().size();
    if (n != b.arguments().size()) return checkSpecialStarCase(a, b);
    Map<String, Tree> match = new TreeMap<String, Tree>();
    for (int i = 0; i < n; i++) {
      Map<String, Tree> submatch = matchTrees(a.get(i), b.get(i));
      match = mergeMatches(match, submatch);
      if (match == null)
        return checkSpecialStarCase(a, b);
    }

    // kinder

    Iterator<String> iA = a.children().keySet().iterator();
    Iterator<String> iB = b.children().keySet().iterator();
    while (iA.hasNext()) {
      if (!iB.hasNext()) return checkSpecialStarCase(a, b);
      String nameA = iA.next(), nameB = iB.next();
      if (!nameA.equals(nameB)) return checkSpecialStarCase(a, b);
      Map<String, Tree> submatch = matchTrees(a.get(nameA), b.get(nameA));
      match = mergeMatches(match, submatch);
    }
    if (iB.hasNext()) return checkSpecialStarCase(a, b);
    return match;
  }

  private static Map<String, Tree> checkSpecialStarCase(Tree a, Tree b) {
    return a.arguments().size() == 1 && a.get(0).nameIs("*")
      || b.arguments().size() == 1 && b.get(0).nameIs("*") ? Collections.<String, Tree> emptyMap() : null;
  }

  private static Map<String, Tree> mergeMatches(Map<String, Tree> m1, Map<String, Tree> m2) {
    if (m1 == null || m2 == null) return null;
    Map<String, Tree> result = new TreeMap<String, Tree>();
    for (String var : m1.keySet()) {
      if (m2.containsKey(var)) {
        throw new RuntimeException("TODO");
        //return matchTrees(m1.get(var), m2.get(var));
      } else
        result.put(var, m1.get(var));
    }

    for (String var : m2.keySet()) {
      if (!m1.containsKey(var))
        result.put(var, m2.get(var));
    }
    return result;
  }

  private static boolean isVar(String name) {
    //return name.length() != 0 && name.charAt(0) >= 'A' && name.charAt(0) <= 'Z';
    return name.length() != 0 && name.charAt(0) == '?';
  }

  public static Tree substitute(Tree tree, Map<String, Tree> map) {
    if (isVar(tree.getName())) {
      Tree subst = map.get(tree.getName());
      if (subst != null)
        return subst;
    }

    Tree result = new Tree(tree.getName());
    for (Tree kind : tree.arguments()) {
      result.add(substitute(kind, map));
    }

    return result;
  }

  public static double doppeltGenauerWert(Tree tree, String kindName, double vorgabe) {
    Tree kind = tree.get(kindName);
    return kind == null ? vorgabe : Double.parseDouble(kind.getName());
  }

  public static Tree wert(double x) {
    return new Tree(String.valueOf(x));
  }

  public static Tree objectToTree(Object a, Treeifier mainTreeifier) {
    return mainTreeifier.objectToTree(a);
  }

  public static Tree objectToTree(Object a) {
    if (a == null)
      return null;

    Tree tree = stdObjectToTree(a);
    if (tree != null)
      return tree;

    Treeifier treeifier = Attachments.get(a, Treeifier.class);
    if (treeifier != null)
      return treeifier.objectToTree(a);

    throw new RuntimeException(a.getClass() + " not treeifiable");
  }

  public static Tree stdObjectToTree(Object a) {
    return stdObjectToTree(a, stdTreeifier);
  }

  public static Tree stdObjectToTree(Object a, Treeifier mainTreeifier) {
    if (a == null)
      return null;
    if (a instanceof ToTree)
      return ((ToTree) a).toTree();
    if (a instanceof ToTree2)
      return ((ToTree2) a).toTree(mainTreeifier);
    if (a instanceof String)
      return Tree.quote((String) a);
    if (a instanceof Collection)
      return listToTree((Collection) a, mainTreeifier);
    if (a instanceof Map)
      return mapToTree((Map) a);
    if (a instanceof Number)
      return new Tree(a.getClass()).add(new Tree(a.toString()));
    if (a instanceof Boolean)
      return new Tree(((Boolean) a));

    // BEAUTIFICATION OPPORTUNITY: manage these more nicely
    if (a instanceof Rectangle) {
      Rectangle r = (Rectangle) a;
      return new Tree(a.getClass())
        .add("x", r.x).add("y", r.y).add("width", r.width).add("height", r.height);
    }

    if (a instanceof Date)
      return new Tree(a.getClass()).add("time", ((Date) a).getTime());

    return null;
  }

  public static <A> A treeToObject(Class<A> targetClass, Tree tree) {
    Object o = treeToObject(tree);
    if (o == null) return null;
    if (targetClass.isInstance(o)) return (A) o;
    Constructor<A> constructor = findConstructor(targetClass, o.getClass());
    if (constructor != null)
      try {
        return constructor.newInstance(o);
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    throw new RuntimeException("Can't convert " + o.getClass().getName() + " to " + targetClass.getName());
  }

  public static <A> A treeToObject(Tree tree) {
    if (tree == null) return null;
    if (tree.getName().startsWith("\""))
      return (A) tree.unquote();
    if (tree.nameIs("#"))
      return (A) treeToList(tree);
    if (tree.nameIs("java.lang.Integer"))
      return (A) new Integer(tree.getInt(0));
    if (tree.nameIs("java.lang.Long"))
      return (A) new Long(tree.getLong(0));
    if (tree.nameIs("true")) return (A) Boolean.TRUE;
    if (tree.nameIs("false")) return (A) Boolean.FALSE;
    if (tree.nameIs("null"))
      return null;

    Object o;
    Class c = null;
    try {
      String className = tree.getName();
      className = migrateClassName(className);
      if (className == null)
        throw new NullPointerException();
      c = Class.forName(className);
      Constructor constructor = findTreeConstructor(c);
      if (constructor != null)
        o = constructor.newInstance(tree);
      else {
        o = c.newInstance();
        if (o instanceof FromTree)
          ((FromTree) o).fromTree(tree);
        else if (o instanceof TreeToObject) {
          o = ((TreeToObject) o).treeToObject(tree);
        } else if (o instanceof Map)
          treeToMap(tree, (Map) o);
        else if (o instanceof Date)
          ((Date) o).setTime(tree.getLong("time"));
        /*else
          throw new RuntimeException(c + " neither has a (Tree) constructor nor implements FromTree");*/
      }
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } catch (InstantiationException e) {
      try {
        if (c != null) c.getConstructor();
      } catch (NoSuchMethodException e2) {
        throw new RuntimeException(c + " has neither a (Tree) nor a () constructor");
      }
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
    return (A) o;
  }

  public static void treeToMap(Tree tree, Map map) {
    for (Tree t : tree.arguments()) {
      map.put(t.getObject(0), t.getObject(1));
    }
  }

  private static <A> Constructor<A> findTreeConstructor(Class<A> c) {
    for (Constructor constructor : c.getConstructors()) {
      if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0] == Tree.class)
        return constructor;
    }
    return null;
  }

  private static <A> Constructor<A> findConstructor(Class<A> c, Class arg) {
    for (Constructor constructor : c.getConstructors()) {
      if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0].isAssignableFrom(arg))
        return constructor;
    }
    return null;
  }

  private static String migrateClassName(String name) {
    int idx = name.lastIndexOf('.');
    if (idx < 0) return name;
    String pkg = name.substring(0, idx);
    String pkg2 = packageMigrationTable.get(pkg);
    if (pkg2 != null)
      return pkg2 + name.substring(idx);
    return name;
  }

  public static void registerPackageMigration(String oldPkg, String newPkg) {
    packageMigrationTable.put(oldPkg, newPkg);
  }

  public static List treeToList(Tree tree) {
    ArrayList<Object> list = new ArrayList<Object>();
    if (tree != null)
      for (Tree k : tree.arguments()) {
        list.add(treeToObject(k));
      }
    return list;
  }

  public static Tree listToTree(Collection list) {
    return listToTree(list, stdTreeifier);
  }

  public static Tree listToTree(Collection list, Treeifier mainTreeifier) {
    Tree tree = new Tree();
    for (Object o : list) {
      tree.add(objectToTree(o, mainTreeifier));
    }
    return tree;
  }

  public static Tree mapToTree(Map map) {
    Tree tree = new Tree(map.getClass());
    for (Map.Entry entry : ((Map<Object, Object>) map).entrySet()) {
      tree.add(new Tree("#").add(entry.getKey()).add(entry.getValue()));
    }
    return tree;
  }

  private static class StdTreeifier implements Treeifier {
    public Tree objectToTree(Object object) {
      return TreeUtil.objectToTree(object);
    }
  }

  public static Object stringToObject(String s) {
    return treeToObject(Tree.parse(s));
  }
}