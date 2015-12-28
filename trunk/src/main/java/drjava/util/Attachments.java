/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

/** The attachments mechanism allows associating arbitrary objects with each other.
 *  One is called the "attachment", the other the "carrier".
 *  <p>
 *  A carrier can have many attachments. An attachment can also belong to multiple
 *  carriers at once.
 *  <p>
 *  Neither carrier nor attachment are queried or informed in the process. The attachment
 *  relation exists only in a global map managed by the Attachments class.
 *
 *  <h3>Threading</h3>
 *
 *  This class is fully synchronized.
 *
 *  <h3>Garbage collection discussion</h3>
 *
 *  Ideal: Carrier strongly supports attachment. Attachment generally does not
 *  support carrier (consider a singleton attachment bound to many carriers).
 *  <p>
 *  Reality: Same as ideal, with one pitfall: Attachments MUST NOT strongly reference
 *  carrier; otherwise, both live eternally. (Possible solution: Introduce a
 *  Carrier interface.)
 *
 *  */
public class Attachments {
  /** global map of all attachment relations, key: carrier, value: attachment list */
  private static WeakHashMap<Object, List<Object>> map
    = new WeakHashMap<Object, List<Object>>();

  /** map lookup/extension (private) */
  private static synchronized List<Object> getList(Object carrier) {
    List<Object> extensions = map.get(carrier);
    if (extensions == null)
      map.put(carrier, extensions = new ArrayList<Object>());
    return extensions;
  }

  /** look for attachment of some type */
  public static synchronized <A> A get(Object carrier, Class<A> attachmentType) {
    List<Object> attachments = getList(carrier);
    for (Object attachment : attachments) {
      if (attachmentType.isInstance(attachment))
        return (A) attachment;
    }
    return null;
  }

  /** add attachment */
  public static synchronized void add(Object carrier, Object attachment) {
    getList(carrier).add(attachment);
  }

  /** add or replace attachment (all existing attachments of given type are removed) */
  public static synchronized <C> void set(Object carrier, C attachment, Class<C> attachmentType) {
    remove(carrier, attachmentType);
    getList(carrier).add(attachment);
  }

  /** remove all attachments of given type */
  public static synchronized void remove(Object attachment, Class attachmentType) {
    List<Object> extensions = getList(attachment);
    for (Iterator<Object> it = extensions.iterator(); it.hasNext();) {
      Object extension = it.next();
      if (attachmentType.isInstance(extension))
        it.remove();
    }
  }

  /** find an instance of aClass in carrier's vicinity.
   *  i.e., if carrier itself is an instance of aClass, return carrier.
   *  otherwise look for a matching attachment */
  public static <C> C find(Object carrier, Class<C> aClass) {
    if (aClass.isInstance(carrier))
      return (C) carrier;
    else
      return get(carrier, aClass);
  }
}