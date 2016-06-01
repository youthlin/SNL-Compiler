package com.youthlin.snl.compiler.frontend.symbol;

import com.youthlin.snl.compiler.frontend.lexer.Token;
import com.youthlin.snl.compiler.frontend.parser.TreeNode;

import java.util.List;
import java.util.Map;

/**
 * Created by lin on 2016-06-01-001.
 * 非终极符
 */
public class NonTerminalSymbol extends Symbol {
    public static final NonTerminalSymbol start = new NonTerminalSymbol("Program");
    private final TreeNode node;

    public NonTerminalSymbol(String value) {
        super();
        node = new TreeNode(value);
    }

    /**
     * 产生式，如：
     * <pre>
     *    (1) [Program] -> [ProgramHead] [DeclarePart] [ProgramBody]
     * </pre>
     */
    private Map<Integer, List<Symbol>> production;

    @Override
    public TreeNode getNode() {
        return node;
    }
}
