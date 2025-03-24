from ollama import Client
from typing import Dict, Any, List, Optional, Union

class OllamaClient:
    def __init__(self, host: str = "http://192.168.34.11:11434", headers: Optional[Dict[str, str]] = None):
        """
        初始化Ollama客户端
        
        Args:
            host: Ollama服务的URL，默认为服务地址
            headers: 请求头信息
        """
        self.host = host
        self.headers = headers or {}
        # 使用Client类初始化
        self.client = Client(host=host, headers=self.headers)
    
    def generate(self, 
                model: str, 
                prompt: str, 
                system: Optional[str] = None,
                temperature: float = 0.7,
                top_p: Optional[float] = None,
                top_k: Optional[int] = None,
                max_tokens: Optional[int] = None) -> str:
        """
        使用Ollama生成文本
        
        Args:
            model: 要使用的模型名称
            prompt: 用户提示
            system: 系统提示
            temperature: 温度参数，控制随机性
            top_p: 控制输出多样性
            top_k: 词汇筛选数量
            max_tokens: 最大输出token数量
            
        Returns:
            生成的文本响应
        """
        options = {
            "temperature": temperature
        }
        
        if top_p is not None:
            options["top_p"] = top_p
        if top_k is not None:
            options["top_k"] = top_k
        if max_tokens is not None:
            options["num_predict"] = max_tokens
        
        try:
            # 使用Client实例的generate方法
            response = self.client.generate(
                model=model,
                prompt=prompt,
                system=system,
                options=options
            )
            return response.get('response', '')
        except Exception as e:
            print(f"请求错误: {e}")
            return f"错误: {str(e)}"
    
    def chat(self, 
            model: str, 
            messages: List[Dict[str, str]], 
            system: Optional[str] = None,
            temperature: float = 0.7) -> Dict[str, Any]:
        """
        使用Ollama进行聊天对话
        
        Args:
            model: 要使用的模型名称
            messages: 消息列表，格式为[{"role": "user"/"assistant", "content": "消息内容"}]
            system: 系统提示
            temperature: 温度参数
            
        Returns:
            聊天响应的JSON对象
        """
        options = {
            "temperature": temperature
        }
        
        try:
            # 使用Client实例的chat方法
            response = self.client.chat(
                model=model,
                messages=messages,
                system=system,
                options=options
            )
            return response
        except Exception as e:
            print(type(e))
            print(f"请求错误: {e}")
            return {"error": str(e)}

# 简单使用示例
def test_ollama(model_name: str = "qwen2.5:latest", prompt: str = "你好，请介绍一下自己") -> str:
    """
    测试Ollama模型的简单函数
    
    Args:
        model_name: 要使用的模型名称
        prompt: 测试提示
    
    Returns:
        模型的响应
    """
    client = OllamaClient()
    return client.generate(model=model_name, prompt=prompt)
    
if __name__ == '__main__':
    print(test_ollama())