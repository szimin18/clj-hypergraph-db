package unification.tool.common.clojure.parser;

import clojure.lang.IFn;
import clojure.lang.RT;

import java.io.IOException;

public class ClojureParser {
    public static final ClojureParser CLOJURE_PARSER = new ClojureParser();

    private ClojureParser() {
    }

    public static ClojureParser getInstance() {
        return CLOJURE_PARSER;
    }

    public void parse() throws IOException {
        //        RT.loadResourceScript("unification/tool/common/clojure/parser/files/clj/run_config_parser.clj");
        //        Var hdm = RT.var("run_config_parser", "hdm");
        //        Object invokeResult = hdm.invoke("asd");
        //        System.out.println(invokeResult);

        IFn f = (IFn) RT.var("clojure.core", "println");
        f.invoke("hello clojure");


        RT.var("clojure.core", "eval").invoke(RT.var("clojure.core", "read-string").invoke(
                "(use 'unification.tool.common.clojure.parser.files.clj.run_config_parser)"));
        //        IFn fn = (IFn) RT.var("test.clojure.core", "hello-world");
        //        Object result = fn.invoke("test");

    }
}
