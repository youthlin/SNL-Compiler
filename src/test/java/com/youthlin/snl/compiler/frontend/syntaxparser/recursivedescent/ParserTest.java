package com.youthlin.snl.compiler.frontend.syntaxparser.recursivedescent;

import com.youthlin.snl.compiler.frontend.syntaxparser.SyntaxTree;
import com.youthlin.snl.compiler.frontend.syntaxparser.ParseResult;
import com.youthlin.snl.compiler.frontend.syntaxparser.TreeNode;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * Created by lin on 2016-05-29-029.
 * 测试递归下降
 */
public class ParserTest {
    private Parser parser;

    @Before
    public void init() {
        InputStream in = ParserTest.class.getClassLoader().getResourceAsStream("sd.snl");
        parser = new Parser(in);
    }

    @Test
    public void test() throws FileNotFoundException {
        ParseResult result = parser.parse();
        if (result.isSuccess()) {
            TreeNode root = result.getRoot();
            SyntaxTree tree = new SyntaxTree();
            tree.setRoot(root);
            System.out.println();
            SyntaxTree.print(root, new PrintStream(new FileOutputStream("D:/o.txt")), "以下是语法树", 5);
        } else {
            System.err.println("Parser Error!");
            result.getErrors().forEach(System.err::println);
        }
    }
}
