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
            System.out.print(r.getValue()+" ");
            int childCount = r.getChildCount();
            for (int i = 0; i < childCount; i++) {
                preOrder(r.getChild()[i]);
            }
        }
    }
}
