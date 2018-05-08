/*
 * https://github.com/YouthLin/SNL-Compiler
 * SNL Small Nested Language
 * 《编译程序的设计与实现》刘磊 金英 张晶 张荷花 单郸；高等教育出版社，ISBN 978-7-04-014620-7.
 * 所有产生式出自该教材
 * 只为学习目的转为 Antlr 语法文件
 * 请只用于学习目的
 * 语法转写: Youth．霖 https://youthlin.com
 *
 * FOR STUDY ONLY
 * POWERED BY JLU
 *
 */
grammar SNL;
//总程序
program
        : programHead declarePart programBody '.';
//程序头
programHead
        :  PROGRAM programName;
programName
        : ID;
//程序声明
declarePart
        : typeDecpart varDecpart procDecpart;
//类型声明
typeDecpart
        : typeDec?;
typeDec
        : TYPE typeDecList;
typeDecList
        : typeId '=' typeDef ';' typeDecMore;
typeDecMore
        : typeDecList?;
typeId  : ID;

//类型
typeDef
        : baseType
        | structureType
        | ID
        ;
baseType
        : INTEGER
        | CHAR
        ;
structureType
        : arrayType
        | recType
        ;
arrayType
        : ARRAY '[' low '..' top ']' OF baseType;
low     : INTC;
top     : INTC;
recType : RECORD fieldDecList END;
fieldDecList
        : baseType idList ';' fieldDecMore
        | arrayType idList ';' fieldDecMore;
fieldDecMore
        : fieldDecList?;
idList  : ID idMore;
idMore  : (',' idList)?;

//变量声明
varDecpart
        : varDec?;
varDec  : VAR varDecList;
varDecList
        : typeDef varIdList ';' varDecMore;
varDecMore
        : varDecList?;
varIdList
        :ID varIdMore;
varIdMore
        : (',' varIdList)?;

//过程声明
procDecpart
        : procDec?;
procDec : PROCEDURE procName '(' paramList ')' ';' procDecPart procBody procDecPart;
procName
        : ID;

//参数声明
paramList
        : paramDecList?;
paramDecList
        : param paramMore;
paramMore
        : (';' paramDecList)?;
param
        : typeDef formList
        | VAR typeDef formList
        ;
formList
        : ID fidMore;
fidMore : (',' formList)?;

//过程中的声明部分
procDecPart
        : declarePart;

//过程体
procBody
        : programBody;

//主程序体
programBody
        : BEGIN stmList END;

//语句序列
stmList : stm stmMore;
stmMore : (';' stmList)?;

//语句
stm     : conditionalStm
        | loopStm
        | inputStm
        | outputStm
        | returnStm
        | ID assCall;
assCall : assignmentRest
        | callStmRest;
//赋值语句
assignmentRest
        : variMore ':=' exp;

//条件语句
conditionalStm
        : IF relExp THEN stmList ELSE stmList FI;
//循环语句
loopStm : WHILE relExp DO stmList ENDWH;
//输入语句
inputStm
        : READ '(' invar ')';
invar   : ID;
//输出语句
outputStm
        : WRITE '(' exp ')';
//返回语句
returnStm
        : RETURN;
//过程调用语句
callStmRest
        : '(' actParamList ')';
actParamList
        : (exp actParamMore)?;
actParamMore
        : (',' actParamList)?;
//条件表达式
relExp  : exp otherRelE;
otherRelE
        : cmpOp exp;
//算数表达式
exp     : term otherTerm;
otherTerm
        : (addOp exp)?;
//项
term    : factor otherFactor;
otherFactor
        : (multOp term)?;
//因子
factor  : '(' exp ')'
        | INTC
        | variable
        ;
variable
        : ID variMore;
variMore
        : ('[' exp ']')?
        | ('.' fieldVar)?
        ;
fieldVar
        : ID fieldVarMore;
fieldVarMore
        : ('[' exp ']')?;
cmpOp   : '<'
        | '='
        ;
addOp   : '+'
        | '-'
        ;
multOp  : '*'
        | '/'
        ;

PROGRAM : 'program';
TYPE    : 'type';
INTEGER : 'integer';
CHAR    : 'char';
ARRAY   : 'array';
OF      : 'of';
RECORD  : 'record';
END     : 'end';
VAR     : 'var';
PROCEDURE
        : 'procedure';
BEGIN   : 'begin';
IF      : 'if';
THEN    : 'then';
ELSE    : 'else';
FI      : 'fi';
WHILE   : 'while';
DO      : 'do';
ENDWH   : 'endwh';
READ    : 'read';
WRITE   : 'write';
RETURN  : 'return';
ID      : [a-zA-Z_][a-zA-Z0-9_]*;
INTC    : [0-9]+;
WS      : [ \t\r\n] -> skip;
SNLCOMMENT
        : '{' .*? '}' -> skip;
LINECOMMENT
        : '//' .*? '\r'?'\n' -> skip;
BLOCKCOMMENT
        : '/*'.*? '*/' -> skip;
