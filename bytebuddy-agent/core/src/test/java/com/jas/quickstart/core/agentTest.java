package com.jas.quickstart.core;

import org.junit.jupiter.api.Test;

public class agentTest {
    @Test
    public void testClassLoad(){
        System.out.println(System.getProperty("user.dir"));
        HelloWorld helloWorld = new HelloWorld();
        helloWorld.hello();
    }

    private class HelloWorld{
        public void hello(){
            System.out.println("hello world");
        }
    }
}
