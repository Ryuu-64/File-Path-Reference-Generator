# Path-Generator

[![](https://jitpack.io/v/Ryuu-64/File-Path-Reference-Generator.svg)](https://jitpack.io/#Ryuu-64/File-Path-Reference-Generator)

[简体中文](https://github.com/Ryuu-64/File-Path-Reference-Generator/blob/main/README_zh-cn.md)

## What is this?

This tool generates relative path string references to all files in the specified folder .java.

## What are the benefits?

The advantages of string **literal** to string **field reference** are as follows:

1. Improve resource citation efficiency
2. Reduce the probability of resource reference errors
3. Runtime errors become compile-time errors
4. Know the resource usage through the IDE

## How to use

1. executable jar

   Download [Generator](https://github.com/Ryuu-64/File-Path-Reference-Generator/releases/)

   Enter the specified parameters:

   1. The specified root directory path
   2. The file path refers to the generation path of the script
   3. Package name (optional, if the package name starts with com or org, it will be filled in automatically when entering the generated path)
   4. The name of the file path reference script (optional, the default name is FilePathReference.java)

   Press the Generate button

2. Gradle

   Write the following code in build.gradle

   ````java
   buildscript {
       repositories {
           maven { url 'https://jitpack.io' }
   
       }
       dependencies {
           classpath 'com.github.Ryuu-64:File-Path-Reference-Generator:Tag' // Enter the Tag you need
       }
   }
   
   import org.ryuu.pathgenerator.Generator
   
   task createFilePathReference {
       doLast {
           Generator.generate("file root directory path", "script generation path", "script package name")
       }
   }
   ````

   Run the createFilePathReference task

   If you need to customize the script name, please use the following API:

   ```java
   Generator.generate("file root directory path", "script generation path", "script package name", "script name")
   ```

## Details

### Structure and characteristics of automatically generated classes

The file hierarchy is the same as the file browser, and the file path field name is the same as the actual file name, with the following exceptions:

1. The folder path field names have a $ prefix
2. The filename is not a valid field name
   1. File field names starting with a number or named [java keywords, reserved words, or special literals](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html) have $ prefix
   2. Other illegal characters are replaced with _
3. The path generator will record the file suffix and generate a static inner class named $SUFFIX at the end

## About

### Contact me

Any errors or suggestions, please contact [me (Ryuu)](64ryuu@gmail.com)