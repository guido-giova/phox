package parser;

public record SourceFile(
        String packageName,
        String className,
        java.nio.file.Path path
) {}
