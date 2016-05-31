package com.youthlin.snl.compiler.frontend.parser.recursivedescent;

import com.youthlin.snl.compiler.frontend.parser.SyntaxParser;
import com.youthlin.snl.compiler.frontend.parser.ParseResult;
import com.youthlin.snl.compiler.frontend.parser.SyntaxTree;
import com.youthlin.snl.compiler.frontend.parser.TreeNode;
import com.youthlin.snl.compiler.frontend.lexer.Lexer;
import com.youthlin.snl.compiler.frontend.lexer.Token;
import com.youthlin.snl.compiler.frontend.lexer.TokenType;
import com.youthlin.snl.compiler.frontend.lexer.LexerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.youthlin.snl.compiler.frontend.lexer.TokenType.*;

/**
 * Created by lin on 2016-05-28-028.
 * 递归下降语法分析
 */
public class RCParser implements SyntaxParser {
    private static final Logger LOG = LoggerFactory.getLogger(RCParser.class);
    private final Token errorToken = new Token(ERROR);
    private int currentTokenIndex;
    private List<Token> list;
    private List<String> errors;
    private Token lastRead = errorToken;

    @Override
    public ParseResult parse(InputStream in) {
        errors = new ArrayList<>();
        ParseResult result = new ParseResult();

        Lexer lexer = new Lexer();
        try {
            LexerResult lexerResult = lexer.getResult(in);
            if (lexerResult.getErrors().size() == 0) {
                list = lexerResult.getTokenList();
            } else {
                //词法分析有错，直接返回
                errors.add("Lexer Error.");
                errors.addAll(lexerResult.getErrors());
                result.setErrors(errors);
                return result;
            }
        } catch (IOException e) {
            errors.add("读取源代码出错！" + e.getMessage());
            result.setErrors(errors);
            return result;
        }
        //无词法错误，Token列表
        for (Token t : list) LOG.trace(t.toString());

        result.setTree(new SyntaxTree(program()));

        //此时输入流中应该为空
        if (getToken() != null) {
            LOG.warn("源程序太长");
            errors.add("Source code too long.");
        }

        if (errors.size() == 0) LOG.debug("语法分析成功");
        else LOG.warn("分析完成，存在错误");

        result.setErrors(errors);
        return result;
    }

    /**
     * 获取下一个 Token.
     * 因为词法分析返回的 List 是 ArrayList,
     * 因此这里直接使用随机存取的 get 方式。
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
            LOG.trace("输入流已空");
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

    private TreeNode node(String value) {
        return new TreeNode(value);
    }

    private TreeNode node() {
//        return new TreeNode("ɛ");
//        return new TreeNode("Ɛ");
        return new TreeNode("空");
    }

    private TreeNode match(TokenType expected) {
        Token input = getToken();
        TreeNode node = null;
        if (input != null) {
            if (input.getType().equals(expected)) {
                LOG.trace("已匹配" + input);
                node = node(expected.getStr());
            } else
                errors.add("Unexpected token:|" + input.getValue() + "|. "
                        + expected.getStr() + " expected. Near " + input.getValue()
                        + " at [" + input.getLine() + ":" + input.getColumn() + "]");
        } else {
            errors.add("Unexpected EOF. No more tokens at input stream.");
        }
        return node;
    }

    private TreeNode matchIDINTCHAR(TokenType type) {
        TreeNode node = null;
        Token token = getToken();
        if (token != null) {
            if (token.getType().equals(type)) {
                node = node(token.getValue());
            } else {
                errors.add("Unexpected Token near |" + token.getValue()
                        + "| A " + type.name() + " token expected. "
                        + " at [" + token.getLine() + ":" + token.getColumn() + "]");
            }
        } else {
            errors.add("Unexpected EOF near |" + lastRead.getValue() + "|. A ID token expected. "
                    + " at [" + lastRead.getLine() + ":" + lastRead.getColumn() + "]");
        }
        return node;
    }

    private void error(TokenType... types) {
        LOG.warn("匹配错误" + peekToken());
        StringBuilder sb = new StringBuilder("Unexpected token near |" + lastRead.getValue() + "|. |");
        for (TokenType t : types) {
            sb.append(t.getStr());
            sb.append("|");
        }
        sb.append(" expected. ");
        sb.append(" at [");
        sb.append(lastRead.getLine());
        sb.append(":");
        sb.append(lastRead.getColumn());
        sb.append("]");
        errors.add(sb.toString());
    }

    /**
     * (1) [Program] -> [ProgramHead] [DeclarePart] [ProgramBody] .  {program}
     */
    private TreeNode program() {
        TreeNode root = node("Program");
        LOG.trace("构造根结点");
        root.setChildren(programHead(), declarePart(), programBody(), match(EOF));
        LOG.trace("根结点设置完毕");
        return root;
    }

