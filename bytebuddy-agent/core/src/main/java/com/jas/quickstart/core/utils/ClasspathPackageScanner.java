package com.jas.quickstart.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.*;


@Slf4j
public class ClasspathPackageScanner {

    private String basePackage;

    public ClasspathPackageScanner(String basePackage) {
        this.basePackage = basePackage;
    }

    public Set<String> getFullyQualifiedPrefixList() throws IOException{
        log.info("Local Class Scanning.");
        return doScan(basePackage);
    }

    private Set<String> doScan(String filePath) throws IOException{

        Set<String> prefixSet = new HashSet<>();
        if (filePath.isEmpty()){
            return prefixSet;
        }

        if (FileUtils.isJarFile(filePath)){
            prefixSet = readFromJarFile(filePath);
        }else {
            prefixSet = readFromDirectory(filePath);
        }

        prefixSet = StringUtil.mergePrefix(prefixSet);
        return prefixSet;
    }

    public static Set<String> readFromJarFile(String jarPath) throws IOException {
        List<String> classes = FileUtils.findClassesFromJarFile(jarPath);
        Set<String> prefixSet = new HashSet<>();
        for (String name: classes){
            String prefix = StringUtil.getPrefixFromClass(name);
            prefixSet.add(prefix);
        }
        return prefixSet;
    }

    public Set<String> readFromDirectory(String path) throws IOException{
        File file = new File(path);
        List<String> names = FileUtils.findClassesFromDirectory(file, "");
        Set<String> prefixSet = new HashSet<>();
        for (String name: names){
            name = name.substring(0, name.length() - 6);
            int lastSlashIndex = name.lastIndexOf('.');
            if (lastSlashIndex != -1) {
                String prefix = name.substring(0, lastSlashIndex);
                prefixSet.add(prefix);
            }
        }

        return prefixSet;
    }


}
