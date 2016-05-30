package com.youthlin.snl.compiler.frontend.tokenizer;

import java.util.List;

/**
 * Created by lin on 2016-05-30-030.
 * 词法分析结果
 */
public class TokenizationResult {
    private List<Token> tokenList;
    private List<String> errors;

    public List<String> getErrors() {
        return errors;
    }

    void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    void setTokenList(List<Token> tokenList) {
        this.tokenList = tokenList;
    }
}