    /**
     * (2) [ProgramHead] -> program [ProgramName] {program}
     */
    private TreeNode programHead() {
        TreeNode pHead = node("ProgramHead");
        LOG.trace("构造ProgramHead结点");
        pHead.setChildren(match(PROGRAM), programName());
        LOG.trace("ProgramHead结点设置完毕");
        return pHead;
    }

    /**
     * (4) [DeclarePart] -> [TypeDecPart] [VarDecPart] [ProcDecPart] {type,var,procedure,begin}
     */
    private TreeNode declarePart() {
        TreeNode node = node("DeclarePart");
        LOG.trace("构造DeclarePart结点");
        node.setChildren(typeDecPart(), varDecPart(), procDecpart());
        LOG.trace("DeclarePart结点设置完毕");
        return node;
    }

    /**
     * (57) [ProgramBody] -> begin [StmList] end    {begin}
     */
    private TreeNode programBody() {
        TreeNode node = node("ProgramBody");
        LOG.trace("构造ProgramBody结点");
        node.setChildren(match(BEGIN), stmList(), match(END));
        LOG.trace("ProgramBody结点设置完毕");
        return node;
    }


    /**
     * (3) [ProgramName] -> ID  {ID}
     */
    private TreeNode programName() {
        TreeNode node = node("ProgramName");
        LOG.trace("构造 ProgramName 结点");
        node.setChildren(matchIDINTCHAR(ID));
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
        switch (peekToken().getType()) {
            case VAR:
            case PROCEDURE:
            case BEGIN:
                node.setChildren(node());
                break;
            case TYPE:
                node.setChildren(typeDec());
                break;
            default:
                error(VAR, PROCEDURE, BEGIN, TYPE);
        }
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
        switch (peekToken().getType()) {
            case PROCEDURE:
            case BEGIN:
                node.setChildren(node());
                break;
            case VAR:
                node.setChildren(varDec());
                break;
            default:
                error(PROCEDURE, BEGIN, VAR);
        }
        LOG.trace("VarDecPart 结点设置完毕");
        return node;
    }

    /**
     * (32) [VarDec] -> VAR [VarDecList]
     */
    private TreeNode varDec() {
        TreeNode node = node("VarDec");
        LOG.trace("构造 VarDec 结点");
        node.setChildren(match(VAR), varDecList());
        LOG.trace("VarDec 结点设置完毕");
        return node;
    }

    /**
     * (33) [VarDecList] -> [TypeDef] [VarIdList] ; [VarDecMore]
     */
    private TreeNode varDecList() {
        TreeNode node = node("VarDecList");
        LOG.trace("构造 VarDecList 结点");
        node.setChildren(typeDef(), varIdList(), match(SEMI), varDecMore());
        LOG.trace("VarDecList 结点设置完毕");
        return node;
    }

    /**
     * (36) [VarIdList] -> ID [VarIdMore]
     */
    private TreeNode varIdList() {
        TreeNode node = node("varIdList");
        LOG.trace("构造 varIdList 结点");
        node.setChildren(matchIDINTCHAR(ID), varIdMore());
        LOG.trace("varIdList 结点设置完毕");
        return node;
    }

    /**
     * (34) [VarDecMore] -> Ɛ           {procedure begin}
     * (35) [VarDecMore] -> [VarDecList]   {integer char array record id}
     */
    private TreeNode varDecMore() {
        TreeNode node = node("varDecMore");
        LOG.trace("构造 varDecMore 结点");
        switch (peekToken().getType()) {
            case PROCEDURE:
            case BEGIN:
                node.setChildren(node());
                break;
            case INTEGER:
            case CHAR:
            case ARRAY:
            case RECORD:
            case ID:
                node.setChildren(varDecList());
                break;
            default:
                error(PROCEDURE, BEGIN, INTEGER, CHAR, ARRAY, RECORD, ID);
        }
        LOG.trace("varDecMore 结点设置完毕");
        return node;
    }

