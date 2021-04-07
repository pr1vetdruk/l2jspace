package ru.privetdruk.l2jspace.gameserver.model.holder;

import ru.privetdruk.l2jspace.gameserver.data.SkillTable;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

/**
 * A generic int/int container.
 */
public class IntIntHolder {
    private int _id;
    private int _value;

    public IntIntHolder(int id, int value) {
        _id = id;
        _value = value;
    }

    @Override
    public String toString() {
        return "IntIntHolder [id=" + _id + " value=" + _value + "]";
    }

    public int getId() {
        return _id;
    }

    public int getValue() {
        return _value;
    }

    public void setId(int id) {
        _id = id;
    }

    public void setValue(int value) {
        _value = value;
    }

    /**
     * @return The {@link L2Skill} associated to the id/value stored on this {@link IntIntHolder}.
     */
    public final L2Skill getSkill() {
        return SkillTable.getInstance().getInfo(_id, _value);
    }
}