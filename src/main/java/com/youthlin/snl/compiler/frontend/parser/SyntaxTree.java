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
