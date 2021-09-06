package ru.privetdruk.l2jspace.gameserver.skill.l2skill;

import ru.privetdruk.l2jspace.common.data.StatSet;
import ru.privetdruk.l2jspace.common.random.Rnd;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Playable;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Pet;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.PetItemList;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.SystemMessage;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class L2SkillCreateItem extends L2Skill {
    private final int[] _createItemId;
    private final int _createItemCount;
    private final int _randomCount;

    public L2SkillCreateItem(StatSet set) {
        super(set);
        _createItemId = set.getIntegerArray("create_item_id");
        _createItemCount = set.getInteger("create_item_count", 0);
        _randomCount = set.getInteger("random_count", 1);
    }

    /**
     * @see L2Skill#useSkill(Creature, WorldObject[])
     */
    @Override
    public void useSkill(Creature activeChar, WorldObject[] targets) {
        Player player = activeChar.getActingPlayer();
        if (activeChar.isAlikeDead())
            return;

        if (activeChar instanceof Playable) {
            if (_createItemId == null || _createItemCount == 0) {
                SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_PREPARED_FOR_REUSE);
                sm.addSkillName(this);
                activeChar.sendPacket(sm);
                return;
            }

            int count = _createItemCount + Rnd.get(_randomCount);
            int rndid = Rnd.get(_createItemId.length);

            if (activeChar instanceof Player)
                player.addItem("Skill", _createItemId[rndid], count, activeChar, true);
            else if (activeChar instanceof Pet) {
                activeChar.getInventory().addItem("Skill", _createItemId[rndid], count, player, activeChar);
                player.sendPacket(new PetItemList((Pet) activeChar));
            }
        }
    }
}