    /**
     * (37) [VarIdMore] -> Ɛ                {;}
     * (38) [VarIdMore] -> , [VarIdList]    {,}
     */
    private TreeNode varIdMore() {
        TreeNode node = node("VarIdMore");
        LOG.trace("构造 VarIdMore 结点");
        switch (peekToken().getType()) {
            case SEMI:
                node.setChildren(node());
                break;
            case COMMA:
                node.setChildren(match(COMMA), varIdList());
                break;
            default:
                error(SEMI, COMMA);
        }
        LOG.trace("VarIdMore 结点设置完毕");
        return node;
    }

    /**
     * (39) [ProcDecpart] -> Ɛ           {begin}
     * (40) [ProcDecpart] -> [ProcDec]  {procedure}
     */
    private TreeNode procDecpart() {
        TreeNode node = node("ProcDecpart");
        LOG.trace("构造 ProcDecpart 结点");
        switch (peekToken().getType()) {
            case BEGIN:
                node.setChildren(node());
                break;
            case PROCEDURE:
                node.setChildren(procDec());
                break;
            default:
                error(BEGIN, PROCEDURE);
        }
        LOG.trace("ProcDecpart 结点设置完毕");
        return node;
    }

//    /**
//     * (42) [ProcDecMore] -> Ɛ          {begin}
//     * (43) [ProcDecMore] -> [ProcDec]  {procedure}
//     */
//    private TreeNode procDecMore() {
//        TreeNode node = node("ProcDecMore");
//        LOG.trace("构造 ProcDecMore 结点");
//        Token token = peekToken();
//        TokenType type = token.getType();
//        switch (type) {
//            case BEGIN:
//                node.setChildren(node());
//                break;
//            case PROCEDURE:
//                node.setChildren(procDec());
//                break;
//            default:
//                error(BEGIN, PROCEDURE);
//        }
//        LOG.trace("ProcDecMore 结点设置完毕");
//        return node;
//    }

    /**
     * (7) [TypeDec] -> type [TypeDecList] {type}
     */
    private TreeNode typeDec() {
        TreeNode node = node("TypeDec");
        LOG.trace("构造 TypeDec 结点");
        node.setChildren(match(TYPE), typeDecList());
        LOG.trace("TypeDec 结点设置完毕");
        return node;
    }

    /**
     * (8) [TypeDecList] -> [TypeId] = [TypeDef] ; [TypeDecMore] {ID}
     */
    private TreeNode typeDecList() {
        TreeNode node = node("TypeDecList");
        LOG.trace("构造 TypeDecList 结点");
        node.setChildren(typeId(), match(EQ), typeDef(), match(SEMI), typeDecMore());
        LOG.trace("TypeDecList 结点设置完毕");
        return node;
    }

