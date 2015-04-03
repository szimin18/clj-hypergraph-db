package unification.tool;

import unification.tool.common.clojure.parser.ClojureParser;

import java.io.IOException;

public class UnificationTool {
    private void run() throws IOException {
        System.out.println("run");

        ClojureParser.getInstance().parse();
    }

    public static void main(String[] args) throws IOException {
        UnificationTool unificationTool = new UnificationTool();
        unificationTool.run();
    }
}
