package com.youthlin.snl.compiler.frontend.grammarparser;

/**
 * Created by lin on 2016-05-28-028.
 * 语法树
 */
public class GrammarTree {
    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    private TreeNode root;

    public void preOrder(TreeNode r) {
        if (r != null) {
            System.out.print(r.getValue() + " ");
            TreeNode[] children = r.getChild();
            if (children != null) {
                for (TreeNode aChildren : children) {
                    preOrder(aChildren);
                }
            }
        }
    }
}
