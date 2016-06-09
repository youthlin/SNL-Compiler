package com.youthlin.snl.compiler.frontend.syntax.symbol;

import com.youthlin.snl.compiler.frontend.lexer.Token;
import com.youthlin.snl.compiler.frontend.lexer.TokenType;

import java.util.List;

import static com.youthlin.snl.compiler.frontend.lexer.TokenType.*;
import static com.youthlin.snl.compiler.frontend.syntax.symbol.NonTerminalSymbol.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * Created by lin on 2016-06-01-001.
 * 所有终极符
 */
@SuppressWarnings("unused")
public enum NON_TERMINAL_SYMBOLS {
    //region 非终极符列表,根据展望符返回相应产生式右部
    Program(new NonTerminalSymbol("Program")) {
        @Override
        public List<Symbol> find(Token predict) {
            if (predict.getType() == PROGRAM) {
                return asList(ProgramHead(), DeclarePart(), ProgramBody());
            }
            return null;
        }
    },
    ProgramHead(new NonTerminalSymbol("ProgramHead")) {
        @Override
        public List<Symbol> find(Token predict) {
            if (predict.getType() == PROGRAM) {
                return asList(new TerminalSymbol(PROGRAM), ProgramName());
            }
            return null;
        }
    },
    ProgramName(new NonTerminalSymbol("ProgramName")) {
        @Override
        public List<Symbol> find(Token predict) {
            if (predict.getType() == ID) {
                return singletonList(new TerminalSymbol(ID));
            }
            return null;
        }
    },
    DeclarePart(new NonTerminalSymbol("DeclarePart")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == TYPE || pre == VAR || pre == PROCEDURE || pre == BEGIN) {
                return asList(TypeDecpart(), VarDecpart(), ProcDecpart());
            }
            return null;
        }
    },
    TypeDecpart(new NonTerminalSymbol("TypeDecpart")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == VAR || pre == PROCEDURE || pre == BEGIN) {
                return singletonList(TerminalSymbol.epsilon);
            } else if (pre == TYPE) {
                return singletonList(TypeDec());
            }
            return null;
        }
    },
    TypeDec(new NonTerminalSymbol("TypeDec")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == TYPE) {
                return asList(new TerminalSymbol(TYPE), TypeDecList());
            }
            return null;
        }
    },
    TypeDecList(new NonTerminalSymbol("TypeDecList")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == ID) {
                return asList(TypeId(), new TerminalSymbol(EQ), TypeDef(),
                        new TerminalSymbol(SEMI), TypeDecMore());
            }
            return null;
        }
    },
    TypeDecMore(new NonTerminalSymbol("TypeDecMore")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == VAR || pre == PROCEDURE || pre == BEGIN)
                return singletonList(TerminalSymbol.epsilon);
            else if (pre == ID) return singletonList(TypeDecList());
            return null;
        }
    },
    TypeId(new NonTerminalSymbol("TypeId")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == ID) return singletonList(new TerminalSymbol(ID));
            return null;
        }
    },
    TypeDef(new NonTerminalSymbol("TypeDef")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case INTEGER:
                case CHAR:
                    return singletonList(BaseType());
                case ARRAY:
                case RECORD:
                    return singletonList(StructureType());
                case ID:
                    return singletonList(new TerminalSymbol(ID));
            }
            return null;
        }
    },
    BaseType(new NonTerminalSymbol("BaseType")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == INTEGER) return singletonList(new TerminalSymbol(INTEGER));
            if (pre == CHAR) return singletonList(new TerminalSymbol(CHAR));
            return null;
        }
    },
    StructureType(new NonTerminalSymbol("StructureType")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == ARRAY) return singletonList(ArrayType());
            if (pre == RECORD) return singletonList(RecType());
            return null;
        }
    },
    ArrayType(new NonTerminalSymbol("ArrayType")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == ARRAY)
                return asList(new TerminalSymbol(ARRAY), new TerminalSymbol(LMIDPAREN), Low(),
                        new TerminalSymbol(UNDERRANGE), Top(), new TerminalSymbol(RMIDPAREN),
                        new TerminalSymbol(OF), BaseType());
            return null;
        }
    },
    Low(new NonTerminalSymbol("Low")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == INTC)
                return singletonList(new TerminalSymbol(INTC));
            return null;
        }
    },
    Top(new NonTerminalSymbol("Top")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == INTC)
                return singletonList(new TerminalSymbol(INTC));
            return null;
        }
    },
    RecType(new NonTerminalSymbol("RecType")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == RECORD)
                return asList(new TerminalSymbol(RECORD), FiledDecList(), new TerminalSymbol(END));
            return null;
        }
    },
    FiledDecList(new NonTerminalSymbol("FiledDecList")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == INTEGER || pre == CHAR)
                return asList(BaseType(), IdList(), new TerminalSymbol(SEMI), FiledDecMore());
            if (pre == ARRAY)
                return asList(ArrayType(), IdList(), new TerminalSymbol(SEMI), FiledDecMore());
            return null;
        }
    },
    FiledDecMore(new NonTerminalSymbol("FiledDecMore")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == END)
                return singletonList(TerminalSymbol.epsilon);
            if (pre == INTEGER || pre == CHAR || pre == ARRAY)
                return singletonList(FiledDecList());
            return null;
        }
    },
    IdList(new NonTerminalSymbol("IdList")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == ID) return asList(new TerminalSymbol(ID), IdMore());
            return null;
        }
    },
    IdMore(new NonTerminalSymbol("IdMore")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == SEMI) return singletonList(TerminalSymbol.epsilon);
            if (pre == COMMA) return asList(new TerminalSymbol(COMMA), IdList());
            return null;
        }
    },
    VarDecpart(new NonTerminalSymbol("VarDecpart")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == PROCEDURE || pre == BEGIN) return singletonList(TerminalSymbol.epsilon);
            if (pre == VAR) return singletonList(VarDec());
            return null;
        }
    },
    VarDec(new NonTerminalSymbol("VarDec")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == VAR) return asList(new TerminalSymbol(VAR), VarDecList());
            return null;
        }
    },
    VarDecList(new NonTerminalSymbol("VarDecList")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case INTEGER:
                case CHAR:
                case ARRAY:
                case RECORD:
                case ID:
                    return asList(TypeDef(), VarIdList(), new TerminalSymbol(SEMI), VarDecMore());
            }
            return null;
        }
    },
    VarDecMore(new NonTerminalSymbol("VarDecMore")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case PROCEDURE:
                case BEGIN:
                    return singletonList(TerminalSymbol.epsilon);
                case INTEGER:
                case CHAR:
                case ARRAY:
                case RECORD:
                case ID:
                    return singletonList(VarDecList());
            }
            return null;
        }
    },
    VarIdList(new NonTerminalSymbol("VarIdList")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == ID) return asList(new TerminalSymbol(ID), VarIdMore());
            return null;
        }
    },
    VarIdMore(new NonTerminalSymbol("VarIdMore")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == SEMI) return singletonList(TerminalSymbol.epsilon);
            if (pre == COMMA) return asList(new TerminalSymbol(COMMA), VarIdList());
            return null;
        }
    },
    ProcDecpart(new NonTerminalSymbol("ProcDecpart")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == BEGIN) return singletonList(TerminalSymbol.epsilon);
            if (pre == PROCEDURE) return singletonList(ProcDec());
            return null;
        }
    },
    ProcDec(new NonTerminalSymbol("ProcDec")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == PROCEDURE) return asList(new TerminalSymbol(PROCEDURE), ProcName(),
                    new TerminalSymbol(LPAREN), ParamList(), new TerminalSymbol(RPAREN),
                    new TerminalSymbol(SEMI), ProcDecPart(), ProcBody(), ProcDecpart());
            return null;
        }
    },
    ProcName(new NonTerminalSymbol("ProcName")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == ID) return singletonList(new TerminalSymbol(ID));
            return null;
        }
    },
    ParamList(new NonTerminalSymbol("ParamList")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case RPAREN:
                    return singletonList(TerminalSymbol.epsilon);
                case INTEGER:
                case CHAR:
                case ARRAY:
                case RECORD:
                case ID:
                case VAR:
                    return singletonList(ParamDecList());
            }
            return null;
        }
    },
    ParamDecList(new NonTerminalSymbol("ParamDecList")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case INTEGER:
                case CHAR:
                case ARRAY:
                case RECORD:
                case ID:
                case VAR:
                    return asList(Param(), ParamMore());
            }
            return null;
        }
    },
    ParamMore(new NonTerminalSymbol("ParamMore")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == RPAREN) return singletonList(TerminalSymbol.epsilon);
            if (pre == SEMI) return asList(new TerminalSymbol(SEMI), ParamDecList());
            return null;
        }
    },
    Param(new NonTerminalSymbol("Param")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case INTEGER:
                case CHAR:
                case ARRAY:
                case RECORD:
                case ID:
                    return asList(TypeDef(), FormList());
                case VAR:
                    return asList(new TerminalSymbol(VAR), TypeDef(), FormList());
            }
            return null;
        }
    },
    FormList(new NonTerminalSymbol("FormList")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == ID) return asList(new TerminalSymbol(ID), FidMore());
            return null;
        }
    },
    FidMore(new NonTerminalSymbol("FidMore")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case SEMI:
                case RPAREN:
                    return singletonList(TerminalSymbol.epsilon);
                case COMMA:
                    return asList(new TerminalSymbol(COMMA), FormList());
            }
            return null;
        }
    },
    ProcDecPart(new NonTerminalSymbol("ProcDecPart")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case TYPE:
                case VAR:
                case PROCEDURE:
                case BEGIN:
                    return singletonList(DeclarePart());
            }
            return null;
        }
    },
    ProcBody(new NonTerminalSymbol("ProcBody")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == BEGIN) return singletonList(ProgramBody());
            return null;
        }
    },
    ProgramBody(new NonTerminalSymbol("ProgramBody")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == BEGIN) return asList(new TerminalSymbol(BEGIN), StmList(), new TerminalSymbol(END));
            return null;
        }
    },
    StmList(new NonTerminalSymbol("StmList")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case ID:
                case IF:
                case WHILE:
                case RETURN:
                case READ:
                case WRITE:
                    return asList(Stm(), StmMore());
            }
            return null;
        }
    },
    StmMore(new NonTerminalSymbol("StmMore")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case ELSE:
                case FI:
                case END:
                case ENDWH:
                    return singletonList(TerminalSymbol.epsilon);
                case SEMI:
                    return asList(new TerminalSymbol(SEMI), StmList());
            }
            return null;
        }
    },
    Stm(new NonTerminalSymbol("Stm")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case IF:
                    return singletonList(ConditionalStm());
                case WHILE:
                    return singletonList(LoopStm());
                case READ:
                    return singletonList(InputStm());
                case WRITE:
                    return singletonList(OutputStm());
                case RETURN:
                    return singletonList(ReturnStm());
                case ID:
                    return asList(new TerminalSymbol(ID), AssCall());
            }
            return null;
        }
    },
    AssCall(new NonTerminalSymbol("AssCall")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case LMIDPAREN:
                case DOT:
                case ASSIGN:
                    return singletonList(AssignmentRest());
                case LPAREN:
                    return singletonList(CallStmRest());
            }
            return null;
        }
    },
    AssignmentRest(new NonTerminalSymbol("AssignmentRest")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case LMIDPAREN:
                case DOT:
                case ASSIGN:
                    return asList(VariMore(), new TerminalSymbol(ASSIGN), Exp());
            }
            return null;
        }
    },
    ConditionalStm(new NonTerminalSymbol("ConditionalStm")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == IF) return asList(new TerminalSymbol(IF), RelExp(), new TerminalSymbol(THEN),
                    StmList(), new TerminalSymbol(ELSE), StmList(), new TerminalSymbol(FI));
            return null;
        }
    },
    LoopStm(new NonTerminalSymbol("LoopStm")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == WHILE) return asList(new TerminalSymbol(WHILE), RelExp(), new TerminalSymbol(DO),
                    StmList(), new TerminalSymbol(ENDWH));
            return null;
        }
    },
    InputStm(new NonTerminalSymbol("InputStm")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == READ) return asList(new TerminalSymbol(READ), new TerminalSymbol(LPAREN),
                    InVar(), new TerminalSymbol(RPAREN));
            return null;
        }
    },
    InVar(new NonTerminalSymbol("InVar")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == ID) return singletonList(new TerminalSymbol(ID));
            return null;
        }
    },
    OutputStm(new NonTerminalSymbol("OutputStm")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == WRITE) return asList(new TerminalSymbol(WRITE), new TerminalSymbol(LPAREN),
                    Exp(), new TerminalSymbol(RPAREN));
            return null;
        }
    },
    ReturnStm(new NonTerminalSymbol("ReturnStm")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == RETURN) return singletonList(new TerminalSymbol(RETURN));
            return null;
        }
    },
    CallStmRest(new NonTerminalSymbol("CallStmRest")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == LPAREN) return asList(new TerminalSymbol(LPAREN), ActParamList(), new TerminalSymbol(RPAREN));
            return null;
        }
    },
    ActParamList(new NonTerminalSymbol("ActParamList")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case RPAREN:
                    return singletonList(TerminalSymbol.epsilon);
                case LPAREN:
                case INTC:
                case ID:
                    return asList(Exp(), ActParamMore());
            }
            return null;
        }
    },
    ActParamMore(new NonTerminalSymbol("ActParamMore")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == RPAREN) return singletonList(TerminalSymbol.epsilon);
            if (pre == COMMA) return asList(new TerminalSymbol(COMMA), ActParamList());
            return null;
        }
    },
    RelExp(new NonTerminalSymbol("RelExp")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case LPAREN:
                case INTC:
                case ID:
                    return asList(Exp(), OtherRelE());
            }
            return null;
        }
    },
    OtherRelE(new NonTerminalSymbol("OtherRelE")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == LT || pre == EQ) return asList(CmpOp(), Exp());
            return null;
        }
    },
    Exp(new NonTerminalSymbol("Exp")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == LPAREN || pre == INTC || pre == ID) return asList(Term(), OtherTerm());
            return null;
        }
    },
    OtherTerm(new NonTerminalSymbol("OtherTerm")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case LT:
                case EQ:
                case RMIDPAREN:
                case THEN:
                case ELSE:
                case FI:
                case DO:
                case ENDWH:
                case RPAREN:
                case END:
                case SEMI:
                case COMMA:
                    return singletonList(TerminalSymbol.epsilon);
                case PLUS:
                case MINUS:
                    return asList(AddOp(), Exp());
            }
            return null;
        }
    },
    Term(new NonTerminalSymbol("Term")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == LPAREN || pre == INTC || pre == ID) return asList(Factor(), OtherFactor());
            return null;
        }
    },
    OtherFactor(OtherFactor()) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case PLUS:
                case MINUS:
                case LT:
                case EQ:
                case RMIDPAREN:
                case THEN:
                case ELSE:
                case FI:
                case DO:
                case ENDWH:
                case RPAREN:
                case END:
                case SEMI:
                case COMMA:
                    return singletonList(TerminalSymbol.epsilon);
                case TIMES:
                case OVER:
                    return asList(MultOp(), Term());
            }
            return null;
        }
    },
    Factor(new NonTerminalSymbol("Factor")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == LPAREN)
                return asList(new TerminalSymbol(LPAREN), Exp(), new TerminalSymbol(RPAREN));
            if (pre == INTC) return singletonList(new TerminalSymbol(INTC));
            if (pre == ID) return singletonList(Variable());
            return null;
        }
    },
    Variable(new NonTerminalSymbol("Variable")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == ID) return asList(new TerminalSymbol(ID), VariMore());
            return null;
        }
    },
    VariMore(new NonTerminalSymbol("VariMore")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case RMIDPAREN:
                case ASSIGN:
                case TIMES:
                case OVER:
                case PLUS:
                case MINUS:
                case LT:
                case EQ:
                case THEN:
                case ELSE:
                case FI:
                case DO:
                case ENDWH:
                case RPAREN:
                case END:
                case SEMI:
                case COMMA:
                    return singletonList(TerminalSymbol.epsilon);
                case LMIDPAREN:
                    return asList(new TerminalSymbol(LMIDPAREN), Exp(), new TerminalSymbol(RMIDPAREN));
                case DOT:
                    return asList(new TerminalSymbol(DOT), FieldVar());
            }
            return null;
        }
    },
    FieldVar(new NonTerminalSymbol("FieldVar")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == ID) return asList(new TerminalSymbol(ID), FieldVarMore());
            return null;
        }
    },
    FieldVarMore(new NonTerminalSymbol("FieldVarMore")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            switch (pre) {
                case ASSIGN:
                case TIMES:
                case OVER:
                case PLUS:
                case MINUS:
                case LT:
                case EQ:
                case THEN:
                case ELSE:
                case FI:
                case DO:
                case ENDWH:
                case RPAREN:
                case END:
                case SEMI:
                case COMMA:
                    return singletonList(TerminalSymbol.epsilon);
                case LMIDPAREN:
                    return asList(new TerminalSymbol(LMIDPAREN), Exp(), new TerminalSymbol(RMIDPAREN));
            }
            return null;
        }
    },
    CmpOp(new NonTerminalSymbol("CmpOp")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == LT) return singletonList(new TerminalSymbol(LT));
            if (pre == EQ) return singletonList(new TerminalSymbol(EQ));
            return null;
        }
    },
    AddOp(new NonTerminalSymbol("AddOp")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == PLUS) return singletonList(new TerminalSymbol(PLUS));
            if (pre == MINUS) return singletonList(new TerminalSymbol(MINUS));
            return null;
        }
    },
    MultOp(new NonTerminalSymbol("MultOp")) {
        @Override
        public List<Symbol> find(Token predict) {
            TokenType pre = predict.getType();
            if (pre == TIMES) return singletonList(new TerminalSymbol(TIMES));
            if (pre == OVER) return singletonList(new TerminalSymbol(OVER));
            return null;
        }
    };

    //endregion

    NON_TERMINAL_SYMBOLS(NonTerminalSymbol v) {
        this.value = v;
    }

    public NonTerminalSymbol value;

    public abstract List<Symbol> find(Token predict);
}
