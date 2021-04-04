package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;

public final class HalishaChest extends Monster {
    public HalishaChest(int objectId, NpcTemplate template) {
        super(objectId, template);

        setNoRndWalk(true);
        setShowSummonAnimation(true);
        disableCoreAi(true);
    }

    @Override
    public boolean isMovementDisabled() {
        return true;
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }
}