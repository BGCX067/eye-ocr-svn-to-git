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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tree implements Displayable, FromTree, ToTree {
  private String name;
  private List<Tree> args = new ArrayList<Tree>();
  private SortedMap<String, Tree> children = new TreeMap<String, Tree>();

  public Tree(String name) {
    /*if (WirreKonstanten.SAFE_MODE)
      checkName(name);*/
    this.name = name;
  }

  public Tree(double value) {
    this.name = String.valueOf(value);
  }

  public Tree(long value) {
    this.name = String.valueOf(value);
  }

  public Tree(boolean b) {
    this.name = b ? "true" : "false";
  }

  public Tree() {
    name = "#";
  }

  public Tree(String name, Tree... args) {
    this.name = name;
    for (Tree tree : args) { add(tree); }
  }

  public Tree(Class c) {
    name = c.getName();
  }

  public String getName() {
    return name;
  }

  public List<Tree> arguments() {
    return args;
  }

  public Map<String, Tree> children() {
    return children;
  }

  public Tree add(String name, Tree kind) {
    children.put(name, kind);
    return this;
  }

  public Tree add(String name, String wert) {
    if (wert == null) return this;
    children.put(name, new Tree(wert));
    return this;
  }

  public Tree add(Tree kind) {
    args.add(kind);
    return this;
  }

  public String toString() {
    if (children.isEmpty() && args.isEmpty())
      return maybeQuoteId(getName());
    if (fitForSimplifiedRepr())
      return maybeQuoteId(getName()) + " " + args.get(0);
    if (name.equals("<") && args.size() == 2)
      return get(0) + "<" + get(1);

    StringBuilder builder = new StringBuilder(maybeQuoteId(getName()));

    builder.append("(");
    boolean first = true;

    for (Tree kind : args) {
      if (first)
        first = false;
      else
        builder.append(", ");
      builder.append(kind);
    }

    for (String name : children.keySet()) {
      if (first)
        first = false;
      else
        builder.append(", ");
      builder.append(maybeQuoteId(name)).append("=").append(children.get(name));
    }

    builder.append(")");
    return builder.toString();
  }

  /** can this tree be rendered as "a b" instead of "a(b)"? */
  private boolean fitForSimplifiedRepr() {
    return children.size() == 0 && args.size() == 1 && ! args.get(0).fitForSimplifiedRepr();
  }

  private String maybeQuoteId(String name) {
    if (!TreeGrammar.isIdentifier(name) && !TreeGrammar.isInteger(name))
      return quoteString(name);
    else
      return name;
  }

  public Tree get(int i) {
    return i < args.size() ? args.get(i) : null;
  }

  public Set<String> names() {
    return children.keySet();
  }

  public Tree get(String name) {
    return children.get(name);
  }

  public Tree get(String name, Tree defaultValue) {
    Tree tree = children.get(name);
    return tree != null ? tree : defaultValue;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Tree tree = (Tree) o;

    if (!args.equals(tree.args)) return false;
    if (!children.equals(tree.children)) return false;
    return name.equals(tree.name);

  }

  public int hashCode() {
    int result;
    result = name.hashCode();
    result = 31 * result + args.hashCode();
    result = 31 * result + children.hashCode();
    return result;
  }

  public boolean nameIs(String s) {
    return name.equals(s);
  }

  public int namelessChildrenCount() {
    return args.size();
  }

  public Tree setName(String name) {
    this.name = name;
    return this;
  }

  public static Tree quote(String text) {
    if (text == null) return null;
    return new Tree(quoteString(text));
  }

  public static String quoteString(String text) {
    if (text == null) return null;
    return "\"" + text
      .replace("\\", "\\\\")
      .replace("\"", "\\\"")
      .replace("\n", "\\n")
      .replace("\r", "\\r") + "\"";
  }

  public static String unquote(Tree tree) {
    if (tree == null) return null;
    String text = tree.getName();
    return unquoteString(text);
  }

  public static String unquoteString(String text) {
    if (text.startsWith("\"") && text.endsWith("\"")) {
      text = text.substring(1, text.length()-1);
      Matcher matcher = Pattern.compile("\\\\(.)").matcher(text);
      StringBuffer buf = new StringBuffer();
      while (matcher.find()) {
        String s = matcher.group(1);
        String replacement = s.equals("n") ? "\n" : (s.equals("r") ? "\r" : s);
        matcher.appendReplacement(buf, "");
        buf.append(replacement);
      }
      matcher.appendTail(buf);
      return buf.toString();
      /*.replace("\\n", "\n")
        .replace("\\\"", "\"")
        .replace("\\\\", "\\");*/
    } else
      return text;
  }

  public double doubleValue() {
    return Double.parseDouble(name);
  }

  public float floatValue() {
    return Float.parseFloat(name);
  }

  public long longValue() {
    return Long.parseLong(name);
  }

  public int intValue() {
    return StringUtil.isInteger(name) ? Integer.parseInt(name)
      : (this.<Number> getObject()).intValue();
  }

  public boolean booleanValue() {
    return "true".equals(name);
  }

  public String unquote() {
    return unquote(this);
  }

  public String getString(String kindname) {
    Tree kind = get(kindname);
    return kind == null ? null : kind.unquote();
  }

  public boolean getBool(String kindname) {
    return getBool(kindname, false);
  }

  public boolean getBool(String kindname, boolean defaultValue) {
    Tree kind = get(kindname);
    return kind == null ? defaultValue : kind.booleanValue();
  }

  public Long getLong(String kindname) {
    return get(kindname).longValue();
  }

  public Integer getInt(String kindname) {
    Tree kind = get(kindname);
    return kind == null ? null : kind.intValue();
  }

  public Tree add(String kindname, long value) {
    return add(kindname, new Tree(value));
  }

  public Tree add(String kindname, boolean value) {
    return add(kindname, new Tree(value));
  }

  public Tree addQ(String kindname, String s) {
    return s == null ? this : add(kindname, quote(s));
  }

  public Tree setLong(String kindname, long value) {
    return add(kindname, new Tree(value));
  }

  public Tree setInt(String kindname, int value) {
    return add(kindname, new Tree(value));
  }

  public Tree setBool(String kindname, boolean value) {
    return add(kindname, new Tree(value));
  }

  public Tree setString(String kindname, String s) {
    return s == null ? remove(kindname) : add(kindname, quote(s));
  }

  private Tree remove(String kindname) {
    children.remove(kindname);
    return this;
  }

  public String getString(String kindname, String def) {
    Tree kind = get(kindname);
    return kind != null ? kind.unquote() : def;
  }

  public int getInt(String kindname, int def) {
    Tree kind = get(kindname);
    return kind != null ? kind.intValue() : def;
  }

  public Tree add(String kindname, Double value) {
    return setDouble(kindname, value);
  }

  public Tree setDouble(String kindname, Double value) {
    if (value == null) return this;
    return add(kindname, String.valueOf(value));
  }

  public Tree add(String kindname, Long value) {
    return setLong(kindname, value);
  }

  public Tree add(String kindname, Float value) {
    if (value == null) return this;
    return add(kindname, String.valueOf(value));
  }

  public void display(Display display) {
    if (children.isEmpty() && args.isEmpty()) {
      display.put(maybeQuoteId(getName()));
      return;
    }

    if (fitForSimplifiedRepr()) {
      display.put(maybeQuoteId(getName())).put(" ").put(args.get(0));
      return;
    }

    display.put(maybeQuoteId(getName())).putnl("(").indent();
    boolean first = true;

    for (String name : children.keySet()) {
      if (first)
        first = false;
      else
        display.putnl(", ");
      display.put(maybeQuoteId(name)).put("=").put(children.get(name));
    }

    for (Tree kind : args) {
      if (first)
        first = false;
      else
        display.putnl(", ");
      display.put(kind);
    }

    display.put(")").unindent();
  }

  public String toMultiLineString() {
    StringDisplay display = new StringDisplay();
    display(display);
    return display.toString();
  }

  public Double getDouble(String kindname) {
    Tree kind = get(kindname);
    return kind == null ? null : kind.doubleValue();
  }

  public double getDouble(String kindname, double defaultValue) {
    Tree kind = get(kindname);
    return kind == null ? defaultValue : kind.doubleValue();
  }

  public Float getFloat(String kindname) {
    Tree kind = get(kindname);
    return kind == null ? null : kind.floatValue();
  }

  public float getFloat(String kindname, float defaultValue) {
    Tree kind = get(kindname);
    return kind == null ? defaultValue : kind.floatValue();
  }

  public Tree add(String kindname, Object object) {
    return object == null ? this : add(kindname, objectToTree(object));
  }

  public Tree add(String kindname, Object object, Treeifier mainTreeifier) {
    return object == null ? this : add(kindname, objectToTree(object, mainTreeifier));
  }

  public <A> A getObject(String kindname) {
    Tree kind = get(kindname);
    return kind == null ? null : TreeUtil.<A> treeToObject(kind);
  }

  public Tree setObject(String kindname, Object object) {
    return add(kindname, object);
  }

  public Tree setObject(String kindname, Object object, Treeifier mainTreeifier) {
    return add(kindname, object, mainTreeifier);
  }

  public <A> A getObject(String kindname, A def) {
    A a = this.<A> getObject(kindname);
    return a == null ? def : a;
  }

  public Tree put(int i, Tree tree) {
    while (i >= args.size())
      args.add(new Tree());
    args.set(i, tree);
    return this;
  }

  /** overwrite contents of this tree with the tree passed */
  public void set(Tree tree) {
    fromTree(tree);
  }

  public void fromTree(Tree tree) {
    name = tree.getName();
    args = new ArrayList<Tree>(tree.args);
    children = new TreeMap<String, Tree>(tree.children);
  }

  public Tree toTree() {
    return this;
  }

  public Tree add(Object object) {
    return add(objectToTree(object));
  }

  public Object getObject(int i) {
    return treeToObject(get(i));
  }

  public int getInt(int i) {
    return get(i).intValue();
  }

  public long getLong(int i) {
    return get(i).longValue();
  }

  public String getString(int i) {
    Tree tree = get(i);
    return tree == null ? null : tree.stringValue();
  }

  private String stringValue() {
    return unquote();
  }

  public <A> A getObject() {
    return TreeUtil.<A> treeToObject(this);
  }

  public Tree addAll(Collection<Tree> trees) {
    for (Tree tree : trees)
      add(tree);
    return this;
  }

  public static Tree parse(String text) {
    return TreeUtil.textToTree(text);
  }

  public int size() {
    return namelessChildrenCount();
  }

  /*public Tree add(Object value, Treeifier treeifier) {
    add(treeifier.objectToTree(value));
    return this;
  }*/

  public Tree addString(String s) {
    return add(Tree.quote(s));
  }

  public Tree subTree(String name) {
    Tree tree = get(name);
    if (tree == null)
      add(name, tree = new Tree());
    return tree;
  }

  public byte[] getBytes(String childName) {
    String s = getString(childName, "");
    return hexToBytes(s);
  }

  private static byte[] hexToBytes(String s) {
    byte[] bytes = new byte[s.length()/2];
    for (int i = 0; i < bytes.length; i++)
      bytes[i] = (byte) Integer.parseInt(s.substring(i*2, i*2+2), 16);
    return bytes;
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder stringBuilder = new StringBuilder(bytes.length*2);
    for (int i = 0; i < bytes.length; i++) {
      String s = "0" + Integer.toHexString(bytes[i]);
      stringBuilder.append(s.substring(s.length()-2, s.length()));
    }
    return stringBuilder.toString();
  }

  public Tree setBytes(String childName, byte[] bytes) {
    return add(childName, bytesToHex(bytes));
  }

  public Tree put(String childName, Tree tree) {
    return add(childName, tree);
  }

  public Tree set(String childName, Tree tree) {
    return add(childName, tree);
  }

  public Tree set(String childName, int i) {
    return add(childName, i);
  }

  public Tree set(String childName, String s) {
    return add(childName, s);
  }

  public void put(String key, String value) {
    add(key, value);
  }

  /** remove all children (named and unnamed) */
  public void clear() {
    children.clear();
    args.clear();
  }

  public String name() {
    return name;
  }

  public Tree setUnquotedString(String childName, String s) {
    if (s == null)
      return remove(childName);
    else
      return add(childName, new Tree(s));
  }

  public String getUnquotedString(String childName) {
    return getUnquotedString(childName, null);
  }

  public String getUnquotedString(String childName, String defaultValue) {
    Tree t = get(childName);
    return t != null ? t.getName() : defaultValue;
  }

  public Tree addUnquotedString(String s) {
    return add(new Tree(s));
  }

  public String getUnquotedString(int idx) {
    return getUnquotedString(idx, null);
  }

  public String getUnquotedString(int idx, String defaultValue) {
    Tree t = get(idx);
    return t != null ? t.getName() : defaultValue;
  }

  public void setUnquotedString(int idx, String s) {
    set(idx, new Tree(s));
  }

  public void set(int idx, Tree tree) {
    put(idx, tree);
  }

  public Tree addFloat(float f) {
    return add(new Tree(f));
  }

  public float getFloat(int idx) {
    return get(idx).floatValue();
  }

  public Tree addInt(int i) {
    return add(new Tree(i));
  }

  public Tree addLong(long l) {
    return add(new Tree(l));
  }

  public Tree setFloat(String childName, float f) {
    return set(childName, new Tree(f));
  }

  public List<Tree> namelessChildren() {
    return args;
  }

  public String toWrappedString() {
    WrappedDisplay wrappedDisplay = new WrappedDisplay();
    display(wrappedDisplay);
    return wrappedDisplay.toString();
  }

  public void remove(int idx) {
    args.remove(idx);
  }
}
