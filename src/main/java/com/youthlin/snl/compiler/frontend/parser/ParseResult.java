package com.youthlin.snl.compiler.frontend.parser;

import java.util.List;

/**
 * Created by lin on 2016-05-29-029.
 * 分析结果
 */
public class ParseResult {
    private SyntaxTree tree;
    private List<String> errors;

    public boolean isSuccess() {
        return errors == null || errors.size() == 0;
    }

    public SyntaxTree getTree() {
        return tree;
    }

    public void setTree(SyntaxTree tree) {
        this.tree = tree;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
