package ru.privetdruk.l2jspace.common.cached;

public class CachedDataValueBoolean extends CachedDataValue {
    private volatile boolean value;

    CachedDataValueBoolean(CachedDataKey key, boolean defaultValue, int charId) {
        super(key, Boolean.toString(defaultValue), charId);
        value = defaultValue;
    }

    @Override
    void load() {
        super.load();
        value = Boolean.parseBoolean(getValue());
    }

    public boolean get() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
        setValue(Boolean.toString(value));
    }
}
