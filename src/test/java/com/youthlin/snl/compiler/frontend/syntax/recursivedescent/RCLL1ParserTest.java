package com.youthlin.snl.compiler.frontend.syntax.recursivedescent;

import com.youthlin.snl.compiler.frontend.lexer.Lexer;
import com.youthlin.snl.compiler.frontend.lexer.LexerResult;
import com.youthlin.snl.compiler.frontend.lexer.Token;
import com.youthlin.snl.compiler.frontend.lexer.TokenType;
import com.youthlin.snl.compiler.frontend.syntax.LL1.LL1Parser;
import com.youthlin.snl.compiler.frontend.syntax.SyntaxTree;
import com.youthlin.snl.compiler.frontend.syntax.ParseResult;
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
    private List<Token> list;
    private LexerResult result;

    @Before
    public void init() throws IOException {
        InputStream in = RCLL1ParserTest.class.getClassLoader().getResourceAsStream("test.snl");
        result = new Lexer().getResult(new InputStreamReader(in));
        list = result.getTokenList();

        for (Token t : list) System.out.println(t);
    }

    @Test
    public void test() throws FileNotFoundException {
        if (result.getErrors().size() > 0) {
            result.getErrors().forEach(System.out::println);
            return;
        }
        ParseResult result = new LL1Parser().parse(list);
//        ParseResult result = new RDParser().parse(list);
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

}
