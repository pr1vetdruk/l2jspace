package ru.privetdruk.l2jspace.gameserver.handler.skillhandlers;

import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.data.xml.SummonItemData;
import ru.privetdruk.l2jspace.gameserver.enums.items.ItemLocation;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.geoengine.GeoEngine;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.WorldObject;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.instance.Pet;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.model.location.SpawnLocation;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class SummonCreature implements ISkillHandler {
    private static final SkillType[] SKILL_IDS =
            {
                    SkillType.SUMMON_CREATURE
            };

    @Override
    public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets) {
        if (!(activeChar instanceof Player))
            return;

        final Player player = (Player) activeChar;
        final ItemInstance item = player.getInventory().getItemByObjectId(player.getAI().getCurrentIntention().getItemObjectId());

        // Skill cast may have been interrupted of cancelled
        if (item == null)
            return;

        // Check for summon item validity.
        if (item.getOwnerId() != player.getObjectId() || item.getLocation() != ItemLocation.INVENTORY)
            return;

        // Owner has a pet listed in world.
        if (World.getInstance().getPet(player.getObjectId()) != null)
            return;

        // Check summon item validity.
        final IntIntHolder summonItem = SummonItemData.getInstance().getSummonItem(item.getItemId());
        if (summonItem == null)
            return;

        // Check NpcTemplate validity.
        final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(summonItem.getId());
        if (npcTemplate == null)
            return;

        // Add the pet instance to world.
        final Pet pet = Pet.restore(item, npcTemplate, player);
        if (pet == null)
            return;

        World.getInstance().addPet(player.getObjectId(), pet);

        player.setSummon(pet);

        pet.forceRunStance();
        pet.setTitle(player.getName());
        pet.startFeed();

        final SpawnLocation spawnLoc = activeChar.getPosition().clone();
        spawnLoc.addStrictOffset(40);
        spawnLoc.setHeadingTo(activeChar.getPosition());
        spawnLoc.set(GeoEngine.getInstance().getValidLocation(activeChar, spawnLoc));

        pet.spawnMe(spawnLoc);
        pet.getAI().setFollowStatus(true);
    }

    @Override
    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}