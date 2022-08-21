# File-Path-Reference-Generator

[![](https://jitpack.io/v/Ryuu-64/File-Path-Reference-Generator.svg)](https://jitpack.io/#Ryuu-64/File-Path-Reference-Generator)

## 这是什么？

此工具生成指定文件夹中所有文件的相对路径字符串引用 .java 文件

## 有何好处？

字符串**字面量**变为字符串**字段引用**的优点如下：

1. 提升资源引用效率
2. 降低资源引用错误概率
3. 运行期错误变成了编译期错误
4. 通过 IDE 得知资源的使用情况

## 如何使用

1. executable jar

   下载 [生成器](https://github.com/Ryuu-64/File-Path-Reference-Generator/releases/)

   输入指定的参数：

   1. 指定的根目录路径
   2. 文件路径引用脚本的生成路径
   3. 包名 (可选，若包名以 com 或 org 开头，将在输入生成路径时自动填入)
   4. 文件路径引用脚本的名称 (可选，默认名称为 FilePathReference.java)

   按下 Generate 按钮

2. Gradle

   在 build.gradle 中写入以下代码

   ```java
   buildscript {
       repositories {
           maven { url 'https://jitpack.io' }
   
       }
       dependencies {
           classpath 'com.github.Ryuu-64:File-Path-Reference-Generator:Tag' // 输入您需要的 Tag
       }
   }
   
   import org.ryuu.file_path_reference_generator.core.Generator
   
   task createFilePathReference {
       doLast {
           Generator.generate("文件根目录", "脚本的生成路径", "脚本的包名")
       }
   }
   ```

   运行 createFilePathReference

   若需要自定义脚本名称请使用如下 API：
   
   ```java
   Generator.generate("文件根目录", "脚本的生成路径", "脚本的包名", "脚本名称")
   ```

### 配置生成忽略

在根目录的文件夹中新建 .fileignore 文件

注释：以 \# 开头

通配符： \*

不忽略：以 ! 开头

忽略：文件的任意相对路径

## 详细

### 自动生成类的结构及特点

文件层级结构与文件浏览器一致，文件路径字段名与实际文件名一致，但存在以下例外：

1. 文件夹路径引用以 $directory 为后缀 (字段名与静态内部类名一致在引用时容易引发歧义)
2. 文件名不是合法的字段名
   1. 以数字开头的文件字段名前添加 $ 符号
   2. 其他不合法的字符被替换为 _
3. 路径生成器将会自动识别文件后缀名，生成一个以 $suffix 为名的静态内部类记录所有的文件后缀名

## 关于

### 联系我

有任何错误或建议，请联系[我 (Ryuu)](64ryuu@gmail.com)
