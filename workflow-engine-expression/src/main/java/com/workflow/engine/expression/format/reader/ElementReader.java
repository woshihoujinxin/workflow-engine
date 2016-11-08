package com.workflow.engine.expression.format.reader;

import com.workflow.engine.expression.format.Element;
import com.workflow.engine.expression.format.ExpressionReader;
import com.workflow.engine.expression.format.FormatException;

import java.io.IOException;

/**
 * @author 林良益，卓诗垚
 * @version 2.0
 *          Oct 9, 2008
 */
public interface ElementReader {
    Element read(ExpressionReader sr) throws FormatException, IOException;
}
