package ru.privetdruk.l2jspace.common.cached;

public class CachedDataValueShort extends CachedDataValue {
    private volatile short value;

    CachedDataValueShort(CachedDataKey key, short defaultValue, int charId) {
        super(key, Short.toString(defaultValue), charId);
        value = defaultValue;
    }

    @Override
    void load() {
        super.load();
        value = Short.parseShort(getValue());
    }

    public short get() {
        return value;
    }

    public void set(short value) {
        this.value = value;
        setValue(Short.toString(value));
    }
}
