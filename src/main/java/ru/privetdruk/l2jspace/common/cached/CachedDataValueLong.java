package ru.privetdruk.l2jspace.common.cached;

public class CachedDataValueLong extends CachedDataValue {
    private volatile long value;

    CachedDataValueLong(CachedDataKey key, long defaultValue, int charId) {
        super(key, Long.toString(defaultValue), charId);
        value = defaultValue;
    }

    @Override
    void load() {
        super.load();
        value = Long.parseLong(getValue());
    }

    public long get() {
        return value;
    }

    public void set(long value) {
        this.value = value;
        setValue(Long.toString(value));
    }
}
