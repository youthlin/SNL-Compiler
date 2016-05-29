package com.youthlin.snl.compiler.frontend.grammarparser.recursivedescent;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.youthlin.snl.compiler.frontend.grammarparser.GrammarParser;
import com.youthlin.snl.compiler.frontend.grammarparser.GrammarParserException;
import com.youthlin.snl.compiler.frontend.grammarparser.TreeNode;
import com.youthlin.snl.compiler.frontend.tokenizer.Token;
import com.youthlin.snl.compiler.frontend.tokenizer.TokenType;
import com.youthlin.snl.compiler.frontend.tokenizer.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lin on 2016-05-28-028.
 * 递归下降语法分析
 */
public class Parser implements GrammarParser {
    private List<Token> list;
    private int currentTokenIndex;
    private Token lastRead;
    private List<String> errors;
    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);
    private final Token errorToken = new Token(TokenType.ERROR);

    public Parser(InputStream in) {
        errors = new ArrayList<>();
        Tokenizer tokenizer = new Tokenizer(in);
        try {
            list = tokenizer.getTokenList();
        } catch (IOException e) {
            list = new LinkedList<>();
            errors.add("读取源代码出错！" + e.getMessage());
            e.printStackTrace();
        }
        for (Token t : list) {
            LOG.trace(t.toString());
        }
    }

    public ParserResult parse() {
        ParserResult result = new ParserResult();
        TreeNode root = program();
        result.setRoot(root);
        if (getToken() != null) { //此时输入流中应该为空
            LOG.warn("源程序太长");
            errors.add("Source code too long.");
        } else {
            LOG.debug("语法分析成功");
        }
        if (errors.size() == 0) result.setSuccess(true);
        result.setErrors(errors);
        return result;
    }

    private TreeNode node(String value) {
        return new TreeNode(value);
    }

    /**
     * 获取下一个 Token.
     * 因为词法分析返回的 List 是 ArrayList,
     * 因此这里使用 get 方式。
     *
     * @return Next Token. null if there has no more token.
     */
    private Token getToken() {
        Token token = null;
        if (currentTokenIndex < list.size()) {
            token = list.get(currentTokenIndex++);
            LOG.trace("获取下一个 Token =" + token);
            lastRead = token;
        } else {
            LOG.warn("输入流已空");
        }
        return token;
    }

    private Token peekToken() {
        Token token = errorToken;
        if (currentTokenIndex < list.size()) {
            token = list.get(currentTokenIndex);
        }
        return token;
    }

    private TreeNode match(String expected) {
        Token input = getToken();
        TreeNode node = null;
        if (input != null) {
            if (input.getValue().equals(expected)) {
                LOG.trace("已匹配" + input);
                node = node(expected);
            } else
                errors.add("Unexpected token. " + expected + " expected. Near " + input.getValue()
                        + " at [" + input.getLine() + ":" + input.getColumn() + "]");
        } else {
            errors.add("Unexpected EOF. No more tokens at input stream.");
        }
        return node;
    }

    /**
     * (1) [Program] -> [ProgramHead] [DeclarePart] [ProgramBody] .  {program}
     */
    private TreeNode program() {
        TreeNode root = node("Program");
        LOG.trace("构造根结点");
        root.setChild(programHead(), declarePart(), programBody(), match("."));
        LOG.trace("根结点设置完毕");
        return root;
    }

    /**
     * (2) [ProgramHead] -> program [ProgramName] {program}
     */
    private TreeNode programHead() {
        TreeNode pHead = node("ProgramHead");
        LOG.trace("构造ProgramHead结点");
        pHead.setChild(match(TokenType.PROGRAM.getStr()), programName());
        LOG.trace("ProgramHead结点设置完毕");
        return pHead;
    }

    /**
     * (4) [DeclarePart] -> [TypeDecPart] [VarDecPart] [ProcDecPart] {type,var,procedure,begin}
     */
    private TreeNode declarePart() {
        TreeNode node = node("DeclarePart");
        LOG.trace("构造DeclarePart结点");
        node.setChild(typeDecPart(), varDecPart(), procDecPart());
        LOG.trace("DeclarePart结点设置完毕");
        return node;
    }

    /**
     * (57) [ProgramBody] -> begin [StmList] end {begin}
     */
    private TreeNode programBody() {
        TreeNode node = node("ProgramBody");
        LOG.trace("构造ProgramBody结点");
//TODO        node.setChild(match(TokenType.BEGIN.getStr(),));
        LOG.trace("ProgramBody结点设置完毕");
        return node;
    }

    private TreeNode matchIDINTCHAR(TokenType type) {
        TreeNode node = null;
        Token token = getToken();
        if (token != null) {
            if (token.getType().equals(type)) {
                node = node(token.getValue());
            } else {
                errors.add("Unexpected Token. A " + type.name() + " token expected near " + token.getValue()
                        + " at [" + token.getLine() + ":" + token.getColumn() + "]");
            }
        } else {
            errors.add("Unexpected EOF. A ID token expected near " + lastRead.getValue()
                    + " at [" + lastRead.getLine() + ":" + lastRead.getColumn() + "]");
        }
        return node;
    }

    private void error(TokenType... types) {
        StringBuilder sb = new StringBuilder("Unexpected token. /");
        for (TokenType t : types) {
            sb.append(t.getStr());
            sb.append("/");
        }
        sb.append(" expected. Near ");
        sb.append(lastRead.getValue());
        sb.append(" at [");
        sb.append(lastRead.getLine());
        sb.append(":");
        sb.append(lastRead.getColumn());
        sb.append("]");
        errors.add(sb.toString());
    }

    /**
     * (3) [ProgramName] -> ID  {ID}
     */
    private TreeNode programName() {
        TreeNode node = node("ProgramName");
        LOG.trace("构造 ProgramName 结点");
        node.setChild(matchIDINTCHAR(TokenType.ID));
        LOG.trace("ProgramName 结点设置完毕");
        return node;
    }

    /**
     * (5) [TypeDecPart] -> Ɛ          {VAR,PROCEDURE,BEGIN}
     * (6) [TypeDecPart] -> [TypeDec]  {TYPE}
     */
    private TreeNode typeDecPart() {
        TreeNode node = node("TypeDecPart");
        LOG.trace("构造 TypeDecPart 结点");
        Token token = peekToken();
        TokenType type = token.getType();
        TreeNode child = null;
        if (type.equals(TokenType.VAR) || type.equals(TokenType.PROCEDURE) || type.equals(TokenType.BEGIN)) {
            child = node("Ɛ");
        } else if (type.equals(TokenType.TYPE)) {
            child = typeDec();
        } else {
            error(TokenType.VAR, TokenType.PROCEDURE, TokenType.BEGIN, TokenType.TYPE);
        }
        node.setChild(child);
        LOG.trace("TypeDecPart 结点设置完毕");
        return node;
    }

    /**
     * (30) [VarDecPart] -> Ɛ         {PROCEDURE,BEGIN}
     * (31) [VarDecPart] -> [VarDec]  {VAR}
     */
    private TreeNode varDecPart() {
        TreeNode node = node("VarDecPart");
        LOG.trace("构造 VarDecPart 结点");
        Token token = peekToken();
        TokenType type = token.getType();
        TreeNode child = null;
        if (type.equals(TokenType.PROCEDURE) || type.equals(TokenType.BEGIN)) {
            child = node("Ɛ");
        } else if (type.equals(TokenType.VAR)) {
            child = typeDec();
        } else {
            error(TokenType.PROCEDURE, TokenType.BEGIN, TokenType.VAR);
        }
        LOG.trace("VarDecPart 结点设置完毕");
        node.setChild(child);
        return node;
    }

    /**
     * (39) [ProcDecPart] -> Ɛ           {begin}
     * (40) [ProcDecPart] -> [ProcDec]  {procedure}
     */
    private TreeNode procDecPart() {
        TreeNode node = node("ProcDecPart");
        LOG.trace("构造 ProcDecPart 结点");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case BEGIN:
                node.setChild(node("Ɛ"));
                break;
            case PROCEDURE:
                node.setChild(procDec());
                break;
            default:
                error(TokenType.BEGIN, TokenType.PROCEDURE);
        }
        LOG.trace("ProcDecPart 结点设置完毕");
        return node;
    }


    /**
     * (7) [TypeDec] -> type [TypeDecList] {type}
     */
    private TreeNode typeDec() {
        TreeNode node = node("TypeDec");
        LOG.trace("构造 TypeDec 结点");
        node.setChild(match(TokenType.TYPE.getStr()), typeDecList());
        LOG.trace("TypeDec 结点设置完毕");
        return node;
    }

    /**
     * (8) [TypeDecList] -> [TypeId] = [TypeDef] ; [TypeDecMore] {ID}
     */
    private TreeNode typeDecList() {
        TreeNode node = node("TypeDecList");
        LOG.trace("构造 TypeDecList 结点");
        node.setChild(typeId(), match(TokenType.EQ.getStr()), typeDef(),
                match(TokenType.SEMI.getStr()), typeDecMore());
        LOG.trace("TypeDecList 结点设置完毕");
        return node;
    }

    /**
     * (11) [TypeId] -> ID  {ID}
     */
    private TreeNode typeId() {
        TreeNode node = node("TypeID");
        LOG.trace("构造 TypeID 结点");
        node.setChild(matchIDINTCHAR(TokenType.ID));
        LOG.trace("TypeID 结点设置完毕");
        return node;
    }

    /**
     * (12) [TypeDef] -> [BaseType]       {integer,char}
     * (13) [TypeDef] -> [StructureType]  {array,record}
     * (14) [TypeDef] -> [ID]             {ID}
     */
    private TreeNode typeDef() {
        TreeNode node = node("TypeDef");
        LOG.trace("构造 TypeDef 结点");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case INTEGER:
            case CHAR:
                node.setChild(baseType());
                break;
            case ARRAY:
            case RECORD:
                node.setChild(structureType());
                break;
            case ID:
                node.setChild(matchIDINTCHAR(TokenType.ID));
                break;
            default:
                error(TokenType.INTEGER, TokenType.CHAR, TokenType.ARRAY, TokenType.RECORD, TokenType.ID);
        }
        LOG.trace("TypeDef 结点设置完毕");
        return node;
    }

    /**
     * ( 9) [TypeDecMore] -> Ɛ              {var,procedure,begin}
     * (10) [TypeDecMore] ->[TypeDecList]   {ID}
     */
    private TreeNode typeDecMore() {
        TreeNode node = node("typeDefMore");
        LOG.trace("构造 typeDefMore 结点");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case VAR:
            case PROCEDURE:
            case BEGIN:
                node.setChild(node("Ɛ"));
                break;
            case ID:
                node.setChild(typeDecList());
                break;
            default:
                error(TokenType.VAR, TokenType.PROCEDURE, TokenType.BEGIN, TokenType.ID);
        }
        LOG.trace("typeDefMore 结点设置完毕");
        return node;
    }

    /**
     * (41) [ProcDec] -> procedure [ProcName] ( [ParamList] ) ; [ProcDecPart][ProcBody][ProcDecMore] {procedure}
     */
    private TreeNode procDec() {
        TreeNode node = node("ProcDec");
        LOG.trace("构造 ProcDec 结点");
        node.setChild(match(TokenType.PROCEDURE.getStr()), procName(),
                match(TokenType.LPAREN.getStr()), paramList(), match(TokenType.RPAREN.getStr()),
                match(TokenType.SEMI.getStr()), procDecPart(), procBody(), procDecMore());
        LOG.trace("ProcDec 结点设置完毕");
        return node;
    }

    /**
     * (12) [BaseType] -> integer
     * (12) [BaseType] -> char
     */
    private TreeNode baseType() {
        TreeNode node = node("BaseType");
        LOG.trace("构造 BaseType 结点");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case INTEGER:
                node.setChild(match(TokenType.INTEGER.getStr()));
                break;
            case CHAR:
                node.setChild(match(TokenType.CHAR.getStr()));
                break;
            default:
                error(TokenType.INTEGER, TokenType.CHAR);
        }
        LOG.trace("BaseType 结点设置完毕");
        return node;
    }

    /**
     * (17) [StructureType] -> [ArrayType] {array}
     * (18) [StructureType] -> [RecType]   {record}
     */
    private TreeNode structureType() {
        TreeNode node = node("StructureType");
        LOG.trace("构造 StructureType 结点");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case ARRAY:
                node.setChild(arrayType());
                break;
            case RECORD:
                node.setChild(recType());
                break;
            default:
                error(TokenType.ARRAY, TokenType.RECORD);
        }
        LOG.trace("StructureType 结点设置完毕");
        return node;
    }

    /**
     * (44) [ProcName] -> ID
     */
    private TreeNode procName() {
        TreeNode node = node("ProcName");
        LOG.trace("构造 ProcName 结点");
        node.setChild(matchIDINTCHAR(TokenType.ID));
        LOG.trace("ProcName 结点设置完毕");
        return node;
    }

    /**
     * (45) [ParamList] -> Ɛ                {)}
     * (46) [ParamList] -> [ParamDecList]   {integer,char,array,record,id,var}
     */
    private TreeNode paramList() {
        TreeNode node = node("ParamList");
        LOG.trace("构造 ParamList 结点");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case RPAREN:
                node.setChild(match(TokenType.RPAREN.getStr()));
                break;
            case INTEGER:
            case CHAR:
            case ARRAY:
            case RECORD:
            case ID:
            case VAR:
                node.setChild(paramDecList());
                break;
            default:
                error(TokenType.RPAREN, TokenType.INTEGER, TokenType.CHAR, TokenType.ARRAY,
                        TokenType.RECORD, TokenType.ID, TokenType.VAR);
        }
        LOG.trace("ParamList 结点设置完毕");
        return node;
    }

    /**
     * (56) [ProcBody] -> [ProgramBody] {begin}
     */
    private TreeNode procBody() {
        TreeNode node = node("ProcBody");
        LOG.trace("构造 ProcBody 结点");
        node.setChild(programBody());
        LOG.trace("ProcBody 结点设置完毕");
        return node;
    }

    /**
     * (42) [ProcDecMore] -> Ɛ          {begin}
     * (43) [ProcDecMore] -> [ProcDec]  {procedure}
     */
    private TreeNode procDecMore() {
        TreeNode node = node("ProcDecMore");
        LOG.trace("构造 ProcDecMore 结点");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case BEGIN:
                node.setChild(node("Ɛ"));
                break;
            case PROCEDURE:
                node.setChild(procDec());
            default:
                error(TokenType.BEGIN, TokenType.PROCEDURE);
        }
        LOG.trace("ProcDecMore 结点设置完毕");
        return node;
    }

    /**
     * (19) [ArrayType] -> array [ [Low] . . [Top] ] of [BaseType]
     */
    private TreeNode arrayType() {
        TreeNode node = node("ArrayType");
        LOG.trace("构造 ArrayType 结点");
        node.setChild(match(TokenType.ARRAY.getStr()), match(TokenType.LMIDPAREN.getStr()), low(),
                match(TokenType.COLON.getStr()), match(TokenType.COLON.getStr()), top(),
                match(TokenType.RMIDPAREN.getStr()), match(TokenType.OF.getStr()), baseType());
        LOG.trace("ArrayType 结点设置完毕");
        return node;
    }

    /**
     * (22) [RecType] -> record [FieldDecList] end
     */
    private TreeNode recType() {
        TreeNode node = node("RecType");
        LOG.trace("构造 RecType 结点");
        node.setChild(match(TokenType.RECORD.getStr()), filedDecList(), match(TokenType.END.getStr()));
        LOG.trace("RecType 结点设置完毕");
        return node;
    }

    /**
     * (47) [ParamDecList] -> [Param] [ParamMore]
     */
    private TreeNode paramDecList() {
        TreeNode node = node("ParamDecList");
        LOG.trace("构造 ParamDecList 结点");
        node.setChild(param(), paramMore());
        LOG.trace("ParamDecList 结点设置完毕");
        return node;
    }

    /**
     * (20) [Low] -> INTC
     */
    private TreeNode low() {
        TreeNode node = node("Low");
        LOG.trace("构造 Low 结点");
        node.setChild(matchIDINTCHAR(TokenType.INTC));
        LOG.trace("Low 结点设置完毕");
        return node;
    }

    /**
     * (21) [Top] -> INTC
     */
    private TreeNode top() {
        TreeNode node = node("Top");
        LOG.trace("构造 Top 结点");
        node.setChild(matchIDINTCHAR(TokenType.INTC));
        LOG.trace("Top 结点设置完毕");
        return node;
    }

    /**
     * Ɛ
     * (23) [FiledDecList] -> [BaseType] [IdList] ; [FiledDecMore]  {integer,char}
     * (24) [FiledDecList] -> [ArrayType] [IdList];[FiledDecMore]   {array}
     */
    private TreeNode filedDecList() {
        TreeNode node = node("FiledDecList");
        LOG.trace("构造 FiledDecList 结点");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case INTEGER:
            case CHAR:
                node.setChild(baseType(), idList(), match(TokenType.SEMI.getStr()), filedDecMore());
                break;
            case ARRAY:
                node.setChild(arrayType(), idList(), filedDecMore());
                break;
            default:
                error(TokenType.INTEGER, TokenType.CHAR, TokenType.ARRAY);
        }
        LOG.trace("FiledDecList 结点设置完毕");
        return node;
    }

    /**
     * (50) [Param] -> [TypeDef] [FormList]         {integer,char,array,record,id}
     * (51) [Param] -> var [TypeDef] [FormList]     {var}
     */
    private TreeNode param() {
        TreeNode node = node("Param");
        LOG.trace("构造 Param 结点");

        LOG.trace("Param 结点设置完毕");
        return node;
    }

    private TreeNode paramMore() {
        TreeNode node = node("ParamMore");
        LOG.trace("构造 ParamMore 结点");

        LOG.trace("ParamMore 结点设置完毕");
        return node;
    }

    private TreeNode idList() {
        TreeNode node = node("IdList");
        LOG.trace("构造 IdList 结点");

        LOG.trace("IdList 结点设置完毕");
        return node;
    }

    private TreeNode filedDecMore() {
        TreeNode node = node("FiledDecMore");
        LOG.trace("构造 FileDdecMore 结点");

        LOG.trace("FiledDecMore 结点设置完毕");
        return node;
    }

}
