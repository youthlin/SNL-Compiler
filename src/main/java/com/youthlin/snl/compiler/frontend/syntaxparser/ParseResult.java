package com.youthlin.snl.compiler.frontend.syntaxparser;

import java.util.List;

/**
 * Created by lin on 2016-05-29-029.
 * 分析结果
 */
public class ParseResult {
    private boolean success;
    private TreeNode root;
    private List<String> errors;

    public ParseResult() {
    }

    public ParseResult(boolean success, TreeNode root, List<String> errors) {
        this.success = success;
        this.root = root;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
