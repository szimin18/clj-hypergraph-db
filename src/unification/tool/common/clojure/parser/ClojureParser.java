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
        Map<String, IPersistentVector> namespaseAssociatedMap = parsedFilesMap.get(namespaceUsedForParsing);
        if (!namespaseAssociatedMap.containsKey(nameOfFileToParse)) {
            namespaseAssociatedMap.put(nameOfFileToParse,
                    (IPersistentVector) RT.var(namespaceUsedForParsing, "evaluate").invoke(nameOfFileToParse));
        }
        return namespaseAssociatedMap.get(nameOfFileToParse);
    }

    // TODO: Find better way to do this
    public String getTypeFromFile(String filePath) {
        return ((Keyword) RT.second(RT.readString(RT.var("clojure.core", "slurp")
                .invoke(filePath).toString()))).getName();
    }
}
