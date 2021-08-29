package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.gameserver.model.Shortcut;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;
import ru.privetdruk.l2jspace.gameserver.model.holder.Timestamp;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class ShortCutRegister extends L2GameServerPacket {
    private final Player player;
    private final Shortcut shortcut;

    public ShortCutRegister(Player player, Shortcut shortcut) {
        this.player = player;
        this.shortcut = shortcut;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x44);

        writeD(shortcut.getType().ordinal());
        writeD(shortcut.getSlot() + shortcut.getPage() * 12);
        switch (shortcut.getType()) {
            case ITEM -> {
                writeD(shortcut.getId());
                writeD(shortcut.getCharacterType());
                writeD(shortcut.getSharedReuseGroup());

                final ItemInstance item = player.getInventory().getItemByObjectId(shortcut.getId());
                if (item == null) {
                    writeD(0x00);
                    writeD(0x00);
                    writeD(0x00);
                } else if (!item.isEtcItem()) {
                    writeD(0x00);
                    writeD(0x00);
                    writeD((item.isAugmented()) ? item.getAugmentation().getId() : 0x00);
                } else {
                    final IntIntHolder[] skills = item.getEtcItem().getSkills();
                    if (skills == null) {
                        writeD(0x00);
                        writeD(0x00);
                    } else {
                        // Retrieve the first Skill only.
                        final L2Skill itemSkill = skills[0].getSkill();

                        final Timestamp timestamp = player.getReuseTimeStamp().get(itemSkill.getReuseHashCode());
                        if (timestamp == null) {
                            writeD(0x00);
                            writeD(0x00);
                        } else {
                            writeD((int) (timestamp.getRemaining() / 1000L));
                            writeD((int) (itemSkill.getReuseDelay() / 1000L));
                        }
                    }
                    writeD(0x00);
                }
            }
            case SKILL -> {
                writeD(shortcut.getId());
                writeD(shortcut.getLevel());
                writeC(0x00);
                writeD(shortcut.getCharacterType());
            }
            default -> {
                writeD(shortcut.getId());
                writeD(shortcut.getCharacterType());
            }
        }
    }
}