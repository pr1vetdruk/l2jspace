package ru.privetdruk.l2jspace.gameserver.network.clientpackets;

import ru.privetdruk.l2jspace.common.lang.StringUtil;
import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.sql.PlayerInfoTable;
import ru.privetdruk.l2jspace.gameserver.data.xml.NpcData;
import ru.privetdruk.l2jspace.gameserver.data.xml.PlayerData;
import ru.privetdruk.l2jspace.gameserver.data.xml.ScriptData;
import ru.privetdruk.l2jspace.gameserver.enums.QuestStatus;
import ru.privetdruk.l2jspace.gameserver.enums.ShortcutType;
import ru.privetdruk.l2jspace.gameserver.enums.actors.Sex;
import ru.privetdruk.l2jspace.gameserver.idfactory.IdFactory;
import ru.privetdruk.l2jspace.gameserver.model.Shortcut;
import ru.privetdruk.l2jspace.gameserver.model.World;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.PlayerTemplate;
import ru.privetdruk.l2jspace.gameserver.model.holder.ItemTemplateHolder;
import ru.privetdruk.l2jspace.gameserver.model.holder.skillnode.GeneralSkillNode;
import ru.privetdruk.l2jspace.gameserver.model.item.instance.ItemInstance;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CharCreateFail;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CharCreateOk;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.CharSelectInfo;
import ru.privetdruk.l2jspace.gameserver.scripting.Quest;

public final class RequestCharacterCreate extends L2GameClientPacket {
    private String _name;
    private int _race;
    private byte _sex;
    private int _classId;
    private byte _hairStyle;
    private byte _hairColor;
    private byte _face;

    @Override
    protected void readImpl() {
        _name = readS();
        _race = readD();
        _sex = (byte) readD();
        _classId = readD();
        readD(); // int
        readD(); // str
        readD(); // con
        readD(); // men
        readD(); // dex
        readD(); // wit
        _hairStyle = (byte) readD();
        _hairColor = (byte) readD();
        _face = (byte) readD();
    }

    @Override
    protected void runImpl() {
        // Invalid race.
        if (_race > 4 || _race < 0) {
            sendPacket(CharCreateFail.REASON_CREATION_FAILED);
            return;
        }

        // Invalid face.
        if (_face > 2 || _face < 0) {
            sendPacket(CharCreateFail.REASON_CREATION_FAILED);
            return;
        }

        // Invalid hair style.
        if (_hairStyle < 0 || (_sex == 0 && _hairStyle > 4) || (_sex != 0 && _hairStyle > 6)) {
            sendPacket(CharCreateFail.REASON_CREATION_FAILED);
            return;
        }

        // Invalid hair color.
        if (_hairColor > 3 || _hairColor < 0) {
            sendPacket(CharCreateFail.REASON_CREATION_FAILED);
            return;
        }

        // Invalid name typo.
        if (!StringUtil.isValidString(_name, Config.CNAME_TEMPLATE)) {
            sendPacket(CharCreateFail.REASON_INCORRECT_NAME);
            return;
        }

        // Your name is already taken by a NPC.
        if (NpcData.getInstance().getTemplateByName(_name) != null) {
            sendPacket(CharCreateFail.REASON_INCORRECT_NAME);
            return;
        }

        // You already have the maximum amount of characters for this account.
        if (PlayerInfoTable.getInstance().getCharactersInAcc(getClient().getAccountName()) >= 7) {
            sendPacket(CharCreateFail.REASON_TOO_MANY_CHARACTERS);
            return;
        }

        // The name already exists.
        if (PlayerInfoTable.getInstance().getPlayerObjectId(_name) > 0) {
            sendPacket(CharCreateFail.REASON_NAME_ALREADY_EXISTS);
            return;
        }

        // The class id related to this template is post-newbie.
        final PlayerTemplate template = PlayerData.getInstance().getTemplate(_classId);
        if (template == null || template.getClassBaseLevel() > 1) {
            sendPacket(CharCreateFail.REASON_CREATION_FAILED);
            return;
        }

        // Create the player Object.
        final Player player = Player.create(IdFactory.getInstance().getNextId(), template, getClient().getAccountName(), _name, _hairStyle, _hairColor, _face, Sex.VALUES[_sex]);
        if (player == null) {
            sendPacket(CharCreateFail.REASON_CREATION_FAILED);
            return;
        }

        // Set default values.
        player.getStatus().setMaxHpMp();

        // send acknowledgement
        sendPacket(CharCreateOk.STATIC_PACKET);

        World.getInstance().addObject(player);

        player.getPosition().set(template.getRandomSpawn());
        player.setTitle("");

        // Register shortcuts.
        player.getShortcutList().addShortcut(new Shortcut(0, 0, ShortcutType.ACTION, 2, -1, 1)); // attack shortcut
        player.getShortcutList().addShortcut(new Shortcut(3, 0, ShortcutType.ACTION, 5, -1, 1)); // take shortcut
        player.getShortcutList().addShortcut(new Shortcut(10, 0, ShortcutType.ACTION, 0, -1, 1)); // sit shortcut

        // Equip or add items, based on template.
        for (ItemTemplateHolder holder : template.getItems()) {
            final ItemInstance item = player.getInventory().addItem("Init", holder.getId(), holder.getValue(), player, null);

            // Tutorial book shortcut.
            if (holder.getId() == 5588)
                player.getShortcutList().addShortcut(new Shortcut(11, 0, ShortcutType.ITEM, item.getObjectId(), -1, 1));

            if (item.isEquipable() && holder.isEquipped())
                player.getInventory().equipItemAndRecord(item);
        }

        // Add skills.
        for (GeneralSkillNode skill : player.getAvailableAutoGetSkills()) {
            if (skill.getId() == 1001 || skill.getId() == 1177)
                player.getShortcutList().addShortcut(new Shortcut(1, 0, ShortcutType.SKILL, skill.getId(), 1, 1), false);

            if (skill.getId() == 1216)
                player.getShortcutList().addShortcut(new Shortcut(9, 0, ShortcutType.SKILL, skill.getId(), 1, 1), false);
        }

        // Tutorial runs here.
        final Quest quest = ScriptData.getInstance().getQuest("Tutorial");
        if (quest != null)
            quest.newQuestState(player).setState(QuestStatus.STARTED);

        player.setOnlineStatus(true, false);
        player.deleteMe();

        final CharSelectInfo csi = new CharSelectInfo(getClient().getAccountName(), getClient().getSessionId().playOkID1);
        sendPacket(csi);
        getClient().setCharSelectSlot(csi.getCharacterSlots());
    }
}