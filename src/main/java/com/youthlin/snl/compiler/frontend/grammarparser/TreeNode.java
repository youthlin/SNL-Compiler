package com.youthlin.snl.compiler.frontend.grammarparser;

/**
 * Created by lin on 2016-05-28-028.
 * 语法树节点
 */
public class TreeNode {
    private int childCount;
    private TreeNode parent;
    private TreeNode[] child;
    private boolean isLeaf;
    private String value;

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
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

    public void setChild(TreeNode[] child) {
        this.child = child;
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
