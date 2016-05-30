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
        TreeNode root = new TreeNode("<Program>");
        tree.setRoot(root);
        TreeNode t1 = new TreeNode("<ProgramHead>");
        TreeNode t2 = new TreeNode("<DeclarePart>");
        TreeNode t3 = new TreeNode("<ProgramBody>");
        root.setChildren(t1, t2, t3, new TreeNode("."));

        TreeNode t4 = new TreeNode("program");
        TreeNode t5 = new TreeNode("<ProgramName>");
        t1.setChildren(t4, t5);

        t4 = new TreeNode("p");
        t5.setChildren(t4);

        TreeNode t = new TreeNode("TypeDecPart");
        TreeNode v = new TreeNode("VarDecPart");
        TreeNode p = new TreeNode("ProcDecPart");
        t2.setChildren(t, v, p);
        t.setChildren(new TreeNode("Ɛ"));
        v.setChildren(new TreeNode("Ɛ"));
        p.setChildren(new TreeNode("Ɛ"));

        TreeNode stmList = new TreeNode("StmList");
        t3.setChildren(new TreeNode("begin"), stmList, new TreeNode("end"));

        TreeNode stm = new TreeNode("Stm");
        TreeNode stmMore = new TreeNode("StmMore");
        stmList.setChildren(stm, stmMore);
        stmMore.setChildren(new TreeNode("Ɛ"));

        TreeNode inputStm = new TreeNode("InputStm");
        stm.setChildren(inputStm);
        TreeNode invar = new TreeNode("INvar");
        inputStm.setChildren(new TreeNode("read"), new TreeNode("("), invar, new TreeNode(")"));
        invar.setChildren(new TreeNode("v1"));
    }

    @Test
    public void showTree() {
        tree.preOrder(tree.getRoot());
    }
}
