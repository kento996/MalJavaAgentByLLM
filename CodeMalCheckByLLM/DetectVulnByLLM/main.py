import csv
import sys
import re
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from DetectVulnByLLM.llm.llm import OllamaClient

malware_API="""
如下是常见的一些可能会出现在恶意java利用脚本中的API：
## 各种种类的内存马的重要实现过程
```
(StandardContext)
.createWrapper()
.addFilterDef(
.addURLPattern(
.addFilterMapBefore(
(WebApplicationContext)
.registerMapping(
adaptedInterceptors.
DefaultWebFilterChain(
(StandardPipeline)
httpUpgradeProtocols
.setExecutor(
addAfterServiceListener
.addServletMappingDecoded(
```
## 恶意逻辑会用到的API
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
                        你是一位安全代码审计专家，如下是一段java代码片段，请你简要分析其代码的功能：
                        ## 代码
                        ```
                        {code}
                        ```
                        ## 要求
                        请输出一段50字以内的功能描述
                        """
                        functinDescription=client.generate(model=model_name, prompt=prompt_function)
                        prompt_malware_check=f"""
                        内存马是指通过类加载器、反射、动态代理、Servlet 过滤器、监听器等技术，在不落地文件的情况下，在服务器内存中建立持久化控制通道的恶意代码。
                        你是一位精通Java安全的分析专家，如下是一段java代码片段的功能描述和源代码，请你判断如下代码是否是Java内存马代码：
                        ：
                        ## 代码
                        {code}
                        ## 功能描述
                        {functinDescription}
                        ## 常见的java恶意API
                        {malware_API}
                        ## 要求
                        如果你觉得代码是java内存马，仅输出“是内存马”即可，如果没有则仅输出“不是内存马”。
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