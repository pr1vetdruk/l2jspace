package ru.privetdruk.l2jspace.gameserver.network.serverpackets;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.custom.model.event.EventType;
import ru.privetdruk.l2jspace.gameserver.data.manager.CursedWeaponManager;
import ru.privetdruk.l2jspace.gameserver.enums.TeamAura;
import ru.privetdruk.l2jspace.gameserver.enums.Paperdoll;
import ru.privetdruk.l2jspace.gameserver.enums.skills.AbnormalEffect;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.Summon;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Cubic;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Costume;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.ArmorSet;
import ru.privetdruk.l2jspace.gameserver.model.item.kind.Item;

public class UserInfo extends L2GameServerPacket {
    private final Player player;
    private int _relation;

    public UserInfo(Player player) {
        this.player = player;

        if (player.isWinnerInEvent(EventType.LAST_EMPEROR)) {
            _relation = 192;
        } else {
            _relation = this.player.isClanLeader() ? 0x40 : 0;

            if (this.player.getSiegeState() == 1) {
                _relation |= 0x180;
            } else if (this.player.getSiegeState() == 2) {
                _relation |= 0x80;
            }
        }
    }

    @Override
    protected final void writeImpl() {
        writeC(0x04);
        writeD(player.getX());
        writeD(player.getY());
        writeD(player.getZ());
        writeD(player.getHeading());
        writeD(player.getId());
        writeS((player.getPolymorphTemplate() != null) ? player.getPolymorphTemplate().getName() : player.getName());
        writeD(player.getRace().ordinal());
        writeD(player.getAppearance().getSex().ordinal());
        writeD((player.getClassIndex() == 0) ? player.getClassId().getId() : player.getBaseClass());
        writeD(player.getStatus().getLevel());
        writeQ(player.getStatus().getExp());
        writeD(player.getStatus().getSTR());
        writeD(player.getStatus().getDEX());
        writeD(player.getStatus().getCON());
        writeD(player.getStatus().getINT());
        writeD(player.getStatus().getWIT());
        writeD(player.getStatus().getMEN());
        writeD(player.getStatus().getMaxHp());
        writeD((int) player.getStatus().getHp());
        writeD(player.getStatus().getMaxMp());
        writeD((int) player.getStatus().getMp());
        writeD(player.getStatus().getSp());
        writeD(player.getCurrentWeight());
        writeD(player.getWeightLimit());
        writeD(player.getActiveWeaponItem() != null ? 40 : 20);

        ArmorSet armorSet = null;

        ItemInstance costumeItem = player.getInventory().getItemFrom(Paperdoll.UNDER);
        if (costumeItem != null
                && costumeItem.getItem().getSlot() == Item.Slot.COSTUME
                && costumeItem.getItem().getCostume() != Costume.NONE) {
            armorSet = costumeItem.getItem().getCostume().getSet();
        }

        boolean isCostume = armorSet != null;

        writeD(player.getInventory().getItemObjectIdFrom(Paperdoll.HAIRALL));
        writeD(player.getInventory().getItemObjectIdFrom(Paperdoll.REAR));
        writeD(player.getInventory().getItemObjectIdFrom(Paperdoll.LEAR));
        writeD(player.getInventory().getItemObjectIdFrom(Paperdoll.NECK));
        writeD(player.getInventory().getItemObjectIdFrom(Paperdoll.RFINGER));
        writeD(player.getInventory().getItemObjectIdFrom(Paperdoll.LFINGER));
        writeD(player.getInventory().getItemObjectIdFrom(Paperdoll.HEAD));
        writeD(player.getInventory().getItemObjectIdFrom(Paperdoll.RHAND));
        writeD(player.getInventory().getItemObjectIdFrom(Paperdoll.LHAND));
        writeD(isCostume ? armorSet.getGlovesId() : player.getInventory().getItemObjectIdFrom(Paperdoll.GLOVES));
        writeD(isCostume ? armorSet.getChestId() : player.getInventory().getItemObjectIdFrom(Paperdoll.CHEST));
        writeD(isCostume ? armorSet.getLegsId() : player.getInventory().getItemObjectIdFrom(Paperdoll.LEGS));
        writeD(isCostume ? armorSet.getBootsId() : player.getInventory().getItemObjectIdFrom(Paperdoll.FEET));
        writeD(player.getInventory().getItemObjectIdFrom(Paperdoll.CLOAK));
        writeD(player.getInventory().getItemObjectIdFrom(Paperdoll.RHAND));
        writeD(player.getInventory().getItemObjectIdFrom(Paperdoll.HAIR));
        writeD(player.getInventory().getItemObjectIdFrom(Paperdoll.FACE));

        writeD(player.getInventory().getItemIdFrom(Paperdoll.HAIRALL));
        writeD(player.getInventory().getItemIdFrom(Paperdoll.REAR));
        writeD(player.getInventory().getItemIdFrom(Paperdoll.LEAR));
        writeD(player.getInventory().getItemIdFrom(Paperdoll.NECK));
        writeD(player.getInventory().getItemIdFrom(Paperdoll.RFINGER));
        writeD(player.getInventory().getItemIdFrom(Paperdoll.LFINGER));
        writeD(player.getInventory().getItemIdFrom(Paperdoll.HEAD));
        writeD(player.getInventory().getItemIdFrom(Paperdoll.RHAND));
        writeD(player.getInventory().getItemIdFrom(Paperdoll.LHAND));
        writeD(isCostume ? armorSet.getGlovesId() : player.getInventory().getItemIdFrom(Paperdoll.GLOVES));
        writeD(isCostume ? armorSet.getChestId() : player.getInventory().getItemIdFrom(Paperdoll.CHEST));
        writeD(isCostume ? armorSet.getBootsId() : player.getInventory().getItemIdFrom(Paperdoll.LEGS));
        writeD(isCostume ? armorSet.getLegsId() : player.getInventory().getItemIdFrom(Paperdoll.FEET));
        writeD(player.getInventory().getItemIdFrom(Paperdoll.CLOAK));
        writeD(player.getInventory().getItemIdFrom(Paperdoll.RHAND));
        writeD(player.getInventory().getItemIdFrom(Paperdoll.HAIR));
        writeD(player.getInventory().getItemIdFrom(Paperdoll.FACE));

        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeD(player.getInventory().getAugmentationIdFrom(Paperdoll.RHAND));
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeD(player.getInventory().getAugmentationIdFrom(Paperdoll.LHAND));
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);
        writeH(0x00);

