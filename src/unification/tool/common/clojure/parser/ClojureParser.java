package unification.tool.common.clojure.parser;

import clojure.lang.IPersistentVector;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Symbol;

import java.util.Map;
import java.util.WeakHashMap;

public class ClojureParser {
    public static final ClojureParser CLOJURE_PARSER = new ClojureParser();

    public static final Map<String, Map<String, IPersistentVector>> parsedFilesMap = new WeakHashMap<>();

    private ClojureParser() {
    }

    public static ClojureParser getInstance() {
        return CLOJURE_PARSER;
    }

    public IPersistentVector parse(String namespaceUsedForParsing, String nameOfFileToParse) {
        if (!parsedFilesMap.containsKey(namespaceUsedForParsing)) {
            RT.var("clojure.core", "require").invoke(Symbol.intern(namespaceUsedForParsing));
            parsedFilesMap.put(namespaceUsedForParsing, new WeakHashMap<>());
        }
        Map<String, IPersistentVector> namespaceAssociatedMap = parsedFilesMap.get(namespaceUsedForParsing);
        if (!namespaceAssociatedMap.containsKey(nameOfFileToParse)) {
            namespaceAssociatedMap.put(nameOfFileToParse,
                    (IPersistentVector) RT.var(namespaceUsedForParsing, "evaluate").invoke(nameOfFileToParse));
        }
        return namespaceAssociatedMap.get(nameOfFileToParse);
    }

    public String getTypeFromFile(String filePath) {
        RT.var("clojure.core", "require").invoke(Symbol.intern("unification.tool.common.clojure.functions"));
        Object modelTypeObject = RT.var("unification.tool.common.clojure.functions", "find-file-type").invoke(filePath);

        if (modelTypeObject != null && modelTypeObject instanceof Keyword) {
            return ((Keyword) modelTypeObject).getName();
        } else {
            throw new IllegalArgumentException("no model type provided");
        }
    }
}
