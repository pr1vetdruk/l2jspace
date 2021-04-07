package ru.privetdruk.l2jspace.gameserver.model.actor.container.creature;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ru.privetdruk.l2jspace.gameserver.data.SkillTable;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillTargetType;
import ru.privetdruk.l2jspace.gameserver.enums.skills.SkillType;
import ru.privetdruk.l2jspace.gameserver.handler.ISkillHandler;
import ru.privetdruk.l2jspace.gameserver.handler.SkillHandler;
import ru.privetdruk.l2jspace.gameserver.model.actor.Creature;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillLaunched;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.MagicSkillUse;
import ru.privetdruk.l2jspace.gameserver.skill.ChanceCondition;
import ru.privetdruk.l2jspace.gameserver.skill.IChanceSkillTrigger;
import ru.privetdruk.l2jspace.gameserver.skill.L2Skill;
import ru.privetdruk.l2jspace.gameserver.skill.effect.EffectChanceSkillTrigger;

public class ChanceSkillList extends ConcurrentHashMap<IChanceSkillTrigger, ChanceCondition> {
    private static final long serialVersionUID = 1L;

    private final Creature _owner;

    public ChanceSkillList(Creature owner) {
        super();

        _owner = owner;
    }

    public Creature getOwner() {
        return _owner;
    }

    public void onHit(Creature target, boolean ownerWasHit, boolean wasCrit) {
        int event;
        if (ownerWasHit) {
            event = ChanceCondition.EVT_ATTACKED | ChanceCondition.EVT_ATTACKED_HIT;
            if (wasCrit)
                event |= ChanceCondition.EVT_ATTACKED_CRIT;
        } else {
            event = ChanceCondition.EVT_HIT;
            if (wasCrit)
                event |= ChanceCondition.EVT_CRIT;
        }

        onChanceSkillEvent(event, target);
    }

    public void onEvadedHit(Creature attacker) {
        onChanceSkillEvent(ChanceCondition.EVT_EVADED_HIT, attacker);
    }

    public void onSkillHit(Creature target, boolean ownerWasHit, boolean wasMagic, boolean wasOffensive) {
        int event;
        if (ownerWasHit) {
            event = ChanceCondition.EVT_HIT_BY_SKILL;
            if (wasOffensive) {
                event |= ChanceCondition.EVT_HIT_BY_OFFENSIVE_SKILL;
                event |= ChanceCondition.EVT_ATTACKED;
            } else {
                event |= ChanceCondition.EVT_HIT_BY_GOOD_MAGIC;
            }
        } else {
            event = ChanceCondition.EVT_CAST;
            event |= wasMagic ? ChanceCondition.EVT_MAGIC : ChanceCondition.EVT_PHYSICAL;
            event |= wasOffensive ? ChanceCondition.EVT_MAGIC_OFFENSIVE : ChanceCondition.EVT_MAGIC_GOOD;
        }

        onChanceSkillEvent(event, target);
    }

    public void onStart() {
        onChanceSkillEvent(ChanceCondition.EVT_ON_START, _owner);
    }

    public void onActionTime() {
        onChanceSkillEvent(ChanceCondition.EVT_ON_ACTION_TIME, _owner);
    }

    public void onExit() {
        onChanceSkillEvent(ChanceCondition.EVT_ON_EXIT, _owner);
    }

    public void onChanceSkillEvent(int event, Creature target) {
        if (_owner.isDead())
            return;

        for (Map.Entry<IChanceSkillTrigger, ChanceCondition> entry : entrySet()) {
            IChanceSkillTrigger trigger = entry.getKey();
            ChanceCondition cond = entry.getValue();

            if (cond != null && cond.trigger(event)) {
                if (trigger instanceof L2Skill)
                    makeCast((L2Skill) trigger, target);
                else if (trigger instanceof EffectChanceSkillTrigger)
                    makeCast((EffectChanceSkillTrigger) trigger, target);
            }
        }
    }

    private void makeCast(L2Skill skill, Creature target) {
        if (skill.getWeaponDependancy(_owner) && skill.checkCondition(_owner, target, false)) {
            if (skill.triggersChanceSkill()) // skill will trigger another skill, but only if its not chance skill
            {
                skill = SkillTable.getInstance().getInfo(skill.getTriggeredChanceId(), skill.getTriggeredChanceLevel());
                if (skill == null || skill.getSkillType() == SkillType.NOTDONE)
                    return;
            }

            if (_owner.isSkillDisabled(skill))
                return;

            if (skill.getReuseDelay() > 0)
                _owner.disableSkill(skill, skill.getReuseDelay());

            final Creature[] targets = skill.getTargetList(_owner, target);
            if (targets.length == 0)
                return;

            final Creature firstTarget = targets[0];

            _owner.broadcastPacket(new MagicSkillLaunched(_owner, skill, targets));
            _owner.broadcastPacket(new MagicSkillUse(_owner, firstTarget, skill.getId(), skill.getLevel(), 0, 0));

            // Launch the magic skill and calculate its effects
            // TODO: once core will support all possible effects, use effects (not handler)
            final ISkillHandler handler = SkillHandler.getInstance().getHandler(skill.getSkillType());
            if (handler != null)
                handler.useSkill(_owner, skill, targets);
            else
                skill.useSkill(_owner, targets);
        }
    }

    private void makeCast(EffectChanceSkillTrigger effect, Creature target) {
        if (effect == null || !effect.triggersChanceSkill())
            return;

        final L2Skill triggered = SkillTable.getInstance().getInfo(effect.getTriggeredChanceId(), effect.getTriggeredChanceLevel());
        if (triggered == null)
            return;

        final Creature caster = triggered.getTargetType() == SkillTargetType.SELF ? _owner : effect.getEffector();

        if (caster == null || triggered.getSkillType() == SkillType.NOTDONE || caster.isSkillDisabled(triggered))
            return;

        if (triggered.getReuseDelay() > 0)
            caster.disableSkill(triggered, triggered.getReuseDelay());

        final Creature[] targets = triggered.getTargetList(_owner, target);
        if (targets.length == 0)
            return;

        final Creature firstTarget = targets[0];
        final ISkillHandler handler = SkillHandler.getInstance().getHandler(triggered.getSkillType());

        _owner.broadcastPacket(new MagicSkillLaunched(_owner, triggered, targets));
        _owner.broadcastPacket(new MagicSkillUse(_owner, firstTarget, triggered.getId(), triggered.getLevel(), 0, 0));

        // Launch the magic skill and calculate its effects
        // TODO: once core will support all possible effects, use effects (not handler)
        if (handler != null)
            handler.useSkill(caster, triggered, targets);
        else
            triggered.useSkill(caster, targets);
    }
}