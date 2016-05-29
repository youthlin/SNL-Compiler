package com.youthlin.snl.compiler.frontend.grammarparser;

/**
 * Created by lin on 2016-05-29-029.
 * 语法错误异常
 */
public class GrammarParserException extends RuntimeException {
    public GrammarParserException() {
        super();
    }

    public GrammarParserException(String msg) {
        super(msg);
    }
}
