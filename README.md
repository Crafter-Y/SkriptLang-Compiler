# SkriptLang-Compiler

This project aims to compile the "code" files designed for the Minecraft Plugin [Skript](https://github.com/SkriptLang/Skript) into a Java Paper Plugin. I know that I will never completely implement the Skript specifications und I also know that I will definitely overengineer some parts of my application. It's fine, this is just a side Project, that I can get distracted with.

## Usage

To "compile" something:

```bash
java -jar skriptlangcompiler-1.0-shaded.jar ./inputfolder ./outputfolder (seed)
```

The input folder should contain individual files with `.sk` types. Folders and files prefixed with `-` will be ignored.

To generate coverage data: (requires graphviz)

```bash
java -jar skriptlangcompiler-1.0-shaded.jar status
```
