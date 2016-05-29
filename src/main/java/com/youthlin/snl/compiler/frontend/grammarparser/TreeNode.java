package com.youthlin.snl.compiler.frontend.grammarparser;

/**
 * Created by lin on 2016-05-28-028.
 * 语法树节点
 */
public class TreeNode {
    private TreeNode parent;
    private TreeNode[] child;
    private boolean isLeaf;
    private String value;

    public TreeNode() {
        this("");
    }

    public TreeNode(String value) {
        this(null, null, false, value);
    }

    public TreeNode(TreeNode parent, TreeNode[] child, boolean isLeaf, String value) {
        this.parent = parent;
        this.child = child;
        this.isLeaf = isLeaf;
        this.value = value;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public TreeNode[] getChild() {
        return child;
    }

    public void setChild(TreeNode... nodes) {
        this.child = nodes;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
