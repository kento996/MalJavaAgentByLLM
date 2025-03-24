# 搜集方式

1. exploit-db检索 java poc、java deserialization、java rce
2. buuctf 检索 java
3. ctftime 检索 java deserialize
4. github 检索 java内存马、java反序列化





# Java 内存马

1. `POJOJackson.java`

   - 参考：https://github.com/datouo/CTF-Java-Gadget/blob/master/src/main/java/com/xiinnn/commonly/POJOJackson.java

2. `DASCTFCBCTF2022_shell.java`

   - 参考：https://www.ctfiot.com/57949.html 的 JavaMaster 题目
   - 描述： `InjectToController` 利用 Spring MVC 机制，在运行时动态注册一个新的 Controller，使攻击者可以远程执行任意命令RCE，注入内存马。利用 `AbstractTranslet` 作为 Gadget，后续用于cc链攻击。

   >  **后续的cc11利用链代码 `DASCTFCBCTF2022_cc11.java` 也收入数据集**：
   >
   > 利用 Apache Commons Collections 库的 `InvokerTransformer` 以及 `TemplatesImpl` 进行代码执行。

3. `DASCTFCBCTF2023.java`

   - 参考：https://pankas.top/2023/10/22/dasctfxcbctf-2023-bypassjava-wp/
   - 描述：实现了 Spring 内存马。动态加载恶意 JNI 库，反序列化后存储执行，通过Spring Web内存马注入，注册后门命令执行路由。

4. `ServletMSDemo.java`

   - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
   - 描述：基于 Servlet 型 demo 修改，动态注册 Servlet 到 `/memshell`

5. `FliterMSDemo.java`

   - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
   - 描述：基于 Fliter 型 demo 修改，利用 Servlet Filter 机制进行持久化后门注入

6. `ListerMSDemo.java`

   - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
   - 描述：基于 Listener 型 demo 修改。这是一个 Tomcat Listener 内存马，在 Tomcat 启动时 通过监听器注入持久化恶意代码，允许攻击者通过 HTTP 请求 (`?cmd=命令`) 远程执行任意系统命令。

7. `TomcatValveDemo.java`

   - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
   - 描述：基于Tomcat Valve型demo修改。这是一个中间件型内存马，通过 `Reflection`（反射）方式获取 Tomcat `StandardContext` 运行时环境，动态向 Tomcat 的 `StandardPipeline` 添加恶意 `Valve` 组件。恶意 `Valve` 允许远程执行任意系统命令（RCE），通过 `cmd` 参数获取攻击者传入的命令并执行，最终返回命令执行的结果给攻击者。

8. `SpringControllerDemo.java`

   - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
   - 描述：这个 Java 代码实现了一个 Spring Boot Web 内存马，可以动态向 Spring MVC 控制器注册新的恶意接口，使攻击者可以远程执行任意命令。

9. `SpringWebFluxDemo.java`

   - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
   - 描述：定义了一个 Spring WebFlux 过滤器 (`MemoryShellFilter`)，它利用反射技术，动态修改 Netty 服务器的 WebFilter 链，从而实现内存马功能。

10. `TomcatUpgrade.java`

    - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
    - 描述：利用 Tomcat 协议升级机制（HTTP Upgrade） 和反射技术来实现远程代码执行（RCE），动态修改 `httpUpgradeProtocols` 以部署可持续利用后门。

11. `TomcatExecutorDemo.java`

    - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
    - 描述：基于Tomcat Executor内存马demo修改。劫持 Tomcat 的线程池，替换 Tomcat 的 `ThreadPoolExecutor`为自定义的`ThreadExecutor`，并在处理请求时注入恶意代码。在新线程执行任务时，检测请求数据是否包含特定关键字（“hacku”），如果检测到关键字，就解析并执行系统命令，命令执行结果会通过 HTTP 头部 `Result` 返回给攻击者。

    

    

# 反序列化

1. `2020YCBCTF.java`

   - 参考：https://blog.csdn.net/RABCDXB/article/details/124099664
   - 描述：该 PoC 的目的是构造一个 Java 反序列化攻击，利用 MySQL 连接的 `autoDeserialize=true` 机制来触发远程代码执行（RCE）。其中，关键攻击点是 `databaseInfo.setUsername(...)` 赋值的一条 Bash 命令，用于反弹 Shell。

2. `VNCTF2022.java`

   - 参考：https://blog.q1ngchuan.top/2024/06/16/
   - 描述：wp中提供的exp作用在于序列化一个 `User` 对象 并使用 Base64 编码 输出结果，本身并不具备恶意性，所以我添加了一个恶意 readObject 方法弹出计算器

3. `2022DASCTF.java`

   - 参考：https://www.cnblogs.com/EddieMurphy-blogs/p/18166454
   - 描述：利用 `TemplatesImpl` 进行任意代码执行（RCE），并结合 `PriorityQueue` 触发反序列化执行恶意代码。

4. `lyzy2021.java`

   - 参考：https://blog.csdn.net/RABCDXB/article/details/125576643
   - 描述：读取恶意类 `evil.class` 的字节码，使用 `ToStringBean` 进行反序列化

5. `realWord2021.java`

   - 参考：https://ctftime.org/writeup/25670
   - 描述：构造一个恶意的 `Serializable` 对象，并将其写入文件 `object.ser`，当目标应用反序列化该对象时，会触发恶意行为。

6. `exploit_300.java`

   - 参考：https://github.com/p4-team/ctf/tree/master/2019-09-07-trendmicro-quals/exploit_300
   - 描述：在原有POC的基础上添加攻击代码，利用 `ysoserial` 生成恶意 payload，并发送到目标服务器，尝试触发反序列化漏洞执行远程代码。

7. `CC1_LazyMap.java`

   - 参考：https://github.com/FFreestanding/JavaUnserializeChain/blob/main/CC1/src/CC1_LazyMap.java
   - 描述：利用 Commons-Collections 3.1 的反序列化漏洞，触发RCE

8. `CC3_Main.java`

   - 参考https://github.com/FFreestanding/JavaUnserializeChain/blob/main/CC3/src/Main.java
   - 描述：该代码是一个Java 反序列化漏洞（CommonsCollections 3 反序列化攻击链）的 PoC，主要利用 `TemplatesImpl`结合 Apache Commons Collections（CC3） 进行恶意代码执行。

   
