package ru.privetdruk.l2jspace.common.cached;

public class CachedDataValueString extends CachedDataValue {

    CachedDataValueString(CachedDataKey key, String defaultValue, int charId) {
        super(key, defaultValue, charId);
    }

    public String get() {
        return getValue();
    }

    public void set(String value) {
        setValue(value);
    }
}
