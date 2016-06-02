package com.youthlin.snl.compiler;

import com.youthlin.snl.compiler.frontend.parser.LL1.LL1Parser;
import com.youthlin.snl.compiler.frontend.parser.SyntaxParser;
import com.youthlin.snl.compiler.frontend.parser.SyntaxTree;
import com.youthlin.snl.compiler.frontend.parser.ParseResult;
import com.youthlin.snl.compiler.frontend.parser.recursivedescent.RDParser;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by lin on 2016-05-31-031.
 * 主程序
 */
public class SNLc {
    private static final Logger LOG = LoggerFactory.getLogger(SNLc.class);

    public static void main(String[] args) {
//        args = new String[]{"D:/sd.snl", "--RC" ,"-e","GBK"};
        Options options = new Options();
        options.addOption("r", "RD", false, "(default)use recursive descent to parse.");
        options.addOption("l", "LL1", false, "use LL1 to parse.");
        options.addOption("h", "help", false, "show this help text and exit.");
        options.addOption("v", "version", false, "show version text and exit.");
        options.addOption("e", "encoding", true, "specify default encoding to open file.");
        String defaultEncoding = "UTF-8";
        String defaultParser = "Recursive Descent";
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

            if (cli.hasOption("e")) defaultEncoding = cli.getOptionValue("e");
            InputStream in = new FileInputStream(arg[0]);
            //http://akini.mbnet.fi/java/unicodereader/
            UnicodeReader unicodeReader = new UnicodeReader(in, defaultEncoding);

            SyntaxParser parser;
            if (cli.hasOption("l")) {
                parser = new LL1Parser();
                defaultParser = "LL1";
            } else parser = new RDParser();
            LOG.debug("参数: Parser=" + defaultParser + ", Encoding=" + defaultEncoding);

            ParseResult result = parser.parse(unicodeReader);
            if (result == null) {
                System.err.println("获取分析结果错误");
                System.exit(1);
            }
            if (result.isSuccess()) {
                SyntaxTree.print(result.getTree().getRoot(), new PrintStream(arg[0] + ".tree.txt"),
                        "Syntax Tree for source code: " + arg[0] + "(by " + defaultParser + ")", 0);
            } else {
                System.err.println("RDParser Error.错误列表：");
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
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * 判断文件的编码格式
     *
     * @param fileName :file
     * @return 文件编码格式
     * @throws Exception
     */
    public static String codeString(String fileName) throws Exception {
        BufferedInputStream bin = new BufferedInputStream(
                new FileInputStream(fileName));
        int p = (bin.read() << 8) + bin.read();
        String code = null;

        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }
        LOG.debug("File encoding = " + code);
        return code;
    }
}
