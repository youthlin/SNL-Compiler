package com.youthlin.snl.compiler.frontend.lexer;

/**
 * Created by lin on 2016-05-28-028.
 * Token种类
 */
public enum TokenType {
    EOF("."), ERROR("error"), EMPTY(""),

    /*保留字*/
    PROGRAM("program"), PROCEDURE("procedure"), TYPE("type"), VAR("var"),
    IF("if"), THEN("then"), ELSE("else"), FI("fi"),
    WHILE("while"), DO("do"), ENDWH("endwh"),
    BEGIN("begin"), END("end"), READ("read"), WRITE("write"),
    ARRAY("array"), OF("of"), RECORD("record"), RETURN("return"),
    /*类型*/
    INTEGER("integer"), CHAR("char"),

    ID("id"), INTC("intc"), CHARACTER("character"),

    /*特殊符号*/
    ASSIGN(":="), EQ("="), LT("<"),
    PLUS("+"), MINUS("-"), TIMES("*"), OVER("/"),
    LPAREN("("), RPAREN(")"), LMIDPAREN("["), RMIDPAREN("]"),
    UNDERRANGE(".."), SEMI(";"), COMMA(","), DOT(".");

    TokenType(String str) {
        this.str = str;
    }

    private String str;

    public String getStr() {
        return str;
    }
}
