package ru.privetdruk.l2jspace.common.cached;

import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.ArrayList;
import java.util.List;

public class CachedData {
    private final int charId;

    private final List<CachedDataValue> cachedDataValueList = new ArrayList<>();

    public CachedData(int charId) {
        this.charId = charId;
    }

    public void load() {
        for (var value : cachedDataValueList) {
            value.load();
        }
    }

    public void store() {
        for (var value : cachedDataValueList) {
            value.update();
        }
    }

    public void showHTML(Player player) {
        var packet = new NpcHtmlMessage(0);
        var str = new StringBuilder();
        str.append("<html><title>Cached Value List</title><body><table width=260><tr><td width=100>key</td><td width=160>value</td></tr>");
        for (var value : cachedDataValueList)
            str.append("<tr><td>").append(value.getKey()).append("</td><td>").append(value.getValue()).append("</td></tr>");
        str.append("</table></body></html>");
        packet.setHtml(str.toString());
        player.sendPacket(packet);
    }

    public static final boolean BOOLEAN_DEFAULT = true;
    public static final byte BYTE_DEFAULT = (byte) 0;
    public static final short SHORT_DEFAULT = (short) 0;
    public static final int INT_DEFAULT = 0;
    public static final long LONG_DEFAULT = 0;
    public static final float FLOAT_DEFAULT = 0.0f;
    public static final double DOUBLE_DEFAULT = 0.0d;
    public static final String STRING_DEFAULT = "";

    public CachedDataValueBoolean newBoolean(CachedDataKey key) {
        return newBoolean(key, BOOLEAN_DEFAULT);
    }

    public CachedDataValueBoolean newBoolean(CachedDataKey key, boolean defaultValue) {
        var value = new CachedDataValueBoolean(key, defaultValue, charId);
        cachedDataValueList.add(value);
        return value;
    }

    public CachedDataValueByte newByte(CachedDataKey key) {
        return newByte(key, BYTE_DEFAULT);
    }

    public CachedDataValueByte newByte(CachedDataKey key, byte defaultValue) {
        var value = new CachedDataValueByte(key, defaultValue, charId);
        cachedDataValueList.add(value);
        return value;
    }

    public CachedDataValueShort newShort(CachedDataKey key) {
        return newShort(key, SHORT_DEFAULT);
    }

    public CachedDataValueShort newShort(CachedDataKey key, short defaultValue) {
        var value = new CachedDataValueShort(key, defaultValue, charId);
        cachedDataValueList.add(value);
        return value;
    }

    public CachedDataValueInt newInt(CachedDataKey key) {
        return newInt(key, INT_DEFAULT);
    }

    public CachedDataValueInt newInt(CachedDataKey key, int defaultValue) {
        var value = new CachedDataValueInt(key, defaultValue, charId);
        cachedDataValueList.add(value);
        return value;
    }

    public CachedDataValueLong newLong(CachedDataKey key) {
        return newLong(key, LONG_DEFAULT);
    }

    public CachedDataValueLong newLong(CachedDataKey key, long defaultValue) {
        var value = new CachedDataValueLong(key, defaultValue, charId);
        cachedDataValueList.add(value);
        return value;
    }

    public CachedDataValueFloat newFloat(CachedDataKey key) {
        return newFloat(key, FLOAT_DEFAULT);
    }

    public CachedDataValueFloat newFloat(CachedDataKey key, float defaultValue) {
        var value = new CachedDataValueFloat(key, defaultValue, charId);
        cachedDataValueList.add(value);
        return value;
    }

    public CachedDataValueDouble newDouble(CachedDataKey key) {
        return newDouble(key, DOUBLE_DEFAULT);
    }

    public CachedDataValueDouble newDouble(CachedDataKey key, double defaultValue) {
        var value = new CachedDataValueDouble(key, defaultValue, charId);
        cachedDataValueList.add(value);
        return value;
    }

    public CachedDataValueString newString(CachedDataKey key) {
        return newString(key, STRING_DEFAULT);
    }

    public CachedDataValueString newString(CachedDataKey key, String defaultValue) {
        var value = new CachedDataValueString(key, defaultValue, charId);
        cachedDataValueList.add(value);
        return value;
    }
}