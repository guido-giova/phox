package parser;

import java.util.List;
import java.util.Map;

public final class TypeResolver {
    private final Map<String, List<Token>> classesAndTokens;
    
    private TypeResolver(String path) {
        this.classesAndTokens = TypeResolver.mapFiles(path);
    }
    
    public static void resolve(String[] args) {
        String joinedArgs = String.join(" ", args);
        TypeResolver resolver = new TypeResolver(joinedArgs);
        
    }
    
    private static Map<String, List<Token>> mapFiles(String path) {
        return Classifier.compilePath(path)
                         .stream()
                         .collect(java.util.stream.Collectors.toMap(
                                 TypeDefinition::typeName,
                                 TypeDefinition::tokens
                         ));
    }
    
    
}
