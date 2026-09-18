package parser;

import parser.exception.PhoxFileNotFoundException;
import parser.exception.PhoxIOException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public final class Phox {
    public static final String PHOX_EXTENSION = ".phox";
    
    private Phox() {
        throw new UnsupportedOperationException("Don't instantiate Phox");
    }
    
    public static void compile(String[] path) {
        Phox.compile(String.join(" ", path));
    }
    
    private static void compile(String path) {
        File main = new File(path);
        if (! main.exists()) {
            throw new PhoxFileNotFoundException(String.format("The file \"%s\" does not exist", path));
        }
        Phox.checkFileType(main, "");
    }
    
    private static void checkFileType(File file, String packageName) {
        if (file.isFile() && file.getName().endsWith(PHOX_EXTENSION)) {
            Phox.handleFile(file, packageName);
        }
        if (file.isDirectory()) {
            Phox.handleDirectory(file, packageName);
        }
    }
    
    private static void handleDirectory(File file, String packageName) {
        File[] children = file.listFiles();
        if (children == null) {return;}
        
        for (File child : children) {
            Phox.checkFileType(child, Phox.getPackageName(file, packageName));
        }
    }
    
    private static String getPackageName(File file, String packageName) {
        String newPackageName = packageName;
        if (!packageName.isEmpty()) {newPackageName += ".";}
        return newPackageName + file.getName();
    }
    
    private static void handleFile(File file, String packageName) {
        final String fileName = file.getName();
        final String completeClassName = packageName + "." + fileName.substring(0, fileName.length() - PHOX_EXTENSION.length());
        
        String fileText = Phox.readFile(file);
        
        CompilerResponse response = Compiler.validate(fileText);
        switch (response) {
            case CompilerResponse.Failed(String reason) ->
                    throw new IllegalArgumentException("Couldn't compile class. Reason: " + reason);
            case CompilerResponse.Success(List<Token> tokens) ->
                    System.out.println(tokens);
        }
    }
    
    private static String readFile(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            return new String(bytes, StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            throw new PhoxIOException(e.getMessage());
        }
    }
}
