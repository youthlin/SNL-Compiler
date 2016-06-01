package com.youthlin.snl.compiler.frontend.parser.LL1;

import com.youthlin.snl.compiler.frontend.lexer.Token;
import com.youthlin.snl.compiler.frontend.lexer.TokenType;
import com.youthlin.snl.compiler.frontend.parser.SyntaxParser;
import com.youthlin.snl.compiler.frontend.parser.ParseResult;
import com.youthlin.snl.compiler.frontend.parser.SyntaxTree;
import com.youthlin.snl.compiler.frontend.parser.TreeNode;
import com.youthlin.snl.compiler.frontend.symbol.NonTerminalSymbol;
import com.youthlin.snl.compiler.frontend.symbol.Symbol;
import com.youthlin.snl.compiler.frontend.symbol.TerminalSymbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by lin on 2016-05-28-028.
 * LL(1)语法分析
 */
public class LL1Parser extends SyntaxParser {
    private static final Logger LOG = LoggerFactory.getLogger(LL1Parser.class);

    @Override
    public ParseResult parse(List<Token> tokenList) {
        list = tokenList;
        errors = new ArrayList<>();
        Stack<Symbol> stack = new Stack<>();
        final NonTerminalSymbol start = new NonTerminalSymbol("S");
        //语法树根结点
        final TreeNode root = start.getNode();
        ParseResult result = new ParseResult();

        stack.push(start); //文法开始符压栈

        Symbol symbol;
        while (!stack.empty() && peekToken().getType() != TokenType.EOF) {
            symbol = stack.pop();
            //是终极符，则应该匹配
            if (symbol instanceof TerminalSymbol) {
                match(((TerminalSymbol) symbol).getToken().getType());
            }
            //是非终极符，查LL1表，扩展语法树节点，产生式右部逆序入栈
            else if (symbol instanceof NonTerminalSymbol) {
                NonTerminalSymbol non = (NonTerminalSymbol) symbol;
                List<Symbol> productionRight = LL1Table.find(non, peekToken());
                if (productionRight != null) {
                    int size = productionRight.size();
                    TreeNode[] children = new TreeNode[size];
                    for (int i = 0; i < size; i++) {
                        children[i] = productionRight.get(i).getNode();
                        Symbol s = productionRight.get(size - 1 - i);
                        //epsilon不用入栈
                        if (!(s instanceof TerminalSymbol) || !((TerminalSymbol) s).isEpsilon())
                            stack.push(productionRight.get(size - 1 - i));
                    }
                    non.getNode().setChildren(children);
                } else errors.add("查表出错");
            } else {
                errors.add("未识别的符号,这个错误不应该出现");
            }
        }

        //最后格局应为()(#)
        if (!stack.empty() || getToken().getType() != TokenType.EOF) {
            errors.add("Source code too long.");
        }

        result.setTree(new SyntaxTree(root));
        result.setErrors(errors);
        return result;
    }

}
