package ru.privetdruk.l2jspace.gameserver.model.olympiad;

import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.enums.OlympiadType;
import ru.privetdruk.l2jspace.gameserver.model.holder.IntIntHolder;

import java.util.List;

public class OlympiadGameNonClassed extends OlympiadGameNormal {
    private OlympiadGameNonClassed(int id, Participant[] opponents) {
        super(id, opponents);
    }

    @Override
    public final OlympiadType getType() {
        return OlympiadType.NON_CLASSED;
    }

    @Override
    protected final int getDivider() {
        return Config.OLY_DIVIDER_NON_CLASSED;
    }

    @Override
    protected final IntIntHolder[] getReward() {
        return Config.OLY_NONCLASSED_REWARD;
    }

    protected static final OlympiadGameNonClassed createGame(int id, List<Integer> list) {
        final Participant[] opponents = OlympiadGameNormal.createListOfParticipants(list);
        if (opponents == null)
            return null;

        return new OlympiadGameNonClassed(id, opponents);
    }
}