package com.youthlin.snl.compiler.frontend.parser.test;

import com.youthlin.snl.compiler.frontend.parser.SyntaxTree;
import com.youthlin.snl.compiler.frontend.parser.TreeNode;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * Created by lin on 2016-05-28-028.
 * 测试语法树
 */
public class SyntaxTreeTest {
    private SyntaxTree tree;

    //    @Before
    public void aVoid() {
        tree = new SyntaxTree();
        TreeNode root = new TreeNode("<Program>");
        tree.setRoot(root);
        TreeNode head = new TreeNode("<ProgramHead>");
        TreeNode dec = new TreeNode("<DeclarePart>");
        TreeNode body = new TreeNode("<ProgramBody>");
        root.setChildren(head, dec, body, new TreeNode("."));

        TreeNode program = new TreeNode("program");
        TreeNode name = new TreeNode("<Name>");
        name.setChildren(new TreeNode("p"));
        head.setChildren(program, name);

        dec.setChildren(new TreeNode("<Type>"), new TreeNode("<Var>"));
        body.setChildren(new TreeNode("begin"));
    }

    @Before
    public void init() {
        tree = new SyntaxTree();
        TreeNode root = new TreeNode("<Program>");
        tree.setRoot(root);
        TreeNode head = new TreeNode("<ProgramHead>");
        TreeNode dec = new TreeNode("<DeclarePart>");
        TreeNode body = new TreeNode("<ProgramBody>");
        root.setChildren(head, dec, body, new TreeNode("."));

        TreeNode name = new TreeNode("<ProgramName>");
        name.setChildren(new TreeNode("p"));
        head.setChildren(new TreeNode("program"), name);

        TreeNode t = new TreeNode("TypeDecPart");
        TreeNode v = new TreeNode("VarDecPart");
        TreeNode p = new TreeNode("ProcDecPart");
        dec.setChildren(t, v, p);
        t.setChildren(new TreeNode("Ɛ"));
        v.setChildren(new TreeNode("Ɛ"));
        p.setChildren(new TreeNode("Ɛ"));

        TreeNode stmList = new TreeNode("StmList");
        body.setChildren(new TreeNode("begin"), stmList, new TreeNode("end"));

        TreeNode stm = new TreeNode("Stm");
        TreeNode stmMore = new TreeNode("StmMore");
        stmList.setChildren(stm, stmMore);
        stmMore.setChildren(new TreeNode("Ɛ"));

        TreeNode inputStm = new TreeNode("InputStm");
        stm.setChildren(inputStm);
        TreeNode invar = new TreeNode("Invar");
        inputStm.setChildren(new TreeNode("read"), new TreeNode("("), invar, new TreeNode(")"));
        invar.setChildren(new TreeNode("v1"));
    }

    @Test
    public void showTree() throws FileNotFoundException {
        SyntaxTree.print(tree.getRoot(), System.out, "", 0);
    }
}
