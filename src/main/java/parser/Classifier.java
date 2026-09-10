package parser;

import java.io.File;

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
        System.out.println(packageName + "." + file.getName());
    }
}
