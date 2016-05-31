package com.youthlin.snl.compiler;

import com.youthlin.snl.compiler.frontend.parser.LL1.LL1Parser;
import com.youthlin.snl.compiler.frontend.parser.SyntaxParser;
import com.youthlin.snl.compiler.frontend.parser.SyntaxTree;
import com.youthlin.snl.compiler.frontend.parser.ParseResult;
import com.youthlin.snl.compiler.frontend.parser.recursivedescent.RCParser;
import org.apache.commons.cli.*;

import java.io.*;

/**
 * Created by lin on 2016-05-31-031.
 * 主程序
 */
public class SNLc {

    public static void main(String[] args) {
//        args = new String[]{"D:/a.snl", "--RC"};
        Options options = new Options();
        options.addOption("r", "RC", false, "(default)use recursive descent to parse.");
        options.addOption("l", "LL1", false, "use LL1 to parse.");
        options.addOption("h", "help", false, "show this help text and exit.");
        options.addOption("v", "version", false, "show version text and exit.");
        try {
            CommandLine cli = new DefaultParser().parse(options, args);
            if (cli.hasOption("h") || cli.hasOption("v")) {
                if (cli.hasOption("h")) help(options);
                if (cli.hasOption("v")) version();
                return;
            }
            String[] arg = cli.getArgs();
            if (arg.length != 1) {
                help(options);
                return;
            }
            InputStream in = new FileInputStream(arg[0]);
            SyntaxParser parser;
            if (cli.hasOption("l")) {
                parser = new LL1Parser();
            } else parser = new RCParser();
            ParseResult result = parser.parse(in);
            if (result == null) {
                System.err.println("获取分析结果错误");
                System.exit(1);
            }
            if (result.isSuccess()) {
                SyntaxTree.print(result.getTree().getRoot(), new PrintStream(arg[0] + ".tree.txt"),
                        "Syntax Tree for source code: " + arg[0], 0);
            } else {
                System.err.println("RCParser Error.错误列表：");
                result.getErrors().forEach(System.err::println);
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            help(options);
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println("File Not Found.找不到源文件。");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void help(Options options) {
        new HelpFormatter().printHelp("SNLc [-r] [-l] [-h] [-v] <SourceFile>", options);
    }

    private static void version() {
        System.out.println("SNLc   : SNL(Small Nested Language) Compiler.");
        System.out.println("Author : Youth．霖");
        System.out.println("Version: 1.0.");
    }
}
