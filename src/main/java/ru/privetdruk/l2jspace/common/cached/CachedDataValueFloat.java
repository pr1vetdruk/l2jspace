package ru.privetdruk.l2jspace.common.cached;

public class CachedDataValueFloat extends CachedDataValue {
    private volatile float value;

    CachedDataValueFloat(CachedDataKey key, float defaultValue, int charId) {
        super(key, Float.toString(defaultValue), charId);
        value = defaultValue;
    }

    @Override
    void load() {
        super.load();
        value = Float.parseFloat(getValue());
    }

    public float get() {
        return value;
    }

    public void set(float value) {
        this.value = value;
        setValue(Float.toString(value));
    }
}
