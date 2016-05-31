package com.youthlin.snl.compiler.frontend.parser;

import java.io.InputStream;

/**
 * Created by lin on 2016-05-28-028.
 * 语法分析器
 */
public interface SyntaxParser {
    ParseResult parse(InputStream in);
}
