package com.youthlin.snl.compiler.frontend.parser;

import java.io.PrintStream;
import java.util.*;

/**
 * Created by lin on 2016-05-28-028.
 * 语法树
 */
public class SyntaxTree {
    private TreeNode root;

    public SyntaxTree() {
    }

    public SyntaxTree(TreeNode root) {
        this.root = root;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    /**
     * 未完成，横板输出语法树
     *
     * @link http://youthlin.com/?p=868
     */
    public static void printnotyet(TreeNode root, PrintStream out, String msg, int offset) {
        //首先输出Message
        out.println(msg);
        //树空，直接返回
        if (root == null) return;
        // 设置每个结点坐标
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

    /**
     * 使用栈进行非递归的前序遍历，竖版输出
     */
    public static void print(TreeNode root, PrintStream out, String msg, int offset) {
        if (root == null) return;
        for (int i = 0; i <= offset; i++) out.print(" ");
        out.println(msg);
        for (int i = 0; i < offset; i++) out.print(" ");
        out.print(" " + root.getValue());
        boolean isLineHead = false;
        int index = 0;
        List<TreeNode> list = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        list.add(root);
        TreeNode node, temp;
        pushChild(stack, root);
        while (!stack.empty()) {
            node = stack.pop();
            if (!isLineHead) {
                out.print("_" + node.getValue());
                node.setPrinted(true);
                if (node.hasChild()) {
                    insert(list, node, ++index);
                    pushChild(stack, node);
                    isLineHead = false;
                } else {
                    isLineHead = true;
                    index = 0;
                    out.println();
                    for (int i = 0; i < offset; i++) out.print(" ");
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    temp = list.get(i);
                    index = i;
                    if (isChildOf(node, temp)) {
                        for (int j = 0; j < temp.getWidth(); j++) out.print(" ");
                        out.print("|_" + node.getValue());
                        node.setPrinted(true);
                        if (node.hasChild()) {
                            insert(list, node, ++index);
                            pushChild(stack, node);
                            isLineHead = false;
                        } else {
                            out.println();
                            for (int j = 0; j < offset; j++) out.print(" ");
                            index = 0;
                            isLineHead = true;
                        }
                        break;//跳出for
                    } else {
                        if (temp.hasChildNotPrinted()) {
                            for (int j = 0; j < temp.getWidth(); j++) out.print(" ");
                            out.print("|");
                        } else {
                            for (int j = 0; j <= temp.getWidth(); j++) out.print(" ");
                        }
                    }
                }
            }
        }
    }

    public static void print(TreeNode root, PrintStream out) {
        print(root, out, "", 0);
    }

    private static void insert(List<TreeNode> list, TreeNode node, int index) {
        if (list.size() <= index)
            list.add(node);
        else
            list.set(index, node);
    }

    private static void pushChild(Stack<TreeNode> stack, TreeNode node) {
        if (node.hasChild() && stack != null) {
            TreeNode temp;
            for (int i = node.getChildren().length - 1; i >= 0; i--) {
                temp = node.getChildren()[i];
                if (temp != null)
                    stack.push(temp);
            }
        }
    }

    private static boolean isChildOf(TreeNode node, TreeNode parent) {
        if (parent == null) return false;
        if (parent.getChildren() != null)
            for (TreeNode n : parent.getChildren())
                if (n == node) return true;
        return false;
    }
}
