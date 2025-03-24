package com.jas.quickstart.core.utils;

import java.net.URL;
import java.util.*;

/**
 * @author ReaJason
 * @since 2024/1/27
 */
public class StringUtil {
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String join(final char delimiter, final String... strings) {
        if (strings.length == 0) {
            return null;
        }
        if (strings.length == 1) {
            return strings[0];
        }
        int length = strings.length - 1;
        for (final String s : strings) {
            if (s == null) {
                continue;
            }
            length += s.length();
        }
        final StringBuilder sb = new StringBuilder(length);
        if (strings[0] != null) {
            sb.append(strings[0]);
        }
        for (int i = 1; i < strings.length; ++i) {
            if (isNotEmpty(strings[i])) {
                sb.append(delimiter).append(strings[i]);
            } else {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    /**
     * "file:/home/whf/cn/fh" -> "/home/whf/cn/fh"
     * "jar:file:/home/whf/foo.jar!cn/fh" -> "/home/whf/foo.jar"
     */
    public static String getRootPath(URL url) {
        String fileUrl = url.getFile();
        int pos = fileUrl.indexOf('!');

        if (-1 == pos) {
            return fileUrl;
        }

        return fileUrl.substring(5, pos);
    }


    /**
     * "cn.fh.lightning" -> "cn/fh/lightning"
     * @param name
     * @return
     */
    public static String dotToSplash(String name) {
        return name.replaceAll("\\.", "/");
    }

    /**
     * "Apple.class" -> "Apple"
     */
    public static String trimExtension(String name) {
        int pos = name.indexOf('.');
        if (-1 != pos) {
            return name.substring(0, pos);
        }

        return name;
    }

    /**
     * /application/home -> /home
     * @param uri
     * @return
     */
    public static String trimURI(String uri) {
        String trimmed = uri.substring(1);
        int splashIndex = trimmed.indexOf('/');

        return trimmed.substring(splashIndex);
    }

    public static String getPrefixFromClass(String clzName) {
        String fileName = clzName.replace(".class", "");

        int lastSlashIndex = fileName.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            fileName = fileName.substring(0, lastSlashIndex);
        }

        String prefix = fileName.replace('/', '.');

        return prefix;
    }

    public static Set<String> mergePrefix(Set<String> preSet){
        Set<String> mergedPrefixes = new HashSet<String>();
        for (String clzName: preSet){
            if (mergedPrefixes.isEmpty()){
                mergedPrefixes.add(clzName);
                continue;
            }
            String toMergePrefix = "";
            for (String mergePrefix: mergedPrefixes){
                toMergePrefix = comparePrefix(mergePrefix, clzName);
                long count = toMergePrefix.chars().filter(ch -> ch == '.').count();
                if (count >= 1){
                    mergedPrefixes.remove(mergePrefix);
                    mergedPrefixes.add(toMergePrefix);
                    break;
                }
            }
            if (toMergePrefix.isEmpty()){
                mergedPrefixes.add(clzName);
            }

        }
        return mergedPrefixes;
    }

    public static String comparePrefix(String s1, String s2){
        List<String> l1 = splitStringToList(s1);
        List<String> l2 = splitStringToList(s2);
        int min = Math.min(l1.size(), l2.size());
        for (int i = 0; i < min; i ++){
            if(!l1.get(i).equals(l2.get(i))) return String.join(".", l1.subList(0,i));
        }
        return String.join(".", l1.subList(0,min));
    }

    public static List<String> splitStringToList(String input) {
        // 使用 . 作为分隔符进行分割
        String[] parts = input.split("\\."); // 注意：. 是正则表达式中的特殊字符，需要用 \\ 转义

        return Arrays.asList(parts);
    }

}
