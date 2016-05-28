package com.youthlin.snl.compiler.frontend.grammarparser.test;

import com.youthlin.snl.compiler.frontend.grammarparser.GrammarTree;
import com.youthlin.snl.compiler.frontend.grammarparser.TreeNode;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by lin on 2016-05-28-028.
 * 测试语法树
 */
public class GrammarTreeTest {
    private GrammarTree tree;

    @Before
    public void init() {
        tree = new GrammarTree();
        TreeNode root = new TreeNode();
        tree.setRoot(root);
        int childCount = 3;
        root.setChildCount(childCount);
        TreeNode t1 = new TreeNode();
        TreeNode t2 = new TreeNode();
        TreeNode t3 = new TreeNode();
        TreeNode[] children = new TreeNode[]{t1, t2, t3};
        root.setChild(children);
        root.setValue("<Program>");
        t1.setValue("<ProgramHead>");
        t2.setValue("<DeclarePart>");
        t3.setValue("<ProgramBody>");

        t1.setChildCount(2);
        TreeNode t4 = new TreeNode();
        TreeNode t5 = new TreeNode();
        children = new TreeNode[]{t4, t5};
        t1.setChild(children);
        t4.setValue("program");
        t5.setValue("<ProgramName>");

        t5.setChildCount(1);
        t4 = new TreeNode();
        t4.setValue("p");
        t5.setChild(new TreeNode[]{t4});


    }

    @Test
    public void showTree() {
        tree.preOrder(tree.getRoot());
    }
}
