# File-Reference-Generator

## 这是什么？

此工具生成指定文件夹中所有文件（包括文件夹）的相对路径字符串引用

## 有何好处？

字符串**字面量**变为字符串**字段引用**

优点如下：

1. 提升资源引用效率
2. 降低资源引用错误概率
3. 运行期错误变成了编译期错误
4. 通过 IDE 得知资源的使用情况

## 如何使用

```
FileReferenceGenerator.generate(
    "E:/LibGdxWorkSpace/Air-Hockey/assets/", // 目标资源根文件夹的绝对路径
    "C:/Users/Ryuu/Documents/File-Reference-Generator/src/main/java/pers/ryuu" // 目标代码文件夹中的位置
);
```

## 详细

### 自动生成类的结构及特点

文件层级结构与文件浏览器一致

层级使用静态内部类实现，因此文件夹中内容使用 . 分隔

保留文件扩展名，使用 _ 符号代替 . 符号

文件夹字段名称以 _folder 结尾

若自动生成的字段名称不合法，将会在字段名称前添加 $ 符号

## 关于
### 联系我
有任何错误或建议，请联系我（ryuu）
### 更新计划
#### 忽略功能
创建 .fileignore 文件，完成文件及文件夹忽略功能
添加不合法字段匹配功能