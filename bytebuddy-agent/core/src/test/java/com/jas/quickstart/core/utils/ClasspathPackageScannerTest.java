package com.jas.quickstart.core.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ClasspathPackageScannerTest {
    @Test
    public void testReadFromJarFile() throws Exception{
//        String absPath = "..\\release\\core";
        String absPath = "..\\release\\core.jar";
        ClasspathPackageScanner classpathPackageScanner = new ClasspathPackageScanner(absPath);
        classpathPackageScanner.getFullyQualifiedPrefixList();
    }

    @Test
    public void testSplitStringToList(){
        String s = "test".substring(0,0);
        List<String> l = new ArrayList<String>();
        l.add("");
        System.out.println(1 + s + 1);
        System.out.println(String.join(".",l));
        System.out.println(111);
    }
}
