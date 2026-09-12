package parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public final class Classifier {
    private Classifier() {
        throw new UnsupportedOperationException("Don't instantiate Classifier");
    }
    
    public static void compilePath(String[] path) {
        Classifier.compilePath(String.join(" ", path));
    }
    
    private static void compilePath(String path) {
        File main = new File(path);
        if (! main.exists()) {
            System.err.println("File not exists");
            System.exit(1);
        }
        Classifier.checkFileType(main, "");
    }
    
    private static void checkFileType(File file, String packageName) {
        if (file.isFile() && file.getName().endsWith(".phox")) {
            Classifier.handleFile(file, packageName);
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null) { return; }
            
            for (File child : children) {
                Classifier.checkFileType(child, Classifier.getPackageName(file, packageName));
            }
        }
    }
    
    private static String getPackageName(File file, String packageName) {
        String newPackageName = packageName;
        if (!packageName.isEmpty()) {newPackageName += ".";}
        return newPackageName + file.getName();
    }
    
    private static void handleFile(File file, String packageName) {
        final String fileName = file.getName();
        final String completeClassName = packageName + "." + fileName.substring(0, fileName.length() - ".phox".length());
        
        String fileText = Classifier.readFile(file);
        
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
            throw new RuntimeException(e);
        }
    }
}
