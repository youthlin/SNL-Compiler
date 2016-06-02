package com.youthlin.snl.compiler.frontend.parser.LL1;

import com.youthlin.snl.compiler.frontend.lexer.Token;
import com.youthlin.snl.compiler.frontend.symbol.NON_TERMINAL_SYMBOLS;
import com.youthlin.snl.compiler.frontend.symbol.NonTerminalSymbol;
import com.youthlin.snl.compiler.frontend.symbol.Symbol;
import com.youthlin.snl.compiler.frontend.symbol.TerminalSymbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2016-06-01-001.
 * LL1表
 */
class LL1Table {
    private static final Logger LOG = LoggerFactory.getLogger(LL1Table.class);

    static List<Symbol> find(NonTerminalSymbol nonTerminalSymbol, Token predict) {
        return lookUp(nonTerminalSymbol, predict);
    }

    private static List<Symbol> lookUp(NonTerminalSymbol nonTerminalSymbol, Token predict) {
        String value = nonTerminalSymbol.getValue();
        LOG.trace("查表 非终极符=" + value + ", 展望符=" + predict.getValue());
        NON_TERMINAL_SYMBOLS symbols = NON_TERMINAL_SYMBOLS.valueOf(value);
        return symbols.find(predict);
    }
}
