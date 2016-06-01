package com.youthlin.snl.compiler.frontend.symbol;

import com.youthlin.snl.compiler.frontend.lexer.Token;
import com.youthlin.snl.compiler.frontend.parser.TreeNode;

import java.util.List;
import java.util.Map;

/**
 * Created by lin on 2016-06-01-001.
 * 非终极符
 */
public class NonTerminalSymbol extends Symbol {
    //region
    public static  NonTerminalSymbol Program (){return  (new NonTerminalSymbol("Program"));}
    public static  NonTerminalSymbol ProgramHead (){return (new NonTerminalSymbol("ProgramHead"));}
    public static  NonTerminalSymbol ProgramName (){return (new NonTerminalSymbol("ProgramName"));}
    public static  NonTerminalSymbol DeclarePart (){return (new NonTerminalSymbol("DeclarePart"));}
    public static  NonTerminalSymbol TypeDecpart (){return (new NonTerminalSymbol("TypeDecpart"));}
    public static  NonTerminalSymbol TypeDec (){return (new NonTerminalSymbol("TypeDec"));}
    public static  NonTerminalSymbol TypeDecList (){return (new NonTerminalSymbol("TypeDecList"));}
    public static  NonTerminalSymbol TypeDecMore (){return (new NonTerminalSymbol("TypeDecMore"));}
    public static  NonTerminalSymbol TypeId (){return (new NonTerminalSymbol("TypeId"));}
    public static  NonTerminalSymbol TypeDef (){return (new NonTerminalSymbol("TypeDef"));}
    public static  NonTerminalSymbol BaseType (){return (new NonTerminalSymbol("BaseType"));}
    public static  NonTerminalSymbol StructureType (){return (new NonTerminalSymbol("StructureType"));}
    public static  NonTerminalSymbol ArrayType (){return (new NonTerminalSymbol("ArrayType"));}
    public static  NonTerminalSymbol Low (){return (new NonTerminalSymbol("Low"));}
    public static  NonTerminalSymbol Top (){return (new NonTerminalSymbol("Top"));}
    public static  NonTerminalSymbol RecType (){return (new NonTerminalSymbol("RecType"));}
    public static  NonTerminalSymbol FiledDecList (){return (new NonTerminalSymbol("FiledDecList"));}
    public static  NonTerminalSymbol FiledDecMore (){return (new NonTerminalSymbol("FiledDecMore"));}
    public static  NonTerminalSymbol IdList (){return (new NonTerminalSymbol("IdList"));}
    public static  NonTerminalSymbol IdMore (){return (new NonTerminalSymbol("IdMore"));}
    public static  NonTerminalSymbol VarDecpart (){return (new NonTerminalSymbol("VarDecpart"));}
    public static  NonTerminalSymbol VarDec (){return (new NonTerminalSymbol("VarDec"));}
    public static  NonTerminalSymbol VarDecList (){return (new NonTerminalSymbol("VarDecList"));}
    public static  NonTerminalSymbol VarDecMore (){return (new NonTerminalSymbol("VarDecMore"));}
    public static  NonTerminalSymbol VarIdList (){return (new NonTerminalSymbol("VarIdList"));}
    public static  NonTerminalSymbol VarIdMore (){return (new NonTerminalSymbol("VarIdMore"));}
    public static  NonTerminalSymbol ProcDecpart (){return (new NonTerminalSymbol("ProcDecpart"));}
    public static  NonTerminalSymbol ProcDec (){return (new NonTerminalSymbol("ProcDec"));}
    public static  NonTerminalSymbol ProcName (){return (new NonTerminalSymbol("ProcName"));}
    public static  NonTerminalSymbol ParamList (){return (new NonTerminalSymbol("ParamList"));}
    public static  NonTerminalSymbol ParamDecList (){return (new NonTerminalSymbol("ParamDecList"));}
    public static  NonTerminalSymbol ParamMore (){return (new NonTerminalSymbol("ParamMore"));}
    public static  NonTerminalSymbol Param (){return (new NonTerminalSymbol("Param"));}
    public static  NonTerminalSymbol FormList (){return (new NonTerminalSymbol("FormList"));}
    public static  NonTerminalSymbol FidMore (){return (new NonTerminalSymbol("FidMore"));}
    public static  NonTerminalSymbol ProcDecPart (){return (new NonTerminalSymbol("ProcDecPart"));}
    public static  NonTerminalSymbol ProcBody (){return (new NonTerminalSymbol("ProcBody"));}
    public static  NonTerminalSymbol ProgramBody (){return (new NonTerminalSymbol("ProgramBody"));}
    public static  NonTerminalSymbol StmList (){return (new NonTerminalSymbol("StmList"));}
    public static  NonTerminalSymbol StmMore (){return (new NonTerminalSymbol("StmMore"));}
    public static  NonTerminalSymbol Stm (){return (new NonTerminalSymbol("Stm"));}
    public static  NonTerminalSymbol AssCall (){return (new NonTerminalSymbol("AssCall"));}
    public static  NonTerminalSymbol AssignmentRest (){return (new NonTerminalSymbol("AssignmentRest"));}
    public static  NonTerminalSymbol ConditionalStm (){return (new NonTerminalSymbol("ConditionalStm"));}
    public static  NonTerminalSymbol LoopStm (){return (new NonTerminalSymbol("LoopStm"));}
    public static  NonTerminalSymbol InputStm (){return (new NonTerminalSymbol("InputStm"));}
    public static  NonTerminalSymbol InVar (){return (new NonTerminalSymbol("InVar"));}
    public static  NonTerminalSymbol OutputStm (){return (new NonTerminalSymbol("OutputStm"));}
    public static  NonTerminalSymbol ReturnStm (){return (new NonTerminalSymbol("ReturnStm"));}
    public static  NonTerminalSymbol CallStmRest (){return (new NonTerminalSymbol("CallStmRest"));}
    public static  NonTerminalSymbol ActParamList (){return (new NonTerminalSymbol("ActParamList"));}
    public static  NonTerminalSymbol ActParamMore (){return (new NonTerminalSymbol("ActParamMore"));}
    public static  NonTerminalSymbol RelExp (){return (new NonTerminalSymbol("RelExp"));}
    public static  NonTerminalSymbol OtherRelE (){return (new NonTerminalSymbol("OtherRelE"));}
    public static  NonTerminalSymbol Exp (){return (new NonTerminalSymbol("Exp"));}
    public static  NonTerminalSymbol OtherTerm (){return (new NonTerminalSymbol("OtherTerm"));}
    public static  NonTerminalSymbol Term (){return (new NonTerminalSymbol("Term"));}
    public static  NonTerminalSymbol Factor (){return (new NonTerminalSymbol("Factor"));}
    public static  NonTerminalSymbol OtherFactor (){return (new NonTerminalSymbol("OtherFactor"));}
    public static  NonTerminalSymbol Variable (){return (new NonTerminalSymbol("Variable"));}
    public static  NonTerminalSymbol VariMore (){return (new NonTerminalSymbol("VariMore"));}
    public static  NonTerminalSymbol FieldVar (){return (new NonTerminalSymbol("FieldVar"));}
    public static  NonTerminalSymbol FieldVarMore (){return (new NonTerminalSymbol("FieldVarMore"));}
    public static  NonTerminalSymbol CmpOp (){return (new NonTerminalSymbol("CmpOp"));}
    public static  NonTerminalSymbol AddOp (){return (new NonTerminalSymbol("AddOp"));}
    public static  NonTerminalSymbol MultOp (){return (new NonTerminalSymbol("MultOp"));}
    //endregion

    private final TreeNode node;
    private final String value;

    public NonTerminalSymbol(String value) {
        super();
        this.value = value;
        node = new TreeNode(value);
    }

    @Override
    public TreeNode getNode() {
        return node;
    }

    public String getValue() {
        return value;
    }


}
