package com.youthlin.snl.compiler.frontend.grammarparser.recursivedescent;

import com.youthlin.snl.compiler.frontend.grammarparser.GrammarTree;
import com.youthlin.snl.compiler.frontend.grammarparser.TreeNode;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by lin on 2016-05-29-029.
 * 测试递归下降
 */
public class ParserTest {
    private Parser parser;

    @Before
    public void init() {
        InputStream in = ParserTest.class.getClassLoader().getResourceAsStream("test.snl");
        parser = new Parser(in);
    }

    @Test
    public void test() {
        ParserResult result = parser.parse();
        if (result.isSuccess()) {
            TreeNode root = parser.parse().getRoot();
            GrammarTree tree = new GrammarTree();
            tree.setRoot(root);
            tree.preOrder(root);
        } else {
            System.err.println("Parser Error!");
            result.getErrors().forEach(System.err::println);
        }
    }
}
