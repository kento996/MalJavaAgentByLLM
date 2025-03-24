package com.jas.quickstart.core.aop.weaving;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class WeavingManagerTest {
    @Test
    public void testInit() throws Exception{
        String classPath = "D:\\projects\\JavaProjects\\JavaMemShell\\JavaMemTro\\bytebuddy-agent\\release\\core.jar";
        List<String> ignoreList = new ArrayList<>();
        ignoreList.add("net.bytebuddy");
        ignoreList.add("java.arthas");
        WeaveManager weaveManager = new WeaveManager();
        weaveManager.init(true, "test", classPath, ignoreList);
    }
}
