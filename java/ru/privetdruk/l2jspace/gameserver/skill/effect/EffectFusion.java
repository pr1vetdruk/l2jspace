package ru.privetdruk.l2jspace.gameserver.skill.effect;

import ru.privetdruk.l2jspace.gameserver.data.SkillTable;
import ru.privetdruk.l2jspace.gameserver.enums.skills.EffectType;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.skill.AbstractEffect;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;

public class EffectFusion extends AbstractEffect {
    public int _effect;
    public int _maxEffect;

    public EffectFusion(EffectTemplate template, L2Skill skill, Creature effected, Creature effector) {
        super(template, skill, effected, effector);

        _effect = getSkill().getLevel();
        _maxEffect = SkillTable.getInstance().getMaxLevel(getSkill().getId());
    }

    @Override
    public boolean onActionTime() {
        return true;
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.FUSION;
    }

    public void increaseEffect() {
        if (_effect < _maxEffect) {
            _effect++;
            updateBuff();
        }
    }

    public void decreaseForce() {
        _effect--;
        if (_effect < 1)
            exit();
        else
            updateBuff();
    }

    private void updateBuff() {
        exit();
        SkillTable.getInstance().getInfo(getSkill().getId(), _effect).getEffects(getEffector(), getEffected());
    }
}