package com.youthlin.snl.compiler.frontend.parser;

/**
 * Created by lin on 2016-05-28-028.
 * 语法树节点
 */
public class TreeNode {
    private TreeNode[] children;
    private String value;
    private int offset;
    private int width;
    private boolean printed;

    TreeNode() {
        this("");
    }

    public TreeNode(String value) {
        this(null, value);
    }

    private TreeNode(TreeNode[] children, String value) {
        this.children = children;
        setValue(value);
    }

    boolean hasChild() {
        if (children == null) return false;
        for (TreeNode n : children)
            if (n != null)
                return true;
        return false;
    }

    boolean hasChildNotPrinted() {
        if (children == null) return false;
        for (TreeNode n : children)
            if (n != null)
                if (!n.printed)
                    return true;
        return false;
    }

//region 呵呵哒
/*
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
                    */
/**
 *  _D_
 * | | |
 * A B C
 *
 * *//*

                    wrw = 2;
                }
                sum += temp.getLeftWidth() + wrw;
            }
        }
        temp = children[mid];

        */
/**
 * 如果有奇数个子树
 * <pre>
 *  ___a___
 * |   |   |
 * 0  _1_  2
 *   |  |
 *   3  4
 *</pre>
 * *//*

        if (!odd) {
            sum += temp.getLeftWidth();
        }
        */
/**
 * 偶数个子树
 * <pre>
 *  _____a____
 * |   |    | |
 * 0  _1_   2 5
 *   |  |
 *   3  4
 *</pre>
 * *//*

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
        */
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
 * *//*

        for (int i = mid + 1; i < childCount; i++) {
            temp = children[i];
            if (temp != null) {
                int wlw = temp.getLeftWidth() + temp.getWidth();
                if (i == mid + 1 && wlw < 2) wlw = 2;
                sum += wlw + temp.getRightWidth();
            }
        }
        rightWidth = sum;
        */

    /**
     * 如果有奇数个子树,还要额外加上mid的右宽, mid为123结点
     * <pre>
     *  ___a___ __      ___abcd __      _D_
     * |   |      |    |   |      |    | | |
     * 0  _123_   2    0  _123_   2    A B C
     *   |    |          |    |
     *   3    4          3    4
     * </pre>
     * a与123在同一竖直位置
     *//*

        if (trueCount % 2 != 0) {
            temp = children[mid];
            sum = temp.getWidth() + temp.getRightWidth();
            if (sum - getWidth() > 0) sum -= getLeftWidth();
            rightWidth += sum;
        }
    }
*/
//endregion

    TreeNode[] getChildren() {
        return children;
    }

    public void setChildren(TreeNode... nodes) {
        this.children = nodes;
    }

    public String getValue() {
        return value;
    }

    private void setValue(String value) {
        this.value = value;
        width = value.length();
    }

    int getWidth() {
        return width;
    }

    int getOffset() {
        return offset;
    }

    void setPrinted(boolean printed) {
        this.printed = printed;
    }
}
