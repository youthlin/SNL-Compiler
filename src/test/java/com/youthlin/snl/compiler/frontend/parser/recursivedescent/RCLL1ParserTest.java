package com.youthlin.snl.compiler.frontend.parser.recursivedescent;

import com.youthlin.snl.compiler.frontend.lexer.Lexer;
import com.youthlin.snl.compiler.frontend.lexer.Token;
import com.youthlin.snl.compiler.frontend.lexer.TokenType;
import com.youthlin.snl.compiler.frontend.parser.LL1.LL1Parser;
import com.youthlin.snl.compiler.frontend.parser.SyntaxTree;
import com.youthlin.snl.compiler.frontend.parser.ParseResult;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2016-05-29-029.
 * 测试递归下降
 */
public class RCLL1ParserTest {
    private RDParser RDParser;
    private InputStream in;
    private List<Token> list;

    @Before
    public void init() throws IOException {
        in = RCLL1ParserTest.class.getClassLoader().getResourceAsStream("sd.snl");
        RDParser = new RDParser();
//        list = getList();
        list = new Lexer().getResult(new InputStreamReader(in)).getTokenList();
        for (Token t : list) System.out.println(t);
    }

    @Test
    public void test() throws FileNotFoundException {
//        ParseResult result = RDParser.parse(in);
        ParseResult result = new LL1Parser().parse(list);
        if (result.isSuccess()) {
            SyntaxTree tree = result.getTree();
            System.out.println();
            SyntaxTree.print(tree.getRoot(), new PrintStream(System.out),
                    "以下是语法树", 5);
        } else {
            System.err.println("RDParser Error!");
            result.getErrors().forEach(System.err::println);
        }
    }

    private List<Token> getList() {
        List<Token> l = new ArrayList<>();
        l.add(new Token("a"));
        l.add(new Token("a"));
        l.add(new Token("d"));
        l.add(new Token("d"));
        l.add(new Token(TokenType.EOF));
        return l;
    }
}
