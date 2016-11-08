package com.workflow.engine.expression.format.reader;

import com.workflow.engine.expression.format.Element;
import com.workflow.engine.expression.format.ExpressionReader;
import com.workflow.engine.expression.format.FormatException;

import java.io.IOException;

/**
 * 类型强转词元
 *
 * @author 侯金鑫
 * @version 2.1
 *          Sep 5, 2016
 */
public class TypeCastTypeReader implements ElementReader {

    public static final char START_MARK = '@';//类型转换开始
    public static final char END_MARK = '$';//类型转化结束

    /**
     * 从流中读取函数类型的ExpressionToken
     *
     * @param sr
     * @return
     * @throws FormatException
     * @throws IOException
     */
    public Element read(ExpressionReader sr) throws FormatException, IOException {
        int index = sr.getCurrentIndex();
        StringBuffer sb = new StringBuffer();
        int b = sr.read();
        if (b == -1 || b != TypeCastTypeReader.START_MARK) {
            throw new FormatException("强制类型转换符非法");
        }
        boolean readStart = true;
        while ((b = sr.read()) != -1) {
            char c = (char) b;
            if (c == TypeCastTypeReader.END_MARK) {
                if (sb.length() == 0) {
                    throw new FormatException("类型名称不能为空");
                }
                sr.reset();
                return new Element(sb.toString(), index, Element.ElementType.CLASS);
            }
            if (readStart) {
                if (!Character.isJavaIdentifierStart(c)) {
                    throw new FormatException("类型名称开头不能为字符：" + c);
                }
                readStart = false;
            }
            if (!Character.isJavaIdentifierPart(c)) {
                throw new FormatException("类型名称不能为非法字符：" + c);
            }
            sb.append(c);
            sr.mark(0);
        }
        throw new FormatException("不是有效的类型转换结束");
    }
}
