import csv
import sys
import re
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from DetectVulnByLLM.llm.llm import OllamaClient
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score
import re

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


def analyze_code(file_path, client, model_name):
    with open(file_path, 'r', encoding='utf-8') as file:
        code = file.read()

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
        ## 代码
        {code}
        ##功能描述
        {functinDescription}
        ## 以下是常见的一系列Java恶意代码片段：
        {malware_API}
        ##要求
        如果你觉得代码是java内存马，仅输出“是内存马”即可，如果没有则仅输出“不是内存马”。
        """

        result = client.generate(model=model_name, prompt=prompt_malware_check)
        print(f"{os.path.basename(file_path)}: {result}")


        if re.search(r'不是\s*(Java)?\s*内存马', result, re.IGNORECASE):
            llm_result = 0
        elif re.search(r'是\s*(Java)?\s*内存马', result, re.IGNORECASE):
            llm_result = 1
        else:
            llm_result = -1

        print("----------------------------------------")

        return llm_result
    


def process_folder(folder_path, label, client, model_name, csv_writer, y_true, y_pred):
    for filename in os.listdir(folder_path):
        if filename.endswith(".java") or filename.endswith(".jsp"):
            file_path = os.path.join(folder_path, filename)
            try:
                pred = analyze_code(file_path, client, model_name)
                y_true.append(label)
                y_pred.append(pred)
                csv_writer.writerow([filename, label, pred])
            except Exception as e:
                print(f"[ERROR] 处理 {filename} 失败: {e}")


def main():
    model_name = "llama3:8b-instruct-q8_0"
    version = "Based"
    safe_model_name = re.sub(r'[:\.]', '_', model_name)
    output_csv_path = f"Dataset/output/{safe_model_name}_{version}Output.csv"

    evil_result_folder = "Dataset/EvilClasses"
    simple_result_folder = "Dataset/SimpleCode"

    client = OllamaClient()

    y_true = []
    y_pred = []

    with open(output_csv_path, mode='w', newline='', encoding='utf-8') as csvfile:
        csv_writer = csv.writer(csvfile)
        csv_writer.writerow(['文件名', '标签', 'LLM判断结果'])

        process_folder(evil_result_folder, 1, client, model_name, csv_writer, y_true, y_pred)
        process_folder(simple_result_folder, 0, client, model_name, csv_writer, y_true, y_pred)

    # 过滤掉未能判断的结果（-1）
    filtered = [(t, p) for t, p in zip(y_true, y_pred) if p != -1]
    if not filtered:
        print("没有有效的检测结果")
        return

    y_true_filtered, y_pred_filtered = zip(*filtered)

    acc = accuracy_score(y_true_filtered, y_pred_filtered)
    precision = precision_score(y_true_filtered, y_pred_filtered)
    recall = recall_score(y_true_filtered, y_pred_filtered)
    f1 = f1_score(y_true_filtered, y_pred_filtered)

    print(f"\n总文件数: {len(y_true)}，有效识别数: {len(filtered)}")
    print(f"准确率 (Accuracy): {acc:.2%}")
    print(f"精确率 (Precision): {precision:.2%}")
    print(f"召回率 (Recall): {recall:.2%}")
    print(f"F1 分数: {f1:.2%}")


if __name__ == '__main__':
    main()