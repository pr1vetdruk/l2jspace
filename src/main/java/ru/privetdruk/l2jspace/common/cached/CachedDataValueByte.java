package ru.privetdruk.l2jspace.common.cached;

public class CachedDataValueByte extends CachedDataValue {
    private volatile byte value;

    CachedDataValueByte(CachedDataKey key, byte defaultValue, int charId) {
        super(key, Byte.toString(defaultValue), charId);
        value = defaultValue;
    }

    @Override
    void load() {
        super.load();
        value = Byte.parseByte(getValue());
    }

    public byte get() {
        return value;
    }

    public void set(byte value) {
        this.value = value;
        setValue(Byte.toString(value));
    }
}
