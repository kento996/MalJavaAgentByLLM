import pathlib
import subprocess
import sys
import os
import argparse
import time
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler

# 指定自定义java-decompiler的路径
self_java_decompiler_path = r'/Tools/java-decompiler.jar'

# 用来校验本地的java_decompiler路径是否正确
def verify_decompiler(my_path):
    if pathlib.Path("Tools/java-decompiler.jar").is_file():
        return "./java-decompiler.jar"
    elif pathlib.Path(my_path).is_file():
        return my_path
    return False

# 反编译单个class文件
def decompile_single_class(class_file, output_folder):
    # 确保输出文件夹存在
    pathlib.Path(output_folder).mkdir(parents=True, exist_ok=True)
    
    # 获取相对路径，保持输出目录结构
    class_path = pathlib.Path(class_file)
    
    print(f"开始反编译: {class_file}")
    
    # 调用java-decompiler进行反编译
    _sub = subprocess.getstatusoutput('java -cp "{}" org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler -dgs=true {} {}'.format(
        java_decompiler_path, class_file, output_folder))
    
    if _sub[0] != 0:
        print(_sub[1])
        print(f"反编译 {class_file} 失败......")
        return False
    
    # 获取预期的输出文件路径
    java_file_name = class_path.stem + ".java"
    expected_java_file = pathlib.Path(output_folder) / java_file_name
    
    if expected_java_file.exists():
        print(f"反编译完成: {expected_java_file}")
        return True
    else:
        print(f"未找到预期的输出文件: {expected_java_file}")
        return False

# 反编译指定文件夹中的所有class文件
def decompile_class_files(class_folder, output_folder):
    # 确保输出文件夹存在
    pathlib.Path(output_folder).mkdir(parents=True, exist_ok=True)
    
    # 获取所有.class文件
    class_files = list(pathlib.Path(class_folder).glob('**/*.class'))
    
    if not class_files:
        print(f"在 {class_folder} 中未找到任何 .class 文件")
        return False
    
    print(f"找到 {len(class_files)} 个 .class 文件，开始反编译...")
    
    # 调用java-decompiler进行反编译
    _sub = subprocess.getstatusoutput('java -cp "{}" org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler -dgs=true {} {}'.format(
        java_decompiler_path, class_folder, output_folder))
    
    if _sub[0] != 0:
        print(_sub[1])
        print("java_decompiler 执行失败......")
        return False
    
    print(f"反编译完成，结果保存在 {output_folder}")
    return True

# 文件系统事件处理器
class ClassFileHandler(FileSystemEventHandler):
    def __init__(self, class_folder, output_folder):
        self.class_folder = class_folder
        self.output_folder = output_folder
        self.class_folder_path = pathlib.Path(class_folder)
        # 记录已处理的文件，避免重复处理
        self.processed_files = set()
        
    def on_created(self, event):
        if event.is_directory:
            return
        
        file_path = pathlib.Path(event.src_path)
        if file_path.suffix.lower() == '.class' and str(file_path) not in self.processed_files:
            print(f"检测到新的class文件: {file_path}")
            self.processed_files.add(str(file_path))
            
            # 计算相对路径，以保持输出目录结构
            try:
                rel_path = file_path.relative_to(self.class_folder_path)
                target_dir = pathlib.Path(self.output_folder) / rel_path.parent
                target_dir.mkdir(parents=True, exist_ok=True)
                
                # 反编译单个文件
                decompile_single_class(str(file_path), str(target_dir))
            except ValueError:
                # 如果无法计算相对路径，直接反编译到输出根目录
                decompile_single_class(str(file_path), self.output_folder)
    
    def on_modified(self, event):
        # 对于修改的class文件，也进行反编译
        if not event.is_directory:
            file_path = pathlib.Path(event.src_path)
            if file_path.suffix.lower() == '.class':
                print(f"检测到class文件被修改: {file_path}")
                self.on_created(event)  # 复用创建事件的处理逻辑

# 监控文件夹变化
def monitor_folder(class_folder, output_folder):
    event_handler = ClassFileHandler(class_folder, output_folder)
    observer = Observer()
    observer.schedule(event_handler, class_folder, recursive=True)
    observer.start()
    
    print(f"开始监控文件夹: {class_folder}")
    print(f"反编译结果将保存到: {output_folder}")
    print("按 Ctrl+C 停止监控...")
    
    try:
        # 首先反编译现有的所有class文件
        decompile_class_files(class_folder, output_folder)
        
        # 然后持续监控新文件
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        observer.stop()
    observer.join()

def main():
    epilog = r'''示例:
    python3 class_decompiler.py -c /path/to/class/folder -o /path/to/output/folder
    '''
    
    parser = argparse.ArgumentParser(epilog=epilog, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument('-c', '--class_folder', help='包含 .class 文件的文件夹路径', required=True)
    parser.add_argument('-o', '--output', help='反编译结果输出路径')
    args = parser.parse_args()
    
    # 指定源class文件夹路径
    class_folder = args.class_folder
    
    # 如果未指定输出路径，则在class文件夹旁边创建一个新文件夹
    if args.output:
        output_folder = args.output
    else:
        output_folder = str(pathlib.Path(class_folder).parent / f"{pathlib.Path(class_folder).name}_decompiled_{int(time.time())}")
    
    # 验证java-decompiler.jar路径
    global java_decompiler_path
    java_decompiler_path = verify_decompiler(self_java_decompiler_path)
    if java_decompiler_path is False:
        sys.exit("请在当前目录存放java-decompiler.jar，或者通过self_java_decompiler_path指定自定义路径")
    
    # 开始监控文件夹
    monitor_folder(class_folder, output_folder)

if __name__ == "__main__":
    main() 