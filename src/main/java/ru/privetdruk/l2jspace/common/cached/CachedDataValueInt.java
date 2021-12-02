package ru.privetdruk.l2jspace.common.cached;

public class CachedDataValueInt extends CachedDataValue {
    private volatile int value;

    CachedDataValueInt(CachedDataKey key, int defaultValue, int charId) {
        super(key, Integer.toString(defaultValue), charId);
        value = defaultValue;
    }

    @Override
    void load() {
        super.load();
        value = Integer.parseInt(getValue());
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;
        setValue(Integer.toString(value));
    }
}
