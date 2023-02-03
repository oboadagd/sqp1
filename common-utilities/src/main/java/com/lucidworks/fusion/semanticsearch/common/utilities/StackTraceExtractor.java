package com.lucidworks.fusion.semanticsearch.common.utilities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.PrintWriter;
import java.io.StringWriter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StackTraceExtractor {
    public static String extract(final Throwable e) {
        if (e == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