    /**
     * (11) [TypeId] -> ID  {ID}
     */
    private TreeNode typeId() {
        TreeNode node = node("TypeID");
        LOG.trace("构造 TypeID 结点");
        node.setChildren(matchIDINTCHAR(ID));
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
                node.setChildren(baseType());
                break;
            case ARRAY:
            case RECORD:
                node.setChildren(structureType());
                break;
            case ID:
                node.setChildren(matchIDINTCHAR(ID));
                break;
            default:
                error(INTEGER, CHAR, ARRAY, RECORD, ID);
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
                node.setChildren(node());
                break;
            case ID:
                node.setChildren(typeDecList());
                break;
            default:
                error(VAR, PROCEDURE, BEGIN, ID);
        }
        LOG.trace("typeDefMore 结点设置完毕");
        return node;
    }

    /**
     * (41) [ProcDec] ->
     * procedure [ProcName] ( [ParamList] ) ; [ProcDecPart][ProcBody][ProcDecMore] {procedure}
     */
    private TreeNode procDec() {
        TreeNode node = node("ProcDec");
        LOG.trace("构造 ProcDec 结点");
        node.setChildren(match(PROCEDURE), procName(),
                match(LPAREN), paramList(), match(RPAREN), match(SEMI),
                procDecPart(), procBody(), procDecpart());
        LOG.trace("ProcDec 结点设置完毕");
        return node;
    }

    /**
     * (55) [ProcDecPart] -> [DeclarePart]
     */
    private TreeNode procDecPart() {
        TreeNode node = node("ProcDecPart");
        LOG.trace("构造 ProcDecPart 结点");
        node.setChildren(declarePart());
        LOG.trace("ProcDecPart 结点设置完毕");
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
                node.setChildren(match(INTEGER));
                break;
            case CHAR:
                node.setChildren(match(CHAR));
                break;
            default:
                error(INTEGER, CHAR);
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
                node.setChildren(arrayType());
                break;
            case RECORD:
                node.setChildren(recType());
                break;
            default:
                error(ARRAY, RECORD);
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
        node.setChildren(matchIDINTCHAR(ID));
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
                node.setChildren(node());
                break;
            case INTEGER:
            case CHAR:
            case ARRAY:
            case RECORD:
            case ID:
            case VAR:
                node.setChildren(paramDecList());
                break;
            default:
                error(RPAREN, INTEGER, CHAR, ARRAY,
                        RECORD, ID, VAR);
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
        node.setChildren(programBody());
        LOG.trace("ProcBody 结点设置完毕");
        return node;
    }


    /**
     * (19) [ArrayType] -> array [ [Low] . . [Top] ] of [BaseType]
     */
    private TreeNode arrayType() {
        TreeNode node = node("ArrayType");
        LOG.trace("构造 ArrayType 结点");
        node.setChildren(match(ARRAY), match(LMIDPAREN),
                low(), match(UNDERRANGE), top(),
                match(RMIDPAREN), match(OF), baseType());
        LOG.trace("ArrayType 结点设置完毕");
        return node;
    }

    /**
     * (22) [RecType] -> record [FieldDecList] end
     */
    private TreeNode recType() {
        TreeNode node = node("RecType");
        LOG.trace("构造 RecType 结点");
        node.setChildren(match(RECORD), filedDecList(), match(END));
        LOG.trace("RecType 结点设置完毕");
        return node;
    }

    /**
     * (47) [ParamDecList] -> [Param] [ParamMore]
     */
    private TreeNode paramDecList() {
        TreeNode node = node("ParamDecList");
        LOG.trace("构造 ParamDecList 结点");
        node.setChildren(param(), paramMore());
        LOG.trace("ParamDecList 结点设置完毕");
        return node;
    }

    /**
     * (20) [Low] -> INTC
     */
    private TreeNode low() {
        TreeNode node = node("Low");
        LOG.trace("构造 Low 结点");
        node.setChildren(matchIDINTCHAR(INTC));
        LOG.trace("Low 结点设置完毕");
        return node;
    }

    /**
     * (21) [Top] -> INTC
     */
    private TreeNode top() {
        TreeNode node = node("Top");
        LOG.trace("构造 Top 结点");
        node.setChildren(matchIDINTCHAR(INTC));
        LOG.trace("Top 结点设置完毕");
        return node;
    }

    /**
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
                node.setChildren(baseType(), idList(), match(SEMI), filedDecMore());
                break;
            case ARRAY:
                node.setChildren(arrayType(), idList(), match(SEMI), filedDecMore());
                break;
            default:
                error(INTEGER, CHAR, ARRAY);
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
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case INTEGER:
            case CHAR:
            case ARRAY:
            case RECORD:
            case ID:
                node.setChildren(typeDef(), formList());
                break;
            case VAR:
                node.setChildren(match(VAR), typeDef(), formList());
                break;
            default:
                error(INTEGER, CHAR, ARRAY,
                        RECORD, ID, VAR);
        }
        LOG.trace("Param 结点设置完毕");
        return node;
    }

    /**
     * (48) [ParamMore] -> Ɛ                    {(}
     * (49) [ParamMore] -> ; [ParamDecList]     {;}
     */
    private TreeNode paramMore() {
        TreeNode node = node("ParamMore");
        LOG.trace("构造 ParamMore 结点");
        switch (peekToken().getType()) {
            case RPAREN:
                node.setChildren(node());
                break;
            case SEMI:
                node.setChildren(match(SEMI), paramDecList());
                break;
            default:
                error(RPAREN, SEMI);
        }
        LOG.trace("ParamMore 结点设置完毕");
        return node;
    }

    /**
     * (27) [IdList] -> ID [IdMore]
     */
    private TreeNode idList() {
        TreeNode node = node("IdList");
        LOG.trace("构造 IdList 结点");
        node.setChildren(matchIDINTCHAR(ID), idMore());
        LOG.trace("IdList 结点设置完毕");
        return node;
    }

    /**
     * (25) [FiledDecMore] -> Ɛ                 {end}
     * (26) [FiledDecMore] -> [FiledDecList]    {integer,char,array}
     */
    private TreeNode filedDecMore() {
        TreeNode node = node("FiledDecMore");
        LOG.trace("构造 FileDdecMore 结点");
        switch (peekToken().getType()) {
            case END:
                node.setChildren(node());
                break;
            case INTEGER:
            case CHAR:
            case ARRAY:
                node.setChildren(filedDecList());
                break;
            default:
                error(END, INTEGER, CHAR, ARRAY);
        }
        LOG.trace("FiledDecMore 结点设置完毕");
        return node;
    }

    /**
     * (52) [FormList] -> ID [FidMore]
     */
    private TreeNode formList() {
        TreeNode node = node("FormList");
        LOG.trace("构造 FormList 结点");
        node.setChildren(matchIDINTCHAR(ID), fidMore());
        LOG.trace("FormList 结点设置完毕");
        return node;
    }

    /**
     * (28) [IdMore] -> Ɛ           {;}
     * (29) [IdMore] -> , [IdList]  {,}
     */
    private TreeNode idMore() {
        TreeNode node = node("IdMore");
        LOG.trace("构造 IdMore 结点");
        switch (peekToken().getType()) {
            case SEMI:
                node.setChildren(node());
                break;
            case COMMA:
                node.setChildren(match(COMMA), idList());
                break;
            default:
                error(SEMI, COMMA);
        }
        LOG.trace("IdMore 结点设置完毕");
        return node;
    }

    /**
     * (53) [FidMore] -> Ɛ              {;)}
     * (54) [FidMore] -> , [FormList]   {,}
     */
    private TreeNode fidMore() {
        TreeNode node = node("FidMore");
        LOG.trace("构造 FidMore 结点");
        switch (peekToken().getType()) {
            case SEMI:
            case RPAREN:
                node.setChildren(node());
                break;
            case COMMA:
                node.setChildren(match(COMMA), formList());
                break;
            default:
                error(SEMI, RPAREN, COMMA);
        }
        LOG.trace("FidMore 结点设置完毕");
        return node;
    }

    /**
     * (58) [StmList] -> [Stm] [StmMore]
     */
    private TreeNode stmList() {
        TreeNode node = node("StmList");
        LOG.trace("构造 StmList 结点");
        node.setChildren(stm(), stmMore());
        LOG.trace("StmList 结点设置完毕");
        return node;
    }

    /**
     * (61) [Stm] -> [ConditionalStm]   {IF}
     * (62) [Stm] -> [LoopStm]          {WHILE}
     * (63) [Stm] -> [InputStm]         {READ}
     * (64) [Stm] -> [OutputStm]        {WRITE}
     * (65) [Stm] -> [ReturnStm]        {RETURN}
     * (66) [Stm] -> ID [AssCall]       {ID}
     */
    private TreeNode stm() {
        TreeNode node = node("Stm");
        LOG.trace("构造 Stm 结点");
        switch (peekToken().getType()) {
            case IF:
                node.setChildren(conditionalStm());
                break;
            case WHILE:
                node.setChildren(loopStm());
                break;
            case READ:
                node.setChildren(inputStm());
                break;
            case WRITE:
                node.setChildren(outputStm());
                break;
            case RETURN:
                node.setChildren(returnStm());
                break;
            case ID:
                node.setChildren(matchIDINTCHAR(ID), assCall());
                break;
            default:
                error(IF, WHILE, READ,
                        WRITE, RETURN, ID);
        }
        LOG.trace("Stm 结点设置完毕");
        return node;
    }

    /**
     * (59) [StmMore] -> Ɛ              {else fi end endwh}
     * (60) [StmMore] -> ; [StmList]    {;}
     */
    private TreeNode stmMore() {
        TreeNode node = node("StmMore");
        LOG.trace("构造 StmMore 结点");
        switch (peekToken().getType()) {
            case ELSE:
            case FI:
            case END:
            case ENDWH:
                node.setChildren(node());
                break;
            case SEMI:
                node.setChildren(match(SEMI), stmList());
                break;
            default:
                error(ELSE, FI, END, ENDWH, SEMI);
        }
        LOG.trace("StmMore 结点设置完毕");
        return node;
    }

    /**
     * (70) [ConditionalStm] -> IF [RelExp] THEN [StmList] ELSE [StmList] FI
     */
    private TreeNode conditionalStm() {
        TreeNode node = node("ConditionalStm");
        LOG.trace("构造 ConditionalStm 结点");
        node.setChildren(match(IF), relExp(), match(THEN),
                stmList(), match(ELSE), stmList(), match(FI));
        LOG.trace("ConditionalStm 结点设置完毕");
        return node;
    }

    /**
     * (71) [LoopStm] -> WHILE [RelExp] DO [StmList] ENDWH
     */
    private TreeNode loopStm() {
        TreeNode node = node("LoopStm");
        LOG.trace("构造 LoopStm 结点");
        node.setChildren(match(WHILE), relExp(), match(DO), stmList(), match(ENDWH));
        LOG.trace("LoopStm 结点设置完毕");
        return node;
    }

    /**
     * (72) [InputStm] -> Read ( [Invar] )
     */
    private TreeNode inputStm() {
        TreeNode node = node("InputStm");
        LOG.trace("构造 InputStm 结点");
        node.setChildren(match(READ), match(LPAREN), invar(), match(RPAREN));
        LOG.trace("InputStm 结点设置完毕");
        return node;
    }

    /**
     * (74) [OutputStm] -> write ( [Exp] )
     */
    private TreeNode outputStm() {
        TreeNode node = node("OutputStm");
        LOG.trace("构造 OutputStm 结点");
        node.setChildren(match(WRITE), match(LPAREN), exp(), match(RPAREN));
        LOG.trace("OutputStm 结点设置完毕");
        return node;
    }

    /**
     * (75) [ReturnStm] -> RETURN
     */
    private TreeNode returnStm() {
        TreeNode node = node("ReturnStm");
        LOG.trace("构造 ReturnStm 结点");
        node.setChildren(match(RETURN));
        LOG.trace("ReturnStm 结点设置完毕");
        return node;
    }

    /**
     * 67应包含`[`,`.`
     * (67) [AssCall] -> [AssignmentRest]   {:=}
     * (68) [AssCall] -> [CallStmRest]      {(}
     */
    private TreeNode assCall() {
        TreeNode node = node("AssCall");
        LOG.trace("构造 AssCall 结点");
        switch (peekToken().getType()) {
            case ASSIGN:
            case LMIDPAREN:
            case DOT:
                node.setChildren(assignmentRest());
                break;
            case LPAREN:
                node.setChildren(callStmRest());
                break;
            default:
                error(ASSIGN, LPAREN);
        }
        LOG.trace("AssCall 结点设置完毕");
        return node;
    }

    /**
     * (81) [RelExp] -> [Exp] [OtherRelE]
     */
    private TreeNode relExp() {
        TreeNode node = node("RelExp");
        LOG.trace("构造 RelExp 结点");
        node.setChildren(exp(), otherRelE());
        LOG.trace("RelExp 结点设置完毕");
        return node;
    }

    /**
     * (73) [Invar] -> ID
     */
    private TreeNode invar() {
        TreeNode node = node("Invar");
        LOG.trace("构造 Invar 结点");
        node.setChildren(matchIDINTCHAR(ID));
        LOG.trace("Invar 结点设置完毕");
        return node;
    }

    /**
     * (83) [Exp] -> [Term] [OtherTerm]
     */
    private TreeNode exp() {
        TreeNode node = node("Exp");
        LOG.trace("构造 Exp 结点");
        node.setChildren(term(), otherTerm());
        LOG.trace("Exp 结点设置完毕");
        return node;
    }

    /**
     * (69) [AssignmentRest] -> [VariMore] := [Exp]
     */
    private TreeNode assignmentRest() {
        TreeNode node = node("AssignmentRest");
        LOG.trace("构造 AssignmentRest 结点");
        node.setChildren(variMore(), match(ASSIGN), exp());
        LOG.trace("AssignmentRest 结点设置完毕");
        return node;
    }

    /**
     * (76) [CallStmRest] -> ( [ActParamList] )
     */
    private TreeNode callStmRest() {
        TreeNode node = node("CallStmRest");
        LOG.trace("构造 CallStmRest 结点");
        node.setChildren(match(LPAREN), actParamList(), match(RPAREN));
        LOG.trace("CallStmRest 结点设置完毕");
        return node;
    }

    /**
     * (82) [OtherRelE] -> [CmpOp] [Exp]
     */
    private TreeNode otherRelE() {
        TreeNode node = node("OtherRelE");
        LOG.trace("构造 OtherRelE 结点");
        node.setChildren(cmpOp(), exp());
        LOG.trace("OtherRelE 结点设置完毕");
        return node;
    }

    /**
     * (86) [Term] -> [Factor] [OtherFactor]
     */
    private TreeNode term() {
        TreeNode node = node("Term");
        LOG.trace("构造 Term 结点");
        node.setChildren(factor(), otherFactor());
        LOG.trace("Term 结点设置完毕");
        return node;
    }

    /**
     * (84) [OtherTerm] -> Ɛ                {< = ] then else fi do endwh ) end ; COMMA}
     * (85) [OtherTerm] -> [AddOp] [Exp]    {+ -}
     */
    private TreeNode otherTerm() {
        TreeNode node = node("OtherTerm");
        LOG.trace("构造 OtherTerm 结点");
        switch (peekToken().getType()) {
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
                node.setChildren(node());
                break;
            case PLUS:
            case MINUS:
                node.setChildren(addOp(), exp());
                break;
            default:
                error(LT, EQ, RMIDPAREN, THEN,
                        ELSE, FI, DO, ENDWH,
                        RPAREN, END, SEMI, COMMA,
                        PLUS, MINUS);
        }
        LOG.trace("OtherTerm 结点设置完毕");
        return node;
    }

    /**
     * Predict集 93 应包含 `]`
     * (93) [VariMore] -> Ɛ             {:= * / + - < = then else fi do endwh ) end ; Comma}
     * (94) [VariMore] -> [ [Exp] ]     {[}
     * (95) [VariMore] -> . [FieldVar]  {.}
     */
    private TreeNode variMore() {
        TreeNode node = node("VariMore");
        LOG.trace("构造 VariMore 结点");
        switch (peekToken().getType()) {
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
            case RMIDPAREN:/**注意*/
                node.setChildren(node());
                break;
            case LMIDPAREN:
                node.setChildren(match(LMIDPAREN), exp(), match(RMIDPAREN));
                break;
            case DOT:
                node.setChildren(match(DOT), filedVar());
                break;
            default:
                error(ASSIGN, TIMES, OVER, PLUS, MINUS, LT, EQ, THEN, ELSE, FI,
                        DO, ENDWH, RPAREN, END, SEMI, COMMA, RMIDPAREN, DOT);
        }
        LOG.trace("VariMore 结点设置完毕");
        return node;
    }

    /**
     * (77) [ActParamList] -> Ɛ                     {)}
     * (78) [ActParamList] -> [Exp] [ActParamMore]  {( INTC ID}
     */
    private TreeNode actParamList() {
        TreeNode node = node("ActParamList");
        LOG.trace("构造 ActParamList 结点");
        switch (peekToken().getType()) {
            case RPAREN:
                node.setChildren(node());
                break;
            case LPAREN:
            case INTC:
            case ID:
                node.setChildren(exp(), actParamMore());
                break;
            default:
                error(RPAREN, LPAREN, INTC, ID);
        }
        LOG.trace("ActParamList 结点设置完毕");
        return node;
    }

    /**
     * ( 99) [CmpOp] -> <
     * (100) [CmpOp] -> =
     */
    private TreeNode cmpOp() {
        TreeNode node = node("CmpOp");
        LOG.trace("构造 CmpOp 结点");
        switch (peekToken().getType()) {
            case LT:
                node.setChildren(match(LT));
                break;
            case EQ:
                node.setChildren(match(EQ));
                break;
            default:
                error(LT, EQ);
        }
        LOG.trace("CmpOp 结点设置完毕");
        return node;
    }

    /**
     * (89) [Factor] -> ( [Exp] )   {(}
     * (90) [Factor] -> INTC        {INTC}
     * (91) [Factor] -> [Variable]  {ID}
     */
    private TreeNode factor() {
        TreeNode node = node("Factor");
        LOG.trace("构造 Factor 结点");
        switch (peekToken().getType()) {
            case LPAREN:
                node.setChildren(match(LPAREN), exp(), match(RPAREN));
                break;
            case INTC:
                node.setChildren(matchIDINTCHAR(INTC));
                break;
            case ID:
                node.setChildren(variable());
                break;
            default:
                error(LPAREN, INTC, ID);
        }
        LOG.trace("Factor 结点设置完毕");
        return node;
    }

    /**
     * (87) [OtherFactor] -> Ɛ                  { + - < = ] then else fi do endwh ) end ; COMMA}
     * (88) [OtherFactor] -> [MultiOp] [Term]   {* /}
     */
    private TreeNode otherFactor() {
        TreeNode node = node("OtherFactor");
        LOG.trace("构造 OtherFactor 结点");
        switch (peekToken().getType()) {
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
                node.setChildren(node());
                break;
            case TIMES:
            case OVER:
                node.setChildren(multiOp(), term());
                break;
            default:
                error(PLUS, MINUS, LT, EQ, RMIDPAREN, THEN, ELSE, FI, DO,
                        ENDWH, RPAREN, END, SEMI, COMMA, TIMES, OVER);
        }
        LOG.trace("OtherFactor 结点设置完毕");
        return node;
    }

    /**
     * (101) [AddOp] -> +
     * (102) [AddOp] -> -
     */
    private TreeNode addOp() {
        TreeNode node = node("AddOp");
        LOG.trace("构造 AddOp 结点");
        switch (peekToken().getType()) {
            case PLUS:
                node.setChildren(match(PLUS));
                break;
            case MINUS:
                node.setChildren(match(MINUS));
                break;
            default:
                error(PLUS, MINUS);
        }
        LOG.trace("AddOp 结点设置完毕");
        return node;
    }

    /**
     * (96) [FiledVar] -> ID [FiledVarMore]
     */
    private TreeNode filedVar() {
        TreeNode node = node("FiledVar");
        LOG.trace("构造 FiledVar 结点");
        node.setChildren(matchIDINTCHAR(ID), filedVarMore());
        LOG.trace("FiledVar 结点设置完毕");
        return node;
    }

    /**
     * (79) [ActParamMor -> Ɛ                   {)}
     * (80) [ActParamMor -> , [ActParamList]    {,}
     */
    private TreeNode actParamMore() {
        TreeNode node = node("FiledVar");
        LOG.trace("构造 FiledVar 结点");
        switch (peekToken().getType()) {
            case RPAREN:
                node.setChildren(node());
                break;
            case COMMA:
                node.setChildren(match(COMMA), actParamList());
                break;
            default:
                error(RPAREN, COMMA);
        }
        LOG.trace("FiledVar 结点设置完毕");
        return node;
    }

    /**
     * (92) [Variable] -> ID [VariMore]
     */
    private TreeNode variable() {
        TreeNode node = node("Variable");
        LOG.trace("构造 Variable 结点");
        node.setChildren(matchIDINTCHAR(ID), variMore());
        LOG.trace("Variable 结点设置完毕");
        return node;
    }

    /**
     * (103) [MultiOp] -> *
     * (104) [MultiOp] -> /
     */
    private TreeNode multiOp() {
        TreeNode node = node("MultiOp");
        LOG.trace("构造 MultiOp 结点");
        switch (peekToken().getType()) {
            case TIMES:
                node.setChildren(match(TIMES));
                break;
            case OVER:
                node.setChildren(match(OVER));
                break;
            default:
                error(TIMES, OVER);
        }
        LOG.trace("MultiOp 结点设置完毕");
        return node;
    }

    /**
     * (97) [FiledVarMore] -> Ɛ     {:= * / + - < = then else fi do endwh ) end ; COMMA}
     * (98) [FiledVarMore] -> [Exp] {[}
     */
    private TreeNode filedVarMore() {
        TreeNode node = node("FiledVarMore");
        LOG.trace("构造 FiledVarMore 结点");
        switch (peekToken().getType()) {
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
                node.setChildren(node());
                break;
            case LMIDPAREN:
                node.setChildren(match(LMIDPAREN), exp(), match(RMIDPAREN));
                break;
            default:
                error(ASSIGN, TIMES, OVER, PLUS, MINUS, LT, EQ, THEN,
                        ELSE, FI, DO, ENDWH, RPAREN, END, SEMI, COMMA, LMIDPAREN);
        }
        LOG.trace("FiledVarMore 结点设置完毕");
        return node;
    }

}
