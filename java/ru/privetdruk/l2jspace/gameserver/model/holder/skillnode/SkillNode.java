package ru.privetdruk.l2jspace.gameserver.model.holder.skillnode;

import ru.privetdruk.l2jspace.common.data.StatSet;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;

/**
 * A generic datatype used to store skills informations for player templates.<br>
 * <br>
 * It extends {@link IntIntHolder} and isn't directly used.
 */
public class SkillNode extends IntIntHolder {
    private final int _minLvl;

    public SkillNode(StatSet set) {
        super(set.getInteger("id"), set.getInteger("lvl"));

        _minLvl = set.getInteger("minLvl");
    }

    public int getMinLvl() {
        return _minLvl;
    }
}