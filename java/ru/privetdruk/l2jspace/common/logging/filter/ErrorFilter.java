package ru.privetdruk.l2jspace.common.logging.filter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class ErrorFilter implements Filter {
    @Override
    public boolean isLoggable(LogRecord record) {
        return record.getThrown() != null;
    }
}