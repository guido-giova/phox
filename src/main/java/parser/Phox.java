package parser;

import parser.exception.PhoxFileNotFoundException;
import parser.exception.PhoxIOException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Main class from which to compile Phox files.
 */
public final class Phox {
    /**
     * The extension that Phox files have
     */
    public static final String PHOX_EXTENSION = ".phox";
    
    private Phox() {
        throw new UnsupportedOperationException("Don't instantiate Phox");
    }
    
    /**
     * Call directly with args from main
     *
     * @param path list of paths given through main
     */
    public static void compile(String[] path) {
        Coordinator.coordinate(
                Arrays.stream(path)
                      .map(Path::of)
                      .map(Phox::compile)
                      .flatMap(Collection::stream)
                      .toList()
        );
    }
    
    private static List<FileDefinition> compile(Path path) {
        if (Files.notExists(path)) {
            throw new PhoxFileNotFoundException(path);
        }
        return Phox.checkFileType(path, "");
    }
    
    private static List<FileDefinition> checkFileType(Path path, String packageName) {
        if (Files.isRegularFile(path) && path.getFileName().toString().endsWith(PHOX_EXTENSION)) {
            return List.of(Phox.handleFile(path, packageName));
        }
        if (Files.isDirectory(path)) {
            return Phox.handleDirectory(path, packageName);
        }
        return List.of();
    }
    
    private static List<FileDefinition> handleDirectory(Path path, String packageName) {
        String newPackageName = Phox.getPackageName(path, packageName);
        try (java.util.stream.Stream<Path> children = Files.list(path)) {
            return children.flatMap(c -> Phox.checkFileType(c, newPackageName).stream())
                           .toList();
        } catch (IOException e) {
            throw new PhoxIOException(e);
        }
    }
    
    private static String getPackageName(Path path, String packageName) {
        String newPackageName = packageName;
        if (!packageName.isEmpty()) {newPackageName += ".";}
        return newPackageName + path.getFileName();
    }
    
    private static String readFile(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            throw new PhoxIOException(e);
        }
    }
    
    private static FileDefinition handleFile(Path path, String packageName) {
        final String fileName = path.getFileName().toString();
        final String className = fileName.substring(0, fileName.length() - PHOX_EXTENSION.length());
        final String fileText = Phox.readFile(path);
        final SourceFile source = new SourceFile(packageName, className, path);
        return new FileDefinition(source, fileText);
    }
}
