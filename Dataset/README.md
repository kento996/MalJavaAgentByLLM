## 数据集说明
EvilClasses中是不同的java漏洞和java CT题目中的利用脚本。
## 构建思路
因为我们的项目现在主要是通过RASP监控，将新加载的类对象字节码dump下来后进行反编译后，
可以获取到反编译后的源代码，然后利用llm来识别这些源代码是否有可能具有恶意行为。因此，
我们需要验证一下llm识别java恶意代码的准确率。

然后目前难以收集大量的java内存马攻击脚本，但是无论是java内存马攻击还是java反序列化
攻击的利用脚本，都是java的类文件。因此，我们可以通过收集不同类型的java漏洞/CTF题目
利用脚本用于验证llm识别java恶意代码的准确率。

## 数据集字段说明
1. `id`: 数据集的id，唯一标识一条数据。
2. `Type`:收集来自于CTF题目还是漏洞利用脚本。（CTF/Vuln）
3. `Code`: java代码
4. `Setting`:是否有配套的环境（方便后面部署测试）

## 注意
对于clss文件使用java-decompiler.jar反编译为java代码后进行存储
使用命令：`java -cp 'java-decompiler.jar的路径' org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler 'clss文件的路径' 'java代码的存储路径'`