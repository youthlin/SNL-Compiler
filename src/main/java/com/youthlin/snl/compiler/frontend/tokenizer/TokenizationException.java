package com.youthlin.snl.compiler.frontend.tokenizer;

/**
 * Created by lin on 2016-05-28-028.
 * 词法错误
 */
public class TokenizationException extends RuntimeException {
    public TokenizationException(String message) {
        super(message);
    }
}
