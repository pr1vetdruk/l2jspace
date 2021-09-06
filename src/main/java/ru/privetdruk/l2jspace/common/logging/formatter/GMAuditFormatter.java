package ru.privetdruk.l2jspace.common.logging.formatter;

import ru.privetdruk.l2jspace.common.logging.MasterFormatter;

import java.util.logging.LogRecord;

public class GMAuditFormatter extends MasterFormatter {
    @Override
    public String format(LogRecord record) {
        return "[" + getFormatedDate(record.getMillis()) + "]" + SPACE + record.getMessage() + CRLF;
    }
}