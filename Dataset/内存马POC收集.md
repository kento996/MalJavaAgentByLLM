# Java 内存马

## 传统Web型内存马

1. `demo2.java`

   通过 动态注册 Filter 的方式注入内存马

2. `FilterBasedBasic_2.java`

   基于 Tomcat 的 Filter 型内存马

3. `FilterTemplate.java`

   攻击者通过动态注册 Filter（或静态部署）插入恶意逻辑，实现对 Web 请求的控制。

4. `FliterMSDemo.java`

   - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
   - 描述：基于 Fliter 型 demo 修改，利用 Servlet Filter 机制进行持久化后门注入

5. `ListerMSDemo.java`

   - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
   - 描述：基于 Listener 型 demo 修改。这是一个 Tomcat Listener 内存马，在 Tomcat 启动时 通过监听器注入持久化恶意代码，允许攻击者通过 HTTP 请求 (`?cmd=命令`) 远程执行任意系统命令。

6. `ServletBasedBasic_2.java`

   基于 Tomcat 容器的 Servlet 动态注册型内存马。

7. `ServletMSDemo.java`

   使用反射方式绕过正常注册流程，将自身（`MemShell`）注入到 Servlet 容器中（通过 `addServlet` 和 `addMapping` 实现），注册路径为 `/memShell`。

8. `ServletTemplate.java`

   继承 `HttpServlet`的标准Servlet

9. `TomcatShellInject.java`

   基于 Tomcat 容器的 Filter 型内存马

   

## 框架型内存马

1. `ControllerBased.java`

   Java Spring 环境下典型的 Controller 型内存马。它通过反射和 Spring Bean 注入机制，将恶意 Controller 动态加载并注册到现有 Web 应用中，监听新的 URL，实现持久化控制，且不会在磁盘上留下明显痕迹。

2. `DASCTFCBCTF2022.java`

   - 参考：https://www.ctfiot.com/57949.html 的 JavaMaster 题目
   - 描述： `InjectToController` 利用 Spring MVC 机制，在运行时动态注册一个新的 Controller，使攻击者可以远程执行任意命令RCE，注入内存马。利用 `AbstractTranslet` 作为 Gadget，后续用于cc链攻击。

3. `DASCTFCBCTF2023.java`

   - 参考：https://pankas.top/2023/10/22/dasctfxcbctf-2023-bypassjava-wp/
   - 描述：动态加载恶意 JNI 库，反序列化后存储执行，通过Spring Web内存马注入，实现了 Spring 内存马。通过 Spring MVC 组件进行动态路由注册，用 native JNI 加载动态链接库来执行系统命令，注册后门命令执行路由。

4. `POJOJackson.java`

   - 参考：https://github.com/datouo/CTF-Java-Gadget/blob/master/src/main/java/com/xiinnn/commonly/POJOJackson.java
   - 描述：显式使用了 Spring Framework 的 AOP 特性，构造了一个代理对象 `JdkDynamicAopProxy`，这是典型的 Spring AOP 相关行为，说明是基于 Spring 框架实现的内存马逻辑。

5. `SpringControllerDemo.java`

   - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
   - 描述：这个 Java 代码实现了一个 Spring Boot Web 内存马，可以动态向 Spring MVC 控制器注册新的恶意接口，使攻击者可以远程执行任意命令。

6. `SpringWebFluxDemo.java`

   - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
   - 描述：定义了一个 Spring WebFlux 过滤器 (`MemoryShellFilter`)，它利用反射技术，动态修改 Netty 服务器的 WebFilter 链，从而实现内存马功能。

   

## 中间件型内存马

1. `FilterBasedBasic_3.java`

   针对 WebLogic

2. `FilterBasedBasic_4.java`

   动态向 WebSphere 应用服务器注册一个 Filter，从而实现内存驻留的恶意代码注入

3. `FilterBasedWithoutRequest_1.java`

   使用 JMX 的 `JmxMBeanServer` 和 `Repository` 查询 Jetty 中的 `WebAppContext`，利用反射从 Jetty 的 `_servletHandler` 中获取并修改 `_filters`，然后动态添加一个新的 `Filter`（实际使用的是 `FilterTemplate`，可能是攻击者定义的恶意 filter），其方式依赖中间件内部结构。

4. `FilterBasedWithoutRequest_2.java`

   基于 Tomcat 的 Filter 型中间件内存马

