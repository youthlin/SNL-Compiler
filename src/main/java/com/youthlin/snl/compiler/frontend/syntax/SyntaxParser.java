package com.youthlin.snl.compiler.frontend.syntax;

import com.youthlin.snl.compiler.frontend.lexer.Lexer;
import com.youthlin.snl.compiler.frontend.lexer.LexerResult;
import com.youthlin.snl.compiler.frontend.lexer.Token;
import com.youthlin.snl.compiler.frontend.lexer.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static com.youthlin.snl.compiler.frontend.lexer.TokenType.ERROR;

/**
 * Created by lin on 2016-05-28-028.
 * 语法分析器
 */
public abstract class SyntaxParser {
    private static final Logger LOG = LoggerFactory.getLogger(SyntaxTree.class);
    private final Token errorToken = new Token(ERROR);
    private int currentTokenIndex;
    protected List<Token> list;
    protected Token lastRead = errorToken;
    protected List<String> errors;

    public abstract ParseResult parse(List<Token> tokenList);

    public ParseResult parse(Reader reader) {
        errors = new ArrayList<>();
        ParseResult result = new ParseResult();
        Lexer lexer = new Lexer();
        try {
            LexerResult lexerResult = lexer.getResult(reader);
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
        return parse(list);
    }

    /**
     * 获取下一个 Token.
     * 因为词法分析返回的 List 是 ArrayList,
     * 因此这里直接使用随机存取的 get 方式。
     *
     * @return Next Token. null if there has no more token.
     */
    protected Token getToken() {
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

    protected Token peekToken() {
        Token token = errorToken;
        if (currentTokenIndex < list.size()) {
            token = list.get(currentTokenIndex);
        }
        return token;
    }

    protected TreeNode node(String value) {
        return new TreeNode(value);
    }

    protected TreeNode node() {
//        return new TreeNode("ɛ");
//        return new TreeNode("Ɛ");
        return new TreeNode("空");
    }

    protected TreeNode match(TokenType expected) {
        Token input = getToken();
        TreeNode node = null;
        if (input != null) {
            TokenType type = input.getType();
            if (type.equals(expected)) {
                LOG.trace("已匹配" + input);
                node = node(expected.getStr());
                //标识符，数字，字符，值设为正确的值，而不是id,intc,character
                switch (type) {
                    case ID:
                    case INTC:
                    case CHARACTER:
                        node = node(input.getValue());
                }
            } else
                errors.add("Unexpected token near `" + input.getValue() + "`. `"
                        + expected.getStr() + "` expected. "
                        + " at [" + input.getLine() + ":" + input.getColumn() + "]");
        } else {
            errors.add("Unexpected EOF. No more tokens at input stream.");
        }
        return node;
    }

    protected void error(TokenType... types) {
        LOG.warn("匹配错误" + peekToken());
        StringBuilder sb = new StringBuilder("Unexpected token near `" + lastRead.getValue() + "`. |");
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
}
