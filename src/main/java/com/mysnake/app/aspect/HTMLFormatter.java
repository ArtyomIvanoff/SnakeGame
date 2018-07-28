package com.mysnake.app.aspect;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class HTMLFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return (" <tr><td>" + (new Date(record.getMillis())).toString() + "</td><td>"
                + record.getMessage() + "</td></tr>\n");
    }

    @Override
    public String getHead(Handler h) {
       return ("<!DOCTYPE html>\n <html>\n  <body>\n" + "<Table border>\n<tr><td>Time</td><td>Log Message</td></tr>\n");
    }

    @Override
    public String getTail(Handler h) {
        return ("</table>\n</body>\n</html>");
    }
}
