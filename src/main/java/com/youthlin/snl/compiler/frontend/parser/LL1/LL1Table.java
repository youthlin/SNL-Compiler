package com.youthlin.snl.compiler.frontend.parser.LL1;

import com.youthlin.snl.compiler.frontend.lexer.Token;
import com.youthlin.snl.compiler.frontend.symbol.NonTerminalSymbol;
import com.youthlin.snl.compiler.frontend.symbol.Symbol;
import com.youthlin.snl.compiler.frontend.symbol.TerminalSymbol;

import java.util.List;

/**
 * Created by lin on 2016-06-01-001.
 * LL1表
 */
class LL1Table {
    static List<Symbol> find(NonTerminalSymbol nonTerminalSymbol, Token token) {
        throw new RuntimeException("Not yet implement");
    }

    public void createLL1Table() {
        final TerminalSymbol epsilon = TerminalSymbol.epsilon;
    }
}
