package com.youthlin.snl.compiler.frontend.parser.LL1;

import com.youthlin.snl.compiler.frontend.lexer.Token;
import com.youthlin.snl.compiler.frontend.symbol.NonTerminalSymbol;
import com.youthlin.snl.compiler.frontend.symbol.Symbol;
import com.youthlin.snl.compiler.frontend.symbol.TerminalSymbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2016-06-01-001.
 * LL1表
 */
class LL1Table {
    private static final Logger LOG = LoggerFactory.getLogger(LL1Table.class);

    static List<Symbol> find(NonTerminalSymbol nonTerminalSymbol, Token predict) {
        LOG.trace("查表：非终极符-展望符：" + nonTerminalSymbol.getNode().getValue()
                + " " + predict.getValue());
        List<Symbol> l = new ArrayList<>();
        if (nonTerminalSymbol.getNode().getValue().equals("S") && predict.getValue().equals("a")) {
            l.add(new TerminalSymbol(new Token("a")));
            l.add(new NonTerminalSymbol("E"));
            return l;
        }
        if (nonTerminalSymbol.getNode().getValue().equals("E")) {
            if (predict.getValue().equals("a")) {
                l.add(new NonTerminalSymbol("S"));
                l.add(new TerminalSymbol(new Token("d")));
                return l;
            }
            if (predict.getValue().equals("d")) {
                l.add(new TerminalSymbol(new Token("d")));
                return l;
            }
        }
        throw new RuntimeException("Not yet implement");
    }

}
