package com.youthlin.snl.compiler.frontend.grammarparser;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

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
            TreeNode[] children = r.getChildren();
            if (children != null) {
                for (TreeNode aChildren : children) {
                    preOrder(aChildren);
                }
            }
        }
    }

    /**
     * 输出语法树
     *
     * @link http://youthlin.com/?p=868
     */
    public static void print(TreeNode root, PrintWriter out, String msg, int offset) {
        //首先输出Message
        out.println(msg);
        //树空，直接返回
        if (root == null) return;
        //TODO 设置每个结点坐标
        TreeNode p = root;
        TreeNode end = new TreeNode();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(p);
        queue.add(end);
        TreeNode temp;
        int cursor = 0;//当前光标位置
        //输出距离左边的偏移
        for (int i = 0; i < offset; i++) out.print(" ");
        //region 层次遍历输出
        while (true) {
            //region 出口
            if (queue.peek() == end) {
                out.println();
                break;
            }//endregion

            //region 输出数据
            while (true) {
                p = queue.poll();
                queue.add(p);
                //region 一行结束
                if (p == end) {
                    out.println();
                    for (int i = 0; i < offset; i++) out.print(" ");
                    cursor = 0;
                    break;
                }//endregion

                //region 找到第一个子孩子
                temp = null;
                for (int i = 0; i < p.getChildren().length; i++) {
                    temp = p.getChildren()[i];
                    if (temp != null) {
                        break;//找到第一个子孩子,跳出for
                    }
                }//endregion
                //region 输出空格直到第一个子孩子的偏移处
                if (temp != null) {
                    for (; cursor < temp.getOffset() && cursor < p.getOffset(); cursor++) {
                        out.print(" ");
                    }
                    //region 输出下划线直到自己的偏移处
                    for (; cursor < p.getOffset(); cursor++) {
                        out.print("_");
                    }
                    //endregion
                }//endregion
                //region 没有子孩子,直接全部输出空格直到自己的偏移处
                else {
                    for (; cursor < p.getOffset(); cursor++) {
                        out.print(" ");
                    }
                }//endregion

                out.print(p.getValue());
                cursor += p.getWidth();

                //region 找到最后一个子孩子
                temp = null;
                for (int i = p.getChildren().length - 1; i >= 0; i--) {
                    temp = p.getChildren()[i];
                    if (temp != null) {
                        break;//找到最后一个子孩子，跳出for循环
                    }
                }//endregion
                if (temp != null) {
                    for (; cursor < temp.getOffset(); cursor++) {
                        out.print("_");
                    }
                }

            }//endregion end while 输出数据

            //region 输出竖线
            while (true) {
                p = queue.poll();
                if (p == end) {
                    out.println();
                    for (int i = 0; i < offset; i++) out.print(" ");
                    cursor = 0;
                    queue.add(end);
                    break;
                }
                //对每个子孩子输出相应的竖线
                for (int i = 0; i < p.getChildren().length; i++) {
                    temp = p.getChildren()[i];
                    if (temp != null) {
                        for (; cursor < temp.getOffset(); cursor++) out.print(" ");
                        out.print("|");
                        cursor++;
                        queue.add(temp);
                    }
                }//for 对每个子孩子输出竖线
            }//endregion end while 输出竖线
        }//endregion wnd while 层次遍历输出
    }

    public static void print(TreeNode root) {
        print(root, new PrintWriter(System.out), "", 0);
    }
}
