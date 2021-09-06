package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.gameserver.enums.ShortcutType;
import ru.privetdruk.l2jspace.gameserver.model.Shortcut;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.ShortCutRegister;

public final class RequestShortCutReg extends L2GameClientPacket {
    private int _type;
    private int _id;
    private int _slot;
    private int _page;
    private int _characterType;

    @Override
    protected void readImpl() {
        _type = readD();
        int slot = readD();
        _id = readD();
        _characterType = readD();

        _slot = slot % 12;
        _page = slot / 12;
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getPlayer();
        if (player == null)
            return;

        if (_page < 0 || _page > 10)
            return;

        if (_type < 1 || _type > ShortcutType.VALUES.length)
            return;

        final ShortcutType type = ShortcutType.VALUES[_type];

        switch (type) {
            case ITEM:
            case ACTION:
            case MACRO:
            case RECIPE:
                Shortcut shortcut = new Shortcut(_slot, _page, type, _id, -1, _characterType);
                sendPacket(new ShortCutRegister(player, shortcut));
                player.getShortcutList().addShortcut(shortcut);
                break;

            case SKILL:
                final int level = player.getSkillLevel(_id);
                if (level > 0) {
                    shortcut = new Shortcut(_slot, _page, type, _id, level, _characterType);
                    sendPacket(new ShortCutRegister(player, shortcut));
                    player.getShortcutList().addShortcut(shortcut);
                }
                break;
        }
    }
}