5. `FilterBasedWithoutRequest_2.java`

   针对 WebSphere 中间件，具体是往 WebSphere 的运行环境中动态注册一个过滤器

6. `FilterBasedWithoutRequest.java`

   使用了 JBoss + Undertow 的中间件机制对其底层 Filter 机制的操作完成动态注入

7. `FilterBasedWithoutRequestVariant_1.java`

   中间件型内存马，专门针对 Jetty 中间件的实现

8. `FilterBasedWithoutRequestVariant_2.java`

   基于 Tomcat 的 Filter 动态注入的内存马

9. `FilterBasedWithoutRequestVariant_4.java`

   一个针对 IBM WebSphere 应用服务器的内存马实现，它通过 WebSphere 的底层类和反射机制动态注册 Filter，并插入到请求处理链的最前端。

10. `ServletBasedWithoutRequest_1.java`

    利用了 Jetty 中间件本身的管理接口和内部结构（JMX + Jetty API）来动态注册 Servlet，并未依赖 Spring 或其它 Web 框架机制，同时也未通过 Agent 或 Instrumentation API 修改类定义。

11. `ServletBasedWithoutRequest_2.java`

    通过 JMX + 反射 + Tomcat 内部结构动态注入 Servlet

12. `ServletBasedWithoutRequest.java`

    利用了 JBoss（Undertow）服务器内部结构和特性，操作中间件原生对象进行 Servlet 注入。

13. `ServletBasedWithoutRequestVariant_1.java`

    利用 Jetty 中间件

14. `ServletBasedWithoutRequestVariant_2.java`

    码通过 JMX 机制获取 `MBeanServer`，并深入操作了 `DefaultMBeanServerInterceptor` 和 `Repository`。它利用反射获取并操作了 Tomcat 的内部结构并动态注册新的servlet

15. `TomcatExecutorDemo.java`

    - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
    - 描述：基于Tomcat Executor内存马demo修改。劫持 Tomcat 的线程池，替换 Tomcat 的 `ThreadPoolExecutor`为自定义的`ThreadExecutor`，并在处理请求时注入恶意代码。在新线程执行任务时，检测请求数据是否包含特定关键字（“hacku”），如果检测到关键字，就解析并执行系统命令，命令执行结果会通过 HTTP 头部 `Result` 返回给攻击者。

16. `TomcatUpgrade.java`

    - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
    - 描述：利用 Tomcat 协议升级机制（HTTP Upgrade） 和反射技术来实现远程代码执行（RCE），动态修改 `httpUpgradeProtocols` 以部署可持续利用后门。

17. `TomcatValveDemo.java`

    - 参考：https://github.com/W01fh4cker/LearnJavaMemshellFromZero
    - 描述：基于Tomcat Valve型demo修改。这是一个中间件型内存马，通过 `Reflection`（反射）方式获取 Tomcat `StandardContext` 运行时环境，动态向 Tomcat 的 `StandardPipeline` 添加恶意 `Valve` 组件。恶意 `Valve` 允许远程执行任意系统命令（RCE），通过 `cmd` 参数获取攻击者传入的命令并执行，最终返回命令执行的结果给攻击者。

    

## Agent型内存马

1. `Attach.java`

   Attach API + Agent Jar，将位于磁盘上的 `EvilFilter.jar` 注入到目标 JVM

2. `demo.java`

   整个类的最终目的是通过构造特定的反序列化对象链（Gadget），实现命令执行或加载 agent（注入内存马），并通过 `Shiro` 的 RememberMe cookie 加密构造进行传输。

   

## 新型内存马

1. `SpringBootEcho_fd.java`

   结合反序列化利用与底层 socket 劫持的新型攻击方式，通常出现在 SpringBoot 或其他 Java 微服务环境中

2. `addFilter.jsp`

   典型的 JSP 注入，动态注册 Filter

3. `shell.jsp`

   一个 嵌入 JSP 页面的脚本，通过 自定义类加载器 + Base64 字节码的方式手动注入内存类；

4. `filter.jsp`

   针对 WebLogic 的内存马，通过操作 WebLogic 的内部结构（如线程上下文、Filter 管理器等）实现恶意代码注入。

5. `listener.jsp`

   依赖 WebLogic 的运行时结构，使用反射操作线程上下文，动态注册恶意监听器 `EvilListener`

6. `servlet.jsp`

   是针对 WebLogic 中间件环境 的内存

   

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

   
