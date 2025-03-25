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
    model_name="qwen2.5:latest"
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

                        prompt_function=f"""
                        你是一位安全代码审计专家，如下是一段可能恶意或被恶意行为滥用的java代码片段，这段代码在做什么？请简要分析其代码的功能：
                        ## 代码
                        ```
                        {code}
                        ```
                        ## 要求
                        请输出一段尽量简要的功能描述，强调是否动态注入类或组件，并强调该类或组件是否恶意或者存在恶意的可能性
                        """
                        functinDescription=client.generate(model=model_name, prompt=prompt_function)
                        
                        print(f"description {filename}:"+"-"*30)
                        print(functinDescription)
                        print(f"description {filename} end:"+"-"*30)
                        for i in range(3):
                            print()
                        
                        prompt_malware_check=f"""
                        Java内存马（或名Java Webshell）是一种驻留在内存中的程序，通过利用Java高级特性（继承、反射等）篡改Java应用组件（如Servlet、Filter、Interceptor等）、类加载器、中间件等，实现在运行时动态注入恶意代码。
                        你是一位精通Java安全的分析专家，如下是一段java代码片段的功能描述和源代码，请你判断如下代码是否是Java内存马代码：
                        **注意：存在恶意类的动态注入的即为内存马。可能本身并无直接恶意逻辑，但只要存在可能被用于恶意类或恶意组件动态注入的潜在恶意用途的代码就可视作内存马**
                        
                        ## 代码
                        {code}
                        ## 功能描述
                        {functinDescription}
                        ## 常见的Java恶意代码片段（Java内存马可能会含有这些代码，当然也可能有其它未列出的方法）
                        {malware_API}
                        ## 要求
                        如果该代码是Java内存马，**仅输出“是内存马”即可**，如果没有，**解释一下该代码为什么不是Java内存马**。
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