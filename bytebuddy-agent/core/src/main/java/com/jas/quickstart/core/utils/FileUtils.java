package com.jas.quickstart.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class FileUtils {

    public static List<String> findClassesFromDirectory(File directory, String packageName) throws IOException {
        List<String> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (Objects.isNull(files)) {
            return classes;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                if (packageName.isEmpty()){
                    classes.addAll(findClassesFromDirectory(file,  file.getName()));
                }else {
                    classes.addAll(findClassesFromDirectory(file, packageName + "." + file.getName()));
                }
            } else if (isClassFile(file.getName())) {
                String clzName = packageName + '.' + file.getName();
                classes.add(clzName);
            } else if (isJarFile(file.getName())) {
                classes.addAll(findClassesFromJarFile(file.getAbsolutePath()));
            }
        }
        return classes;
    }

    public static List<String> findClassesFromJarFile(String jarPath) throws IOException {
        JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jarIn.getNextJarEntry();

        List<String> classes = new ArrayList<>();
        while (null != entry){
            String name = entry.getName();
            if (isClassFile(name)){
                classes.add(name);
            }
            entry = jarIn.getNextJarEntry();
        }
        return classes;
    }


    public static boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

    public static boolean isJarFile(String name) {
        return name.endsWith(".jar");
    }
}
