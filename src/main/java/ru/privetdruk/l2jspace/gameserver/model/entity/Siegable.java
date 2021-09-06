package ru.privetdruk.l2jspace.gameserver.model.entity;

import ru.privetdruk.l2jspace.gameserver.enums.SiegeSide;
import ru.privetdruk.l2jspace.gameserver.model.actor.Npc;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;

import java.util.Calendar;
import java.util.List;

public interface Siegable {
    public void startSiege();

    public void endSiege();

    public List<Clan> getAttackerClans();

    public List<Clan> getDefenderClans();

    public boolean checkSide(Clan clan, SiegeSide type);

    public boolean checkSides(Clan clan, SiegeSide... types);

    public boolean checkSides(Clan clan);

    public Npc getFlag(Clan clan);

    public Calendar getSiegeDate();
}