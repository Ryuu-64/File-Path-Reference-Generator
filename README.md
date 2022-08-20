# File-Path-Reference-Generator

[简体中文](https://github.com/Ryuu-64/File-Path-Reference-Generator/blob/main/README_zh-cn.md)

## what is this?

This tool generates relative path string references to all files in the specified folder .java.

## What are the benefits?

The advantages of string **literal** to string **field reference** are as follows:

1. Improve resource citation efficiency
2. Reduce the probability of resource reference errors
3. Runtime errors become compile-time errors
4. Know the resource usage through the IDE

## how to use

### Generate reference file

download the [generator](https://github.com/Ryuu-64/File-Path-Reference-Generator/releases/download/0.1.0/File-Path-Reference-Generator-0.1.0.jar)

Enter the specified parameters

1. The specified root directory path
2. The file path refers to the generation path of the script
3. Package name (optional, if the registration starts with com or org, it will be filled in automatically when entering the generation path)
4. The name of the file path reference script (optional, the default name is FilePathReference.java)

Press Generate button

### Configure generation to ignore

Create a new .fileignore file in the root folder

Comment: starts with \#

Wildcard: \*

Don't ignore: start with !

Ignore: any relative path to the file

## details

### Structure and characteristics of automatically generated classes

The file hierarchy is the same as the file browser, and the file path field name is the same as the actual file name, with the following exceptions:

1. The folder path reference is suffixed with $directory (the field name is consistent with the static inner class name, which is easy to cause ambiguity when referencing)
2. The filename is not a valid field name
   1. Add a $ sign before the file field name that starts with a number
   2. Other illegal characters will be replaced by _
3. The path generator will automatically identify the file suffix and generate a static inner class named $suffix to record all file suffixes

## about

### contact me

Any errors or suggestions, please contact [me (Ryuu)](2357622935@qq.com)