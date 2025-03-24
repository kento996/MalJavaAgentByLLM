# DetectVulnByLLM

这个项目使用大语言模型(LLM)来辅助检测代码中的安全漏洞。

## 项目结构

```
DetectVulnByLLM/
├── llm/                     # LLM相关功能包
│   ├── __init__.py          # 包初始化文件
│   └── llm.py               # 实现对Ollama模型的调用
├── main.py                  # 主程序，用于测试LLM功能
├── requirements.txt         # 项目依赖
└── README.md                # 项目说明
```

## 安装

1. 确保已安装Python 3.8或更高版本
2. 安装依赖项：

```bash
pip install -r requirements.txt
```

3. 确保已安装并运行Ollama服务（默认在 http://192.168.34.11:11434）

## 使用方法

运行主程序来测试与Ollama模型的交互：

```bash
python main.py
```

## 功能

- `OllamaClient`: 提供与Ollama模型交互的客户端类
  - `generate()`: 使用模型生成文本
  - `chat()`: 使用模型进行对话
- 简单的测试函数，展示如何使用客户端

## 实现细节

- 使用官方的`ollama`库中的`Client`类与Ollama服务通信
- 支持自定义请求头，便于API认证和其他自定义需求
- 支持配置模型参数，如温度、top_p和top_k等
- 默认使用`qwen2.5:latest`模型，可根据需要修改 