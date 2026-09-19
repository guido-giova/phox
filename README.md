# Phox language

## Class Compiler

Given a list of directories or files, it reads, scans, tokenizes, and creates the abstract syntax tree.

## Execution

Create a class with a `public static void main(String[] args)` and call `parser.Phox.compile(args);`. Then, in a
terminal, run:

```bash
java <yourApp> <paths>
```

for example:

```bash
java Main "./phox" ",/other/files"
```
