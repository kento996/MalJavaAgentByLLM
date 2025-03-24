import os

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
## 灰色逻辑代码片段，即可能被正常代码使用，也高度可能被恶意代码使用的片段
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
```
"""
def main():
    model_name="qwen2.5:latest"
    client = OllamaClient()
    class_result_folder = "../classResult"

    for filename in os.listdir(class_result_folder):
        if filename.endswith(".java"):
            with open(os.path.join(class_result_folder, filename), 'r',encoding='utf-8') as file:
                try:
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
                    你是一位安全代码审计专家，如下是一段java代码片段的功能描述和源代码，首先请你检查代码中是否有常见的恶意代码会使用的API，然后判断如下代码是否是恶意的Java代码：
                    ## 代码
                    {code}
                    ##功能描述
                    {functinDescription}
                    ## 常见的恶意代码会使用的API列表：
                    {malware_API}
                    ##要求
                    如果你觉得代码有恶意性，仅输出“有恶意性”即可，如果没有则仅输出“没有恶意性”
                    """
                    malwareCheck = client.generate(model=model_name, prompt=prompt_malware_check)
                    print(malwareCheck)

                except Exception as e:
                    print(e)



if __name__ == '__main__':
    main()