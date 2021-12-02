package ru.privetdruk.l2jspace.common.cached;

public class CachedDataValueDouble extends CachedDataValue {
    private volatile double value;

    CachedDataValueDouble(CachedDataKey key, double defaultValue, int charId) {
        super(key, Double.toString(defaultValue), charId);
        value = defaultValue;
    }

    @Override
    void load() {
        super.load();
        value = Double.parseDouble(getValue());
    }

    public double get() {
        return value;
    }

    public void set(double value) {
        this.value = value;
        setValue(Double.toString(value));
    }
}
