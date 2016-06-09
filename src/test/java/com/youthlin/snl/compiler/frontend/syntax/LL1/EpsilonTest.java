package com.youthlin.snl.compiler.frontend.syntax.LL1;

import com.youthlin.snl.compiler.frontend.lexer.Token;
import com.youthlin.snl.compiler.frontend.syntax.symbol.TerminalSymbol;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by lin on 2016-06-01-001.
 * 测试空符号
 */
public class EpsilonTest {
    @Test
    public void test() {
        TerminalSymbol epsilon = TerminalSymbol.epsilon;
        Assert.assertTrue(epsilon.isEpsilon());
        Assert.assertTrue(!new TerminalSymbol(new Token()).isEpsilon());
    }
}
