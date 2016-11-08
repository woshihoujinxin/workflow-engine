package com.workflow.engine.expression.format.reader;

import com.workflow.engine.expression.format.ExpressionReader;
import com.workflow.engine.expression.format.FormatException;

import java.io.IOException;

/**
 * 词元读取器工厂
 *
 * @author 林良益，卓诗垚
 * @version 2.0
 *          Oct 9, 2008
 */
public class ElementReaderFactory {

    /**
     * 根据流开头构造不同的词元读取
     * 流应该非空格开头
     *
     * @param reader
     * @return ElementReader
     * @throws IOException
     * @throws FormatException
     */
    public static ElementReader createElementReader(ExpressionReader reader) throws IOException, FormatException {
        //读一个char
        reader.mark(0);
        int b = reader.read();
        reader.reset();
        if (b != -1) {
            char c = (char) b;
            try {
                if (c == StringTypeReader.START_MARK) {//"开头，构造字符串读取器
                    return StringTypeReader.class.newInstance();
                } else if (c == DateTypeReader.START_MARK) {//[开头，构造日期读取器
                    return DateTypeReader.class.newInstance();
                } else if (c == FunctionTypeReader.START_MARK) {//$开头，构造函数读取器
                    return FunctionTypeReader.class.newInstance();
                } else if (c == TypeCastTypeReader.START_MARK) {//@开头, 构造类型转换读取器
                    return TypeCastTypeReader.class.newInstance();
                } else if (SplitterTypeReader.SPLITTER_CHAR.indexOf(c) >= 0) {//如果是分隔符，构造分隔符读取器
                    return SplitterTypeReader.class.newInstance();
                } else if (NumberTypeReader.NUMBER_CHARS.indexOf(c) >= 0) {//以数字开头，构造数字类型读取器
                    return NumberTypeReader.class.newInstance();
                } else if (OperatorTypeReader.isOperatorStart(reader)) {//如果前缀是运算符，构造运算符读取器
                    return OperatorTypeReader.class.newInstance();
                } else {
                    return VariableTypeReader.class.newInstance();//否则构造一个变量读取器
                }
            } catch (Exception e) {
                throw new FormatException(e);
            }

        } else {
            throw new FormatException("流已结束");
        }
    }
}
