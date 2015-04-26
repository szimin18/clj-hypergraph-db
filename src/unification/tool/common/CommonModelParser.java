package unification.tool.common;

import clojure.lang.*;

import java.util.ArrayList;
import java.util.List;

public class CommonModelParser {
    private static final CommonModelParser COMMON_MODEL_PARSER = new CommonModelParser();

    public static CommonModelParser getInstance() {
        return COMMON_MODEL_PARSER;
    }

    public List<Object> findAllItemsFromMapValueByType(Object iPersistentMap, String keywordString,
                                                       String typeKeyword) {
        return findAllItemsByType(seqableFromMap(iPersistentMap, keywordString), typeKeyword);
    }

    public List<Object> findAllItemsByType(Seqable seqable, String typeKeyword)
            throws IllegalArgumentException {
        List<Object> result = new ArrayList<>();

        if (seqable != null) {
            ISeq seq = seqable.seq();

            while (seq != null && seq.count() != 0) {
                Object first = seq.first();

                Object keyword = RT.get(first, Keyword.intern("type"));

                String typeString = ((Keyword) keyword).getName();

                if (typeKeyword.equals(typeString)) {
                    result.add(first);
                }

                seq = seq.more();
            }
        }

        return result;
    }

    public Seqable seqableFromMap(Object iPersistentMap, String keywordString) {
        return (Seqable) ((IPersistentMap) iPersistentMap).valAt(Keyword.intern(keywordString));
    }

    public Object objectFromMap(Object iPersistentMap, String keywordString) {
        return ((IPersistentMap) iPersistentMap).valAt(Keyword.intern(keywordString));
    }

    public String keywordNameFromMap(Object iPersistentMap, String keywordString) {
        return ((Keyword) ((IPersistentMap) iPersistentMap).valAt(Keyword.intern(keywordString))).getName();
    }

    public String stringFromMap(Object iPersistentMap, String keywordString) {
        return ((IPersistentMap) iPersistentMap).valAt(Keyword.intern(keywordString)).toString();
    }

    public boolean booleanFromMap(Object iPersistentMap, String keywordString) {
        return (boolean) ((IPersistentMap) iPersistentMap).valAt(Keyword.intern(keywordString));
    }

    public IPersistentVector vectorFromMap(Object iPersistentMap, String keywordString) {
        return (IPersistentVector) ((IPersistentMap) iPersistentMap).valAt(Keyword.intern(keywordString));
    }
}