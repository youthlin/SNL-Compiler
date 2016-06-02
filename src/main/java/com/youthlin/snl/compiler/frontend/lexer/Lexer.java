package com.youthlin.snl.compiler.frontend.lexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by lin on 2016-05-28-028.
 * 词法分析器
 */
public class Lexer {
    private enum State {
        Normal, InId, InNum, InComment, InChar, Error,
        InAssign, InRange
    }

    private static Logger LOG = LoggerFactory.getLogger(Lexer.class);
    private int getMeFirst = -1;
    private int line = 1;
    private int column = 0;
    private List<String> errors;
    private Reader reader;

    public LexerResult getResult(Reader reader) throws IOException {
        errors = new ArrayList<>();
        LexerResult result = new LexerResult();
        List<Token> list = new ArrayList<>();
        if (reader == null) {
            errors.add("Input stream must not be not null.");
            result.setErrors(errors);
            result.setTokenList(list);
            return result;
        }
        this.reader = reader;
        Token token = getToken();
        while (token != null) {
            list.add(token);
            token = getToken();
        }
        result.setTokenList(list);
        result.setErrors(errors);
        for (String error : errors) {
            LOG.warn(error);
        }
        return result;
    }

    private Token getToken() throws IOException {
        State state = State.Normal;
        LOG.trace("调用getToken方法");
        StringBuilder sb = new StringBuilder();
        int ch = getChar();
        while (ch != -1) {
            sb.append((char) ch);
            Token token;
            switch (state) {
                case Normal://region 开始识别
                    LOG.trace("进入Normal状态");
                    if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                        state = State.InId;
                    } else if (ch >= '0' && ch <= '9') {
                        state = State.InNum;
                    }//region 单字符分界符
                    else if (isBlank(ch)) {
                        //sb.substring(0, sb.length() - 1);//呵呵哒坑货
                        sb.deleteCharAt(sb.length() - 1);
                        state = State.Normal;
                    } else if (ch == '+') {
                        token = new Token(line, column, TokenType.PLUS, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '-') {
                        token = new Token(line, column, TokenType.MINUS, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '*') {
                        token = new Token(line, column, TokenType.TIMES, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '/') {
                        token = new Token(line, column, TokenType.OVER, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '(') {
                        token = new Token(line, column, TokenType.LPAREN, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == ')') {
                        token = new Token(line, column, TokenType.RPAREN, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '[') {
                        token = new Token(line, column, TokenType.LMIDPAREN, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == ']') {
                        token = new Token(line, column, TokenType.RMIDPAREN, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == ';') {
                        token = new Token(line, column, TokenType.SEMI, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == ',') {
                        token = new Token(line, column, TokenType.COMMA, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '=') {
                        token = new Token(line, column, TokenType.EQ, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else if (ch == '<') {
                        token = new Token(line, column, TokenType.LT, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    }//endregion
                    else if (ch == ':') {
                        state = State.InAssign;
                    } else if (ch == '{') {
                        sb.deleteCharAt(sb.length() - 1);//把【{】删除
                        state = State.InComment;
                    } else if (ch == '.') {
                        state = State.InRange;
                    } else if (ch == '\'') {
                        sb.deleteCharAt(sb.length() - 1);//把【'】删除
                        state = State.InChar;
                    } else {
                        LOG.trace("【ERROR】当前字符在预料之外:{}({})", (char) ch, ch);
                        state = State.Error;
                    }
                    //endregion
                    break;
                case InId://region 识别标识符
                    LOG.trace("进入InId状态");
                    if (isAlpha(ch) || isDigit(ch)) {
                        state = State.InId;
                    } else {
                        //当前字符已经不是`标识符`的组成部分，即已经把标识符识别出来了
                        LOG.trace("当前字符已经不属于标识符组成了:{}({})", showChar(ch), ch);
                        //当前字符不属于标识符，因此回退，下次分析再读
                        unGetChar(sb.charAt(sb.length() - 1));
                        token = new Token(line, column, TokenType.ID, sb.substring(0, sb.length() - 1));
                        LOG.debug("已识别Token:" + token);
                        token.checkKeyWords();
                        return token;
                    }
                    //endregion
                    break;
                case InNum://region 识别数字
                    LOG.trace("开始识别数字");
                    if (!isDigit(ch)) {
                        //数字识别完成
                        unGetChar(sb.charAt(sb.length() - 1));
                        token = new Token(line, column, TokenType.INTC, sb.substring(0, sb.length() - 1));
                        LOG.debug("已识别Token:" + token);
                        return token;
                    }
                    //endregion
                    break;
                case InAssign://region 识别赋值符号:=
                    if (ch == '=') {
                        token = new Token(line, column, TokenType.ASSIGN, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    } else {
                        state = State.Error;
                    }
                    //endregion
                    break;
                case InComment://region处理注释
                    sb.deleteCharAt(sb.length() - 1);//删除读入的第一个注释中的字符
                    while (ch != -1 && ch != '}') {
                        ch = getChar();
                    }//一直到注释结束，注释结束后继续识别
                    state = State.Normal;
                    if (ch != '}') {
                        LOG.trace("期待注释结束符【}】却没有}");
                        state = State.Error;
                    }
                    //endregion
                    break;
                case InRange://region 下标界限 ..
                    if (ch == '.') {
                        token = new Token(line, column, TokenType.UNDERRANGE, sb.toString());
                        LOG.debug("已识别Token:" + token);
                        return token;
                    }
                    //endregion
                    break;
                case InChar://region 识别引号括起来的字符
                    if (isAlpha(ch) || isDigit(ch)) {
                        ch = getChar();
                        if (ch == '\'') {
                            token = new Token(line, column, TokenType.CHARACTER, sb.toString());
                            LOG.debug("已识别Token:" + token);
                            return token;
                        }
                    }
                    state = State.Error;
                    //endregion
                    break;
                case Error://region 错误处理 返回空的Token 记录错误信息
                    LOG.warn("[错误]在 " + line + "行 " + column + "列");
                    errors.add("[错误]在 " + line + "行 " + column + "列");
                    token = new Token();
                    return token;
                //endregion
                default:
                    state = State.Error;
            }
            ch = getChar();
        }
        //region 文件结束处理
        if (state == State.InRange) {//文件已结束，且前一个符号是【.】说明程序结束
            Token token = new Token(line, column, TokenType.EOF, ".");
            LOG.debug("已识别Token:" + token);
            return token;
        }
        if (state != State.Normal) {
            errors.add("[错误]在 " + line + "行 " + column + "列");
        }//endregion
        return null;
    }

    private int getChar() throws IOException {
        int ch;
        if (getMeFirst != -1 && getMeFirst != ' ' && getMeFirst != '\r' && getMeFirst != '\n') {
            ch = getMeFirst;
            getMeFirst = -1;
        } else ch = reader.read();

        if (ch == '\n') {
            column = 0;
            line++;
        } else if (ch != -1) column++;

        if (ch == '\r') column--;
        LOG.trace("当前字符是{}({})[{}:{}]", showChar(ch), ch, line, column);
        return ch;
    }

    private void unGetChar(int ch) {
        getMeFirst = ch;
    }

    private boolean isAlpha(int ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    private boolean isDigit(int ch) {
        return (ch >= '0' && ch <= '9');
    }

    private boolean isBlank(int ch) {
        return ((char) ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r');
    }

    private String showChar(int ch) {
        if (ch == '\n')
            return "\\n";
        else if (ch == '\r')
            return "\\r";
        else return "" + (char) ch;
    }

    public static String getSourceFileAsString(InputStream in) {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(in);
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        InputStream in = Lexer.class.getClassLoader().getResourceAsStream("p.snl");
        Lexer lexer = new Lexer();
        try {
            LexerResult result = lexer.getResult(new InputStreamReader(in));
            if (result.getErrors().size() == 0) {
                List<Token> list = result.getTokenList();
                System.out.println();
                if (list.size() > 0) {
                    System.out.printf("[ 行:列 ]|【 语义信息 】| 词法信息 \n");
                    System.out.printf("---------+--------------+----------\n");
                }
                for (Token t : list) {
                    System.out.printf("[%3d:%-3d]|【%10s】|%10s\n", t.line, t.column, t.value, t.type.getStr());
                }
                if (list.size() > 0) {
                    System.out.printf("---------+--------------+----------\n");
                    System.out.printf("[ 行:列 ]|【 语义信息 】| 词法信息 \n");
                }
            } else {
                System.err.println("词法分析错误");
                result.getErrors().forEach(System.err::println);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
