package com.youthlin.snl.compiler.frontend.parser.recursivedescent;

import com.youthlin.snl.compiler.frontend.parser.SyntaxTree;
import com.youthlin.snl.compiler.frontend.parser.ParseResult;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * Created by lin on 2016-05-29-029.
 * 测试递归下降
 */
public class RCLL1ParserTest {
    private RCParser RCParser;
    private InputStream in;

    @Before
    public void init() {
        in = RCLL1ParserTest.class.getClassLoader().getResourceAsStream("sd.snl");
        RCParser = new RCParser();
    }

    @Test
    public void test() throws FileNotFoundException {
        ParseResult result = RCParser.parse(in);
        if (result.isSuccess()) {
            SyntaxTree tree = result.getTree();
            System.out.println();
            SyntaxTree.print(tree.getRoot(), new PrintStream(System.out),
                    "以下是语法树", 5);
        } else {
            System.err.println("RCParser Error!");
            result.getErrors().forEach(System.err::println);
        }
    }
}
