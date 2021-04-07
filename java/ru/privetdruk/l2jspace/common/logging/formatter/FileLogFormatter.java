package ru.privetdruk.l2jspace.common.logging.formatter;

import java.util.logging.LogRecord;

import ru.privetdruk.l2jspace.common.logging.MasterFormatter;

public class FileLogFormatter extends MasterFormatter {
    @Override
    public String format(LogRecord record) {
        return "[" + getFormatedDate(record.getMillis()) + "]" + SPACE + record.getLevel().getName() + SPACE + record.getMessage() + CRLF;
    }
}