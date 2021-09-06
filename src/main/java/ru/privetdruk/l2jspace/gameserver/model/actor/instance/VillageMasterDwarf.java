package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import ru.privetdruk.l2jspace.gameserver.enums.actors.ClassId;
import ru.privetdruk.l2jspace.gameserver.enums.actors.ClassRace;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;

public final class VillageMasterDwarf extends VillageMaster {
    public VillageMasterDwarf(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected final boolean checkVillageMasterRace(ClassId pclass) {
        if (pclass == null)
            return false;

        return pclass.getRace() == ClassRace.DWARF;
    }
}