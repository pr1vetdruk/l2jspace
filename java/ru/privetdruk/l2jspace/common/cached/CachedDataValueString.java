package ru.privetdruk.l2jspace.common.cached;

public class CachedDataValueString extends CachedDataValue {

    CachedDataValueString(String name, String defaultValue, int charId) {
        super(name, defaultValue, charId);
    }

    public String get() {
        return getValue();
    }

    public void set(String value) {
        setValue(value);
    }
}