        writeD(player.getStatus().getPAtk(null));
        writeD(player.getStatus().getPAtkSpd());
        writeD(player.getStatus().getPDef(null));
        writeD(player.getStatus().getEvasionRate(null));
        writeD(player.getStatus().getAccuracy());
        writeD(player.getStatus().getCriticalHit(null, null));
        writeD(player.getStatus().getMAtk(null, null));
        writeD(player.getStatus().getMAtkSpd());
        writeD(player.getStatus().getPAtkSpd());
        writeD(player.getStatus().getMDef(null, null));
        writeD(player.getPvpFlag());
        writeD(player.getKarma());

        final int runSpd = player.getStatus().getBaseRunSpeed();
        final int walkSpd = player.getStatus().getBaseWalkSpeed();
        final int swimSpd = player.getStatus().getBaseSwimSpeed();

        writeD(runSpd);
        writeD(walkSpd);
        writeD(swimSpd);
        writeD(swimSpd);
        writeD(0);
        writeD(0);
        writeD((player.isFlying()) ? runSpd : 0);
        writeD((player.isFlying()) ? walkSpd : 0);

        writeF(player.getStatus().getMovementSpeedMultiplier());
        writeF(player.getStatus().getAttackSpeedMultiplier());

        final Summon summon = player.getSummon();
        if (player.isMounted() && summon != null) {
            writeF(summon.getCollisionRadius());
            writeF(summon.getCollisionHeight());
        } else {
            writeF(player.getCollisionRadius());
            writeF(player.getCollisionHeight());
        }

        writeD(player.getAppearance().getHairStyle());
        writeD(player.getAppearance().getHairColor());
        writeD(player.getAppearance().getFace());
        writeD((player.isGM()) ? 1 : 0);

        writeS((player.getPolymorphTemplate() != null) ? "Morphed" : player.getTitle());

        writeD(player.getClanId());
        writeD(player.getClanCrestId());
        writeD(player.getAllyId());
        writeD(player.getAllyCrestId());
        writeD(_relation);
        writeC(player.getMountType());
        writeC(player.getOperateType().getId());
        writeC((player.hasDwarvenCraft()) ? 1 : 0);
        writeD(player.getPkKills());
        writeD(player.getPvpKills());

        writeH(player.getCubicList().size());
        for (final Cubic cubic : player.getCubicList())
            writeH(cubic.getId());

        writeC((player.isInPartyMatchRoom()) ? 1 : 0);
        writeD((!player.getAppearance().isVisible() && player.isGM()) ? (player.getAbnormalEffect() | AbnormalEffect.STEALTH.getMask()) : player.getAbnormalEffect());
        writeC(0x00);
        writeD(player.getClanPrivileges());
        writeH(player.getRecomLeft());
        writeH(player.getRecomHave());
        writeD((player.getMountNpcId() > 0) ? player.getMountNpcId() + 1000000 : 0);
        writeH(player.getStatus().getInventoryLimit());
        writeD(player.getClassId().getId());
        writeD(0x00);
        writeD(player.getStatus().getMaxCp());
        writeD((int) player.getStatus().getCp());
        writeC((player.isMounted()) ? 0 : player.getEnchantEffect());
        writeC((Config.PLAYER_SPAWN_PROTECTION > 0 && player.isSpawnProtected()) ? TeamAura.BLUE.getId() : player.getTeamAura().getId());
        writeD(player.getClanCrestLargeId());
        writeC((player.isNoble()) ? 1 : 0);
        writeC(player.isHero()
                || (player.isGM() && Config.GM_HERO_AURA)
                || player.isTopRank() ? 1 : 0);
        writeC((player.isFishing()) ? 1 : 0);
        writeLoc(player.getFishingStance().getLoc());
        writeD(player.getAppearance().getNameColor());
        writeC((player.isRunning()) ? 0x01 : 0x00);
        writeD(player.getPledgeClass());
        writeD(player.getPledgeType());
        writeD(player.getAppearance().getTitleColor());
        writeD(CursedWeaponManager.getInstance().getCurrentStage(player.getCursedWeaponEquippedId()));
    }
}