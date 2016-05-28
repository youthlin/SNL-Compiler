package com.youthlin.snl.compiler.frontend.tokenizer.test;

import com.youthlin.snl.compiler.frontend.tokenizer.Token;
import com.youthlin.snl.compiler.frontend.tokenizer.TokenType;
import com.youthlin.snl.compiler.frontend.tokenizer.Tokenizer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by lin on 2016-05-28-028.
 * 测试词法分析器
 */
public class TokenizerTest {
    private Tokenizer tokenizer;

    @Before
    public void init() {
        InputStream source = TokenizerTest.class.getClassLoader().getResourceAsStream("test.snl");
        tokenizer = new Tokenizer(source);
    }

    @Test
    public void test() throws IOException {
        List<Token> list = tokenizer.getTokenList();
        Assert.assertTrue(list.size() > 0);
        for (Token t : list) {
            if (t.getValue() != null && t.getValue().equals(TokenType.PROGRAM.getStr())) {
                Assert.assertEquals(TokenType.PROGRAM, t.getType());
            }
        }
    }

    @After
    public void after() {
        InputStream source = TokenizerTest.class.getClassLoader().getResourceAsStream("test.snl");
        System.out.println("测试完成，源文件是:");
        System.out.println("//----------开始");
        System.out.println(Tokenizer.getSourceFileAsString(source));
        System.out.println("//----------结束");
    }
}
