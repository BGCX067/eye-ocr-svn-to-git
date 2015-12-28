/*
 (C) 2007 Stefan Reich (jazz@drjava.de)
 This source file is part of Project Prophecy.
 For up-to-date information, see http://www.drjava.de/prophecy

 This source file is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation, version 2.1.
 */
package prophecy.common;

import drjava.util.FileTreePersistence;
import drjava.util.Tree;
import drjava.util.TreePersistence;
import drjava.util.Attachments;
import prophecy.common.PersistentTree;
import prophecy.common.CapsuledTreePersistence;
import org.apache.log4j.Logger;

import java.io.File;

/*
 * from: m39
 */
public class ClassData extends PersistentTree {

    private static Logger l = Logger.getLogger(ClassData.class);
    private static String memoryDir = "memory/classdata";

    private ClassData(TreePersistence persistence) {
        super(new CapsuledTreePersistence(persistence));
    }

    public static synchronized ClassData get(Class aClass) {
        ClassData classData = Attachments.get(aClass, ClassData.class);
        if (classData == null) {
            File file = new File(getMemoryDir(), aClass.getName().replace('.', '/') + ".tree");
            l.debug("file path is " + file.getAbsolutePath());
            FileTreePersistence persistence = new FileTreePersistence(file);
            classData = new ClassData(persistence);
            Attachments.add(aClass, classData);
        }
        return classData;
    }

    public static synchronized String getMemoryDir() {
        return memoryDir;
    }

    public static synchronized void setMemoryDir(String memoryDir) {
        ClassData.memoryDir = memoryDir;
    }

    public static ClassData get(Object object) {
        return get(object.getClass());
    }

    public Tree getClassPairing(Class partnerClass) {
        return subTree("classPairings").subTree(partnerClass.getName());
    }

    public Tree set(String childName, String s) {
        return add(childName, s);
    }
}