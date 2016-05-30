package com.youthlin.snl.compiler.frontend.grammarparser;

import java.util.Stack;

/**
 * Created by lin on 2016-05-28-028.
 * 语法树节点
 */
public class TreeNode {
    private TreeNode[] children;
    private String value;
    private int offset;
    private int width;
    private int leftWidth = -1;
    private int rightWidth = -1;


    public TreeNode() {
        this("");
    }

    public TreeNode(String value) {
        this(null, value);
    }

    public TreeNode(TreeNode[] children, String value) {
        this.children = children;
        setValue(value);
    }

    private void initLeftWidth() {
        TreeNode temp;
        int sum = 0;
        int trueCount = 0;
        for (TreeNode aChildren : children) {
            if (aChildren != null)
                trueCount++;
        }
        boolean odd = trueCount / 2 == 0;//偶数棵
        if (trueCount == 0) {
            leftWidth = 0;
            return;
        }
        int mid = 0;
        int leftNonNullCount = 0;
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) leftNonNullCount++;
            if (leftNonNullCount == (1 + trueCount) / 2) mid = i;
        }
        //求左边子树的宽度之和
        for (int i = 0; i < mid; i++) {
            temp = children[i];
            if (temp != null) {
                int wrw = +temp.getWidth() + temp.getRightWidth();//width and right width
                if ((odd && i == mid - 1 && wrw < 2)) {
                    /**
                     *  _D_
                     * | | |
                     * A B C
                     *
                     * */
                    wrw = 2;
                }
                sum += temp.getLeftWidth() + wrw;
            }
        }
        temp = children[mid];

        /**
         * 如果有奇数个子树
         * <pre>
         *  ___a___
         * |   |   |
         * 0  _1_  2
         *   |  |
         *   3  4
         *</pre>
         * */
        if (!odd) {
            sum += temp.getLeftWidth();
        }
        /**
         * 偶数个子树
         * <pre>
         *  _____a____
         * |   |    | |
         * 0  _1_   2 5
         *   |  |
         *   3  4
         *</pre>
         * */
        else {
            int wrw = temp.getWidth() + temp.getRightWidth();
            if (wrw < 2) wrw = 2;
            sum += temp.getLeftWidth() + wrw;
        }
        leftWidth = sum;
    }

    private void initRightWidth() {
        TreeNode temp;
        int childCount = children.length;
        int sum = 0;
        int trueCount = 0;
        for (TreeNode aChildren : children) {
            if (aChildren != null)
                trueCount++;
        }
        if (trueCount == 0) {
            rightWidth = 0;
            return;
        }
        int mid = 0;
        int leftNonNullCount = 0;
        for (int i = 0; i < childCount; i++) {
            if (children[i] != null) leftNonNullCount++;
            if (leftNonNullCount == (1 + trueCount) / 2) mid = i;
        }
        /**
         * 偶数个子树, mid结点为1
         * <pre>
         *  ______a___
         * |   |    | |
         * 0  _1_   2 5
         *   |  |
         *   3  4
         *</pre>
         * a比4更靠右1个位置
         * */
        for (int i = mid + 1; i < childCount; i++) {
            temp = children[i];
            if (temp != null) {
                int wlw = temp.getLeftWidth() + temp.getWidth();
                if (i == mid + 1 && wlw < 2) wlw = 2;
                sum += wlw + temp.getRightWidth();
            }
        }
        rightWidth = sum;
        /**
         * 如果有奇数个子树,还要额外加上mid的右宽, mid为123结点
         * <pre>
         *  ___a___ __      ___abcd __      _D_
         * |   |      |    |   |      |    | | |
         * 0  _123_   2    0  _123_   2    A B C
         *   |    |          |    |
         *   3    4          3    4
         *</pre>
         * a与123在同一竖直位置
         * */
        if (trueCount % 2 != 0) {
            temp = children[mid];
            sum = temp.getWidth() + temp.getRightWidth();
            if (sum - getWidth() > 0) sum -= getLeftWidth();
            rightWidth += sum;
        }
    }

    //TODO 非递归中根遍历
    public void initOffset() {
        Stack<TreeNode> stack = new Stack<>();
    }

    TreeNode[] getChildren() {
        return children;
    }

    public void setChildren(TreeNode... nodes) {
        this.children = nodes;
    }

    String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        width = value.length();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getRightWidth() {
        if (rightWidth == -1) initRightWidth();
        return rightWidth;
    }

    public void setRightWidth(int rightWidth) {
        this.rightWidth = rightWidth;
    }

    public int getLeftWidth() {
        if (leftWidth == -1) initLeftWidth();
        return leftWidth;
    }

    public void setLeftWidth(int leftWidth) {
        this.leftWidth = leftWidth;
    }
}
