import csv
import sys
import re
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from DetectVulnByLLM.llm.llm import OllamaClient

malware_API="""
如下是常见的一些可能会出现在Java内存马中的代码片段：
## 各种种类的Java内存马的重要实现过程
```
(StandardContext)
.createWrapper()
.addFilterDef(
.addURLPattern(
(WebApplicationContext)
.registerMapping(
adaptedInterceptors.
DefaultWebFilterChain(
(StandardPipeline)
httpUpgradeProtocols
.setExecutor(
addAfterServiceListener
.addServletMappingDecoded(
.loadAgent(
.detach(
addFilterMapBefore
addMappingForUrlPatterns
filterPatternList
prependFilterMapping
getFilterMappings
ApplicationServletRegistration
addServlet
```
## 恶意逻辑会用到的代码片段（注：该这些片段可能不会出现在内存马注入的逻辑中）
```
getRuntime().exec(
ProcessBuilder.start
RuntimeUtil.exec(
RuntimeUtil.execForStr(
System.getProperty(
Streams.copy(
.getOriginalFilename(
.transferTo(
UploadedFile(
FileUtils.copyFile(
MultipartHttpServletRequest
.getFileName(
.saveAs(
.getFileSuffix(
.getFile
MultipartFile
/bin/sh
/bin/bash
```
## Java内存马代码可能会使用的逻辑
```
.getParameter(
.getSuperclass()
.exec(
.addMessageHandler(
.invoke(
.getName().equals("system")
org.apache.coyote.Request.
Runtime.getRuntime()
.currentThread().getThreadGroup()
(SocketWrapperBase)
.addShutdownHook(
.getBasicRemote()
.Base64.
getDecoder().decode
```
"""
def main():
    model_name="deepseek-r1:70b"
    client = OllamaClient()
    class_result_folder = "Dataset/EvilClasses"
    output_csv_path = "Dataset/output.csv"

    total = 0
    correct = 0

    with open(output_csv_path, mode='w', newline='', encoding='utf-8') as csvfile:
        csv_writer = csv.writer(csvfile)
        csv_writer.writerow(['文件名', '标签', 'LLM判断结果'])

        for filename in os.listdir(class_result_folder):
            if filename.endswith(".java") or filename.endswith(".jsp"):
                with open(os.path.join(class_result_folder, filename), 'r',encoding='utf-8') as file:
                    try:
                        code = file.read()
                        # 默认所有样本为内存马
                        true_label = 1
                        total += 1

                        prompt_function=prompt_function = f"""
                        你是一位Java中间件安全专家，请分析以下代码的核心行为，按顺序检查：
                        1. 是否通过反射/JNI/类加载器等机制动态修改中间件核心组件（如Tomcat的StandardContext、Spring的HandlerMapping）？
                        2. 是否注册了未在配置文件中声明的Servlet/Filter/Listener/UpgradeProtocol等组件？
                        3. 是否存在敏感操作（如命令执行、字节码注入）或隐蔽性设计（随机类名、无文件驻留）？

                        ## 代码
                        ```
                        {code}
                        ```
                        ## 输出要求
                        用50字内回答，按此模板：
                        【行为】动态注入： 组件类型（是/否），技术手段。【恶意可能性】原因。
                        示例：【行为】动态注入Filter（是），反射修改Tomcat的StandardContext。【恶意可能性】无配置文件声明，可路由恶意请求。
                        """
                        functinDescription=client.generate(model=model_name, prompt=prompt_function)
                        
                        print(f"description {filename}:"+"-"*30)
                        print(functinDescription)
                        print(f"description {filename} end:"+"-"*30)
                        for i in range(3):
                            print()
                        
                        prompt_malware_check=f"""
                        **内存马判定规则（依次检查，满足任意一条即判定为是）**
                        1. **动态注入中间件核心组件**：通过反射/Unsafe/Instrumentation等修改包括但不限于以下对象：
                        - Tomcat: StandardContext、filterConfigs、ServletContainer
                        - Spring: AbstractHandlerMapping、Controller类池
                        - Jetty: ServletHandler、FilterHolder
                        - 且注册的组件（类名/URL）未在配置文件中定义。

                        2. **敏感行为载体**：注入的组件包含以下代码特征：
                        - 解析请求参数（如`request.getParameter("cmd")`）
                        - 动态加载类（`defineClass`/`ClassLoader`篡改）
                        - 反射调用Runtime/ProcessBuilder等敏感类

                        3. **隐蔽性设计**：符合以下任意一项：
                        - 类名/路径随机化（如`filterName = "dynamic_" + System.nanoTime()`）
                        - 无文件驻留（仅通过字节码操作在内存生成类）
                        - 利用中间件漏洞（如JMX、JNDI）实现持久化

                        **排除条件**（同时满足才不视为内存马）：
                        - 组件类名在业务包路径下（如`com.company.*`）
                        - 代码明确用于业务功能（如日志Filter、鉴权Servlet）

                        ## 代码信息
                        代码片段：
                        {code}
                        ## 功能描述
                        {functinDescription}
                        ## 常见的一系列Java恶意代码片段
                        {malware_API}
                        ## 要求
                        严格应用上述规则，若符合1-3中任意一条且不满足排除条件，**必须回答“是内存马”**，否则解释原因。
                        """
                        
                        malwareCheck = client.generate(model=model_name, prompt=prompt_malware_check)
                        print(f"{filename}: {malwareCheck}")

                        if re.search(r'不是\s*内存马', malwareCheck):
                            llm_result = 0
                        elif re.search(r'是\s*内存马', malwareCheck):
                            llm_result = 1
                        else:
                            llm_result = "-1"

                        if llm_result == true_label:
                            correct += 1

                        # 写入 CSV 行
                        csv_writer.writerow([filename, 1, llm_result])

                        
                    except Exception as e:
                        print(e)

        if total > 0:
            accuracy = correct / total
            print(f"\n识别总数：{total}，识别正确：{correct}，准确率：{accuracy:.2%}")
        else:
            print("没有找到可识别的Java文件。")

if __name__ == '__main__':
    main()