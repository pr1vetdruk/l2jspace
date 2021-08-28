package ru.privetdruk.l2jspace.gameserver.enums.skills;

public enum EffectType {
    BLOCK_BUFF,
    BLOCK_DEBUFF,

    BUFF,
    DEBUFF,

    CANCEL,

    CANCEL_DEBUFF,
    NEGATE,

    CLAN_GATE,
    CHANCE_SKILL_TRIGGER,
    INCREASE_CHARGES,

    DMG_OVER_TIME,
    HEAL,
    HEAL_OVER_TIME,
    MANA_DMG_OVER_TIME,
    MANA_HEAL,
    MANA_HEAL_OVER_TIME,

    ABORT_CAST,
    BLUFF,
    BETRAY,
    STUN,
    ROOT,
    SLEEP,
    MUTE,
    PHYSICAL_MUTE,
    SILENCE_MAGIC_PHYSICAL,
    FEAR,
    PARALYZE,
    PETRIFICATION,
    IMMOBILE_UNTIL_ATTACKED,
    STUN_SELF,
    CONFUSION,
    DISTRUST,
    RANDOMIZE_HATE,

    FAKE_DEATH,
    SILENT_MOVE,

    POLEARM_TARGET_SINGLE,

    SEED,
    SPOIL,

    REMOVE_TARGET,
    TARGET_ME,

    RELAXING,
    NOBLESSE_BLESSING,
    PROTECTION_BLESSING,
    FUSION,
    CHARM_OF_COURAGE,
    CHARM_OF_LUCK,
    INVINCIBLE,
    PHOENIX_BLESSING,

    THROW_UP,
    WARP,

    SIGNET_GROUND,
    SIGNET_EFFECT;

    public static boolean isntCancellable(EffectType type) {
        return type == CHARM_OF_COURAGE || type == CHARM_OF_LUCK || type == NOBLESSE_BLESSING || type == PROTECTION_BLESSING;
    }
}