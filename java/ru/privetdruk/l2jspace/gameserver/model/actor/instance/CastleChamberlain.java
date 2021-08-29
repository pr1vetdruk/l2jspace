package ru.privetdruk.l2jspace.gameserver.model.actor.instance;

import ru.privetdruk.l2jspace.common.lang.StringUtil;
import ru.privetdruk.l2jspace.config.Config;
import ru.privetdruk.l2jspace.gameserver.data.manager.CastleManorManager;
import ru.privetdruk.l2jspace.gameserver.data.manager.SevenSignsManager;
import ru.privetdruk.l2jspace.gameserver.data.sql.ClanTable;
import ru.privetdruk.l2jspace.gameserver.data.xml.TeleportData;
import ru.privetdruk.l2jspace.gameserver.enums.CabalType;
import ru.privetdruk.l2jspace.gameserver.enums.SealType;
import ru.privetdruk.l2jspace.gameserver.enums.TeleportType;
import ru.privetdruk.l2jspace.gameserver.enums.actors.NpcTalkCond;
import ru.privetdruk.l2jspace.gameserver.model.actor.Player;
import ru.privetdruk.l2jspace.gameserver.model.actor.ai.type.CastleManagerNpcAI;
import ru.privetdruk.l2jspace.gameserver.model.actor.ai.type.CreatureAI;
import ru.privetdruk.l2jspace.gameserver.model.actor.template.NpcTemplate;
import ru.privetdruk.l2jspace.gameserver.model.entity.Castle;
import ru.privetdruk.l2jspace.gameserver.model.pledge.Clan;
import ru.privetdruk.l2jspace.gameserver.network.SystemMessageId;
import ru.privetdruk.l2jspace.gameserver.network.serverpackets.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * An instance type extending {@link Merchant}, used for castle chamberlains.<br>
 * <br>
 * It handles following actions :
 * <ul>
 * <li>Tax rate control</li>
 * <li>Regional manor system control</li>
 * <li>Castle treasure control</li>
 * <li>Siege time modifier</li>
 * <li>Items production</li>
 * <li>Doors management && Doors/walls upgrades</li>
 * <li>Traps management && upgrades</li>
 * </ul>
 */
public class CastleChamberlain extends Merchant {
    private static final int CERTIFICATES_BUNDLE = 10;
    private static final int CERTIFICATES_PRICE = 1000;

    private int _preHour = 6;

    public CastleChamberlain(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public CreatureAI getAI() {
        CreatureAI ai = _ai;

        if (ai == null) {
            synchronized (this) {
                ai = _ai;

                if (ai == null) {
                    _ai = ai = new CastleManagerNpcAI(this);
                }
            }
        }

        return ai;
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        final NpcTalkCond condition = getNpcTalkCond(player);
        if (condition == NpcTalkCond.NONE) {
            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile("data/html/chamberlain/noprivs.htm");
            player.sendPacket(html);
            return;
        }

        if (condition == NpcTalkCond.UNDER_SIEGE) {
            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile("data/html/chamberlain/busy.htm");
            player.sendPacket(html);
            return;
        }

        StringTokenizer st = new StringTokenizer(command, " ");
        String actualCommand = st.nextToken(); // Get actual command

        String val = (st.hasMoreTokens()) ? st.nextToken() : "";

        if (actualCommand.equalsIgnoreCase("banish_foreigner")) {
            if (!validatePrivileges(player, Clan.CP_CS_DISMISS))
                return;

            // Move non-clan members off castle area, and send html
            getCastle().banishForeigners();
            sendFileMessage(player, "data/html/chamberlain/banishafter.htm");
        } else if (actualCommand.equalsIgnoreCase("banish_foreigner_show")) {
            if (!validatePrivileges(player, Clan.CP_CS_DISMISS))
                return;

            sendFileMessage(player, "data/html/chamberlain/banishfore.htm");
        } else if (actualCommand.equalsIgnoreCase("manage_functions")) {
            if (!validatePrivileges(player, Clan.CP_CS_SET_FUNCTIONS))
                return;

            sendFileMessage(player, "data/html/chamberlain/chamberlain-manage.htm");
        } else if (actualCommand.equalsIgnoreCase("functions")) {
            if (!validatePrivileges(player, Clan.CP_CS_USE_FUNCTIONS))
                return;

            if (val.equalsIgnoreCase("tele")) {
                final Castle.CastleFunction chf = getCastle().getFunction(Castle.FUNC_TELEPORT);
                if (chf == null) {
                    final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                    html.setFile("data/html/chamberlain/chamberlain-nac.htm");
                    html.replace("%objectId%", getObjectId());
                    player.sendPacket(html);
                    return;
                }

                TeleportData.getInstance().showTeleportList(player, this, (chf.getLvl() == 2) ? TeleportType.CHF_LEVEL_2 : TeleportType.CHF_LEVEL_1);
            } else if (val.equalsIgnoreCase("support")) {
                final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

                final Castle.CastleFunction chf = getCastle().getFunction(Castle.FUNC_SUPPORT);
                if (chf == null)
                    html.setFile("data/html/chamberlain/chamberlain-nac.htm");
                else {
                    html.setFile("data/html/chamberlain/support" + chf.getLvl() + ".htm");
                    html.replace("%mp%", (int) getStatus().getMp());
                }
                html.replace("%objectId%", getObjectId());
                player.sendPacket(html);
            } else if (val.equalsIgnoreCase("back"))
                showChatWindow(player);
            else {
                final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                html.setFile("data/html/chamberlain/chamberlain-functions.htm");

                final Castle.CastleFunction chfExp = getCastle().getFunction(Castle.FUNC_RESTORE_EXP);
                if (chfExp != null)
                    html.replace("%xp_regen%", chfExp.getLvl());
                else
                    html.replace("%xp_regen%", "0");

                final Castle.CastleFunction chfHp = getCastle().getFunction(Castle.FUNC_RESTORE_HP);
                if (chfHp != null)
                    html.replace("%hp_regen%", chfHp.getLvl());
                else
                    html.replace("%hp_regen%", "0");

                final Castle.CastleFunction chfMp = getCastle().getFunction(Castle.FUNC_RESTORE_MP);
                if (chfMp != null)
                    html.replace("%mp_regen%", chfMp.getLvl());
                else
                    html.replace("%mp_regen%", "0");

                html.replace("%npcId%", getNpcId());
                html.replace("%objectId%", getObjectId());
                player.sendPacket(html);
            }
        } else if (actualCommand.equalsIgnoreCase("manage")) {
            if (!validatePrivileges(player, Clan.CP_CS_SET_FUNCTIONS))
                return;

            if (val.equalsIgnoreCase("recovery")) {
                if (st.hasMoreTokens()) {
                    if (getCastle().getOwnerId() == 0)
                        return;

                    val = st.nextToken();

                    if (val.equalsIgnoreCase("hp_cancel")) {
                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                        html.setFile("data/html/chamberlain/functions-cancel.htm");
                        html.replace("%apply%", "recovery hp 0");
                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    } else if (val.equalsIgnoreCase("mp_cancel")) {
                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                        html.setFile("data/html/chamberlain/functions-cancel.htm");
                        html.replace("%apply%", "recovery mp 0");
                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    } else if (val.equalsIgnoreCase("exp_cancel")) {
                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                        html.setFile("data/html/chamberlain/functions-cancel.htm");
                        html.replace("%apply%", "recovery exp 0");
                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    } else if (val.equalsIgnoreCase("edit_hp")) {
                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                        html.setFile("data/html/chamberlain/functions-apply.htm");
                        html.replace("%name%", "Fireplace (HP Recovery Device)");

                        final int percent = Integer.parseInt(st.nextToken());

                        int cost = switch (percent) {
                            case 80 -> Config.CS_HPREG1_FEE;
                            case 120 -> Config.CS_HPREG2_FEE;
                            case 180 -> Config.CS_HPREG3_FEE;
                            case 240 -> Config.CS_HPREG4_FEE;
                            default -> Config.CS_HPREG5_FEE;
                        };

                        html.replace("%cost%", cost + "</font> adenas / " + (Config.CS_HPREG_FEE_RATIO / 86400000) + " day</font>)");
                        html.replace("%use%", "Provides additional HP recovery for clan members in the castle.<font color=\"00FFFF\">" + percent + "%</font>");
                        html.replace("%apply%", "recovery hp " + percent);
                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    } else if (val.equalsIgnoreCase("edit_mp")) {
                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                        html.setFile("data/html/chamberlain/functions-apply.htm");
                        html.replace("%name%", "Carpet (MP Recovery)");

                        final int percent = Integer.parseInt(st.nextToken());

                        int cost = switch (percent) {
                            case 5 -> Config.CS_MPREG1_FEE;
                            case 15 -> Config.CS_MPREG2_FEE;
                            case 30 -> Config.CS_MPREG3_FEE;
                            default -> Config.CS_MPREG4_FEE;
                        };
                        html.replace("%cost%", cost + "</font> adenas / " + (Config.CS_MPREG_FEE_RATIO / 86400000) + " day</font>)");
                        html.replace("%use%", "Provides additional MP recovery for clan members in the castle.<font color=\"00FFFF\">" + percent + "%</font>");
                        html.replace("%apply%", "recovery mp " + percent);
                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    } else if (val.equalsIgnoreCase("edit_exp")) {
                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                        html.setFile("data/html/chamberlain/functions-apply.htm");
                        html.replace("%name%", "Chandelier (EXP Recovery Device)");

                        final int percent = Integer.parseInt(st.nextToken());

                        int cost = switch (percent) {
                            case 15 -> Config.CS_EXPREG1_FEE;
                            case 25 -> Config.CS_EXPREG2_FEE;
                            case 35 -> Config.CS_EXPREG3_FEE;
                            default -> Config.CS_EXPREG4_FEE;
                        };
                        html.replace("%cost%", cost + "</font> adenas / " + (Config.CS_EXPREG_FEE_RATIO / 86400000) + " day</font>)");
                        html.replace("%use%", "Restores the Exp of any clan member who is resurrected in the castle.<font color=\"00FFFF\">" + percent + "%</font>");
                        html.replace("%apply%", "recovery exp " + percent);
                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    } else if (val.equalsIgnoreCase("hp")) {
                        val = st.nextToken();
                        final int percent = Integer.parseInt(val);

                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

                        final Castle.CastleFunction chf = getCastle().getFunction(Castle.FUNC_RESTORE_HP);
                        if (chf != null && chf.getLvl() == percent) {
                            html.setFile("data/html/chamberlain/functions-used.htm");
                            html.replace("%val%", val + "%");
                            html.replace("%objectId%", getObjectId());
                            player.sendPacket(html);
                            return;
                        }

                        html.setFile("data/html/chamberlain/functions-apply_confirmed.htm");

                        int fee;
                        switch (percent) {
                            case 0 -> {
                                fee = 0;
                                html.setFile("data/html/chamberlain/functions-cancel_confirmed.htm");
                            }
                            case 80 -> fee = Config.CS_HPREG1_FEE;
                            case 120 -> fee = Config.CS_HPREG2_FEE;
                            case 180 -> fee = Config.CS_HPREG3_FEE;
                            case 240 -> fee = Config.CS_HPREG4_FEE;
                            default -> fee = Config.CS_HPREG5_FEE;
                        }

                        if (!getCastle().updateFunctions(player, Castle.FUNC_RESTORE_HP, percent, fee, Config.CS_HPREG_FEE_RATIO, (getCastle().getFunction(Castle.FUNC_RESTORE_HP) == null)))
                            html.setFile("data/html/chamberlain/low_adena.htm");

                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    } else if (val.equalsIgnoreCase("mp")) {
                        val = st.nextToken();
                        final int percent = Integer.parseInt(val);

                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

                        final Castle.CastleFunction chf = getCastle().getFunction(Castle.FUNC_RESTORE_MP);
                        if (chf != null && chf.getLvl() == percent) {
                            html.setFile("data/html/chamberlain/functions-used.htm");
                            html.replace("%val%", val + "%");
                            html.replace("%objectId%", getObjectId());
                            player.sendPacket(html);
                            return;
                        }

                        html.setFile("data/html/chamberlain/functions-apply_confirmed.htm");

                        int fee;
                        switch (percent) {
                            case 0 -> {
                                fee = 0;
                                html.setFile("data/html/chamberlain/functions-cancel_confirmed.htm");
                            }
                            case 5 -> fee = Config.CS_MPREG1_FEE;
                            case 15 -> fee = Config.CS_MPREG2_FEE;
                            case 30 -> fee = Config.CS_MPREG3_FEE;
                            default -> fee = Config.CS_MPREG4_FEE;
                        }
                        if (!getCastle().updateFunctions(player, Castle.FUNC_RESTORE_MP, percent, fee, Config.CS_MPREG_FEE_RATIO, (getCastle().getFunction(Castle.FUNC_RESTORE_MP) == null)))
                            html.setFile("data/html/chamberlain/low_adena.htm");

                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    } else if (val.equalsIgnoreCase("exp")) {
                        val = st.nextToken();
                        final int percent = Integer.parseInt(val);

                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

                        final Castle.CastleFunction chf = getCastle().getFunction(Castle.FUNC_RESTORE_EXP);
                        if (chf != null && chf.getLvl() == percent) {
                            html.setFile("data/html/chamberlain/functions-used.htm");
                            html.replace("%val%", val + "%");
                            html.replace("%objectId%", getObjectId());
                            player.sendPacket(html);
                            return;
                        }

                        html.setFile("data/html/chamberlain/functions-apply_confirmed.htm");

                        int fee;
                        switch (percent) {
                            case 0 -> {
                                fee = 0;
                                html.setFile("data/html/chamberlain/functions-cancel_confirmed.htm");
                            }
                            case 15 -> fee = Config.CS_EXPREG1_FEE;
                            case 25 -> fee = Config.CS_EXPREG2_FEE;
                            case 35 -> fee = Config.CS_EXPREG3_FEE;
                            default -> fee = Config.CS_EXPREG4_FEE;
                        }
                        if (!getCastle().updateFunctions(player, Castle.FUNC_RESTORE_EXP, percent, fee, Config.CS_EXPREG_FEE_RATIO, (getCastle().getFunction(Castle.FUNC_RESTORE_EXP) == null)))
                            html.setFile("data/html/chamberlain/low_adena.htm");

                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    }
                } else {
                    final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                    html.setFile("data/html/chamberlain/edit_recovery.htm");
                    String hp = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 80\">80%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 120\">120%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 180\">180%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 240\">240%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 300\">300%</a>]";
                    String exp = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 15\">15%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 25\">25%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 35\">35%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 50\">50%</a>]";
                    String mp = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 5\">5%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 15\">15%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 30\">30%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 40\">40%</a>]";

                    final Castle.CastleFunction chfHp = getCastle().getFunction(Castle.FUNC_RESTORE_HP);
                    if (chfHp != null) {
                        html.replace("%hp_recovery%", chfHp.getLvl() + "%</font> (<font color=\"FFAABB\">" + chfHp.getLease() + "</font> adenas /" + Config.CS_HPREG_FEE_RATIO / 86400000 + " day");
                        html.replace("%hp_period%", "Withdraw the fee for the next time at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfHp.getEndTime()));
                        html.replace("%change_hp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery hp_cancel\">Deactivate</a>]" + hp);
                    } else {
                        html.replace("%hp_recovery%", "none");
                        html.replace("%hp_period%", "none");
                        html.replace("%change_hp%", hp);
                    }

                    final Castle.CastleFunction chfExp = getCastle().getFunction(Castle.FUNC_RESTORE_EXP);
                    if (chfExp != null) {
                        html.replace("%exp_recovery%", chfExp.getLvl() + "%</font> (<font color=\"FFAABB\">" + chfExp.getLease() + "</font> adenas /" + Config.CS_EXPREG_FEE_RATIO / 86400000 + " day)");
                        html.replace("%exp_period%", "Withdraw the fee for the next time at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfExp.getEndTime()));
                        html.replace("%change_exp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery exp_cancel\">Deactivate</a>]" + exp);
                    } else {
                        html.replace("%exp_recovery%", "none");
                        html.replace("%exp_period%", "none");
                        html.replace("%change_exp%", exp);
                    }

                    final Castle.CastleFunction chfMp = getCastle().getFunction(Castle.FUNC_RESTORE_MP);
                    if (chfMp != null) {
                        html.replace("%mp_recovery%", chfMp.getLvl() + "%</font> (<font color=\"FFAABB\">" + chfMp.getLease() + "</font> adenas /" + Config.CS_MPREG_FEE_RATIO / 86400000 + " day)");
                        html.replace("%mp_period%", "Withdraw the fee for the next time at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(getCastle().getFunction(Castle.FUNC_RESTORE_MP).getEndTime()));
                        html.replace("%change_mp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery mp_cancel\">Deactivate</a>]" + mp);
                    } else {
                        html.replace("%mp_recovery%", "none");
                        html.replace("%mp_period%", "none");
                        html.replace("%change_mp%", mp);
                    }

                    html.replace("%objectId%", getObjectId());
                    player.sendPacket(html);
                }
            } else if (val.equalsIgnoreCase("other")) {
                if (st.hasMoreTokens()) {
                    if (getCastle().getOwnerId() == 0)
                        return;

                    val = st.nextToken();

                    if (val.equalsIgnoreCase("tele_cancel")) {
                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                        html.setFile("data/html/chamberlain/functions-cancel.htm");
                        html.replace("%apply%", "other tele 0");
                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    } else if (val.equalsIgnoreCase("support_cancel")) {
                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                        html.setFile("data/html/chamberlain/functions-cancel.htm");
                        html.replace("%apply%", "other support 0");
                        html.replace("%apply%", "other tele 0");
                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    } else if (val.equalsIgnoreCase("edit_support")) {
                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                        html.setFile("data/html/chamberlain/functions-apply.htm");
                        html.replace("%name%", "Insignia (Supplementary Magic)");

                        final int stage = Integer.parseInt(st.nextToken());

                        int cost = switch (stage) {
                            case 1 -> Config.CS_SUPPORT1_FEE;
                            case 2 -> Config.CS_SUPPORT2_FEE;
                            case 3 -> Config.CS_SUPPORT3_FEE;
                            default -> Config.CS_SUPPORT4_FEE;
                        };
                        html.replace("%cost%", cost + "</font> adenas / " + (Config.CS_SUPPORT_FEE_RATIO / 86400000) + " day</font>)");
                        html.replace("%use%", "Enables the use of supplementary magic.");
                        html.replace("%apply%", "other support " + stage);
                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    } else if (val.equalsIgnoreCase("edit_tele")) {
                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                        html.setFile("data/html/chamberlain/functions-apply.htm");
                        html.replace("%name%", "Mirror (Teleportation Device)");

                        final int stage = Integer.parseInt(st.nextToken());

                        int cost;
                        if (stage == 1) {
                            cost = Config.CS_TELE1_FEE;
                        } else {
                            cost = Config.CS_TELE2_FEE;
                        }

                        html.replace("%cost%", cost + "</font> adenas / " + (Config.CS_TELE_FEE_RATIO / 86400000) + " day</font>)");
                        html.replace("%use%", "Teleports clan members in a castle to the target <font color=\"00FFFF\">Stage " + stage + "</font> staging area");
                        html.replace("%apply%", "other tele " + stage);
                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    } else if (val.equalsIgnoreCase("tele")) {
                        val = st.nextToken();
                        final int lvl = Integer.parseInt(val);

                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

                        final Castle.CastleFunction chf = getCastle().getFunction(Castle.FUNC_TELEPORT);
                        if (chf != null && chf.getLvl() == lvl) {
                            html.setFile("data/html/chamberlain/functions-used.htm");
                            html.replace("%val%", "Stage " + val);
                            html.replace("%objectId%", getObjectId());
                            player.sendPacket(html);
                            return;
                        }

                        html.setFile("data/html/chamberlain/functions-apply_confirmed.htm");

                        int fee;
                        switch (lvl) {
                            case 0 -> {
                                fee = 0;
                                html.setFile("data/html/chamberlain/functions-cancel_confirmed.htm");
                            }
                            case 1 -> fee = Config.CS_TELE1_FEE;
                            default -> fee = Config.CS_TELE2_FEE;
                        }
                        if (!getCastle().updateFunctions(player, Castle.FUNC_TELEPORT, lvl, fee, Config.CS_TELE_FEE_RATIO, (getCastle().getFunction(Castle.FUNC_TELEPORT) == null)))
                            html.setFile("data/html/chamberlain/low_adena.htm");

                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    } else if (val.equalsIgnoreCase("support")) {
                        val = st.nextToken();
                        final int lvl = Integer.parseInt(val);

                        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

                        final Castle.CastleFunction chf = getCastle().getFunction(Castle.FUNC_SUPPORT);
                        if (chf != null && chf.getLvl() == lvl) {
                            html.setFile("data/html/chamberlain/functions-used.htm");
                            html.replace("%val%", "Stage " + val);
                            html.replace("%objectId%", getObjectId());
                            player.sendPacket(html);
                            return;
                        }

                        html.setFile("data/html/chamberlain/functions-apply_confirmed.htm");

                        int fee;
                        switch (lvl) {
                            case 0 -> {
                                fee = 0;
                                html.setFile("data/html/chamberlain/functions-cancel_confirmed.htm");
                            }
                            case 1 -> fee = Config.CS_SUPPORT1_FEE;
                            case 2 -> fee = Config.CS_SUPPORT2_FEE;
                            case 3 -> fee = Config.CS_SUPPORT3_FEE;
                            default -> fee = Config.CS_SUPPORT4_FEE;
                        }
                        if (!getCastle().updateFunctions(player, Castle.FUNC_SUPPORT, lvl, fee, Config.CS_SUPPORT_FEE_RATIO, (chf == null)))
                            html.setFile("data/html/chamberlain/low_adena.htm");

                        html.replace("%objectId%", getObjectId());
                        player.sendPacket(html);
                    }
                } else {
                    NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                    html.setFile("data/html/chamberlain/edit_other.htm");

                    String tele = "[<a action=\"bypass -h npc_%objectId%_manage other edit_tele 1\">Level 1</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_tele 2\">Level 2</a>]";
                    String support = "[<a action=\"bypass -h npc_%objectId%_manage other edit_support 1\">Level 1</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 2\">Level 2</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 3\">Level 3</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 4\">Level 4</a>]";

                    final Castle.CastleFunction chfTel = getCastle().getFunction(Castle.FUNC_TELEPORT);
                    if (chfTel != null) {
                        html.replace("%tele%", "Stage " + chfTel.getLvl() + "</font> (<font color=\"FFAABB\">" + chfTel.getLease() + "</font> adenas /" + Config.CS_TELE_FEE_RATIO / 86400000 + " day)");
                        html.replace("%tele_period%", "Withdraw the fee for the next time at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfTel.getEndTime()));
                        html.replace("%change_tele%", "[<a action=\"bypass -h npc_%objectId%_manage other tele_cancel\">Deactivate</a>]" + tele);
                    } else {
                        html.replace("%tele%", "none");
                        html.replace("%tele_period%", "none");
                        html.replace("%change_tele%", tele);
                    }

                    final Castle.CastleFunction chfSup = getCastle().getFunction(Castle.FUNC_SUPPORT);
                    if (chfSup != null) {
                        html.replace("%support%", "Stage " + chfSup.getLvl() + "</font> (<font color=\"FFAABB\">" + chfSup.getLease() + "</font> adenas /" + Config.CS_SUPPORT_FEE_RATIO / 86400000 + " Day)");
                        html.replace("%support_period%", "Withdraw the fee for the next time at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfSup.getEndTime()));
                        html.replace("%change_support%", "[<a action=\"bypass -h npc_%objectId%_manage other support_cancel\">Deactivate</a>]" + support);
                    } else {
                        html.replace("%support%", "none");
                        html.replace("%support_period%", "none");
                        html.replace("%change_support%", support);
                    }

                    html.replace("%objectId%", getObjectId());
                    player.sendPacket(html);
                }
            } else if (val.equalsIgnoreCase("back"))
                showChatWindow(player);
            else {
                NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                html.setFile("data/html/chamberlain/manage.htm");
                html.replace("%objectId%", getObjectId());
                player.sendPacket(html);
            }
        } else if (actualCommand.equalsIgnoreCase("support")) {
            if (!validatePrivileges(player, Clan.CP_CS_USE_FUNCTIONS))
                return;

            final Castle.CastleFunction chf = getCastle().getFunction(Castle.FUNC_SUPPORT);
            if (chf == null || chf.getLvl() == 0)
                return;

            if (player.isCursedWeaponEquipped()) {
                // Custom system message
                player.sendMessage("The wielder of a cursed weapon cannot receive outside heals or buffs");
                return;
            }

            setTarget(player);

            try {
                final int id = Integer.parseInt(val);
                final int lvl = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 0;

                getAI().tryToCast(player, id, lvl);
            } catch (Exception e) {
                player.sendMessage("Invalid skill, contact your server support.");
            }
        } else if (actualCommand.equalsIgnoreCase("support_back")) {
            if (!validatePrivileges(player, Clan.CP_CS_USE_FUNCTIONS))
                return;

            final Castle.CastleFunction chf = getCastle().getFunction(Castle.FUNC_SUPPORT);
            if (chf == null || chf.getLvl() == 0)
                return;

            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile("data/html/chamberlain/support" + chf.getLvl() + ".htm");
            html.replace("%mp%", (int) getStatus().getMp());
            html.replace("%objectId%", getObjectId());
            player.sendPacket(html);
        } else if (actualCommand.equalsIgnoreCase("products")) {
            if (!validatePrivileges(player, Clan.CP_CS_USE_FUNCTIONS))
                return;

            sendFileMessage(player, "data/html/chamberlain/products.htm");
        } else if (actualCommand.equalsIgnoreCase("list_siege_clans")) {
            if (!validatePrivileges(player, Clan.CP_CS_MANAGE_SIEGE))
                return;

            player.sendPacket(new SiegeInfo(getCastle()));
        } else if (actualCommand.equalsIgnoreCase("receive_report")) {
            if (condition == NpcTalkCond.CLAN_MEMBER)
                sendFileMessage(player, "data/html/chamberlain/noprivs.htm");
            else {
                final Clan clan = ClanTable.getInstance().getClan(getCastle().getOwnerId());

                final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                html.setFile("data/html/chamberlain/report.htm");
                html.replace("%objectId%", getObjectId());
                html.replace("%clanname%", clan.getName());
                html.replace("%clanleadername%", clan.getLeaderName());
                html.replace("%castlename%", getCastle().getName());
                html.replace("%ss_event%", SevenSignsManager.getInstance().getCurrentPeriod().getName());

                switch (SevenSignsManager.getInstance().getSealOwner(SealType.AVARICE)) {
                    case NORMAL -> html.replace("%ss_avarice%", "Not in Possession");
                    case DAWN -> html.replace("%ss_avarice%", "Lords of Dawn");
                    case DUSK -> html.replace("%ss_avarice%", "Revolutionaries of Dusk");
                }

                switch (SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS)) {
                    case NORMAL -> html.replace("%ss_gnosis%", "Not in Possession");
                    case DAWN -> html.replace("%ss_gnosis%", "Lords of Dawn");
                    case DUSK -> html.replace("%ss_gnosis%", "Revolutionaries of Dusk");
                }

                switch (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE)) {
                    case NORMAL -> html.replace("%ss_strife%", "Not in Possession");
                    case DAWN -> html.replace("%ss_strife%", "Lords of Dawn");
                    case DUSK -> html.replace("%ss_strife%", "Revolutionaries of Dusk");
                }
                player.sendPacket(html);
            }
        } else if (actualCommand.equalsIgnoreCase("items")) {
            if (!validatePrivileges(player, Clan.CP_CS_USE_FUNCTIONS))
                return;

            if (val.isEmpty())
                return;

            showBuyWindow(player, Integer.parseInt(val + "1"));
        }  else if (actualCommand.equalsIgnoreCase("manage_vault")) {
            if (!validatePrivileges(player, Clan.CP_CS_TAXES))
                return;

            String filename = "data/html/chamberlain/vault.htm";
            int amount = 0;

            if (val.equalsIgnoreCase("deposit")) {
                try {
                    amount = Integer.parseInt(st.nextToken());
                } catch (NoSuchElementException ignored) {
                }

                if (amount > 0 && getCastle().getTreasury() + amount < Integer.MAX_VALUE) {
                    if (player.reduceAdena("Castle", amount, this, true))
                        getCastle().addToTreasuryNoTax(amount);
                }
            } else if (val.equalsIgnoreCase("withdraw")) {
                try {
                    amount = Integer.parseInt(st.nextToken());
                } catch (NoSuchElementException ignored) {
                }

                if (amount > 0) {
                    if (getCastle().getTreasury() < amount)
                        filename = "data/html/chamberlain/vault-no.htm";
                    else {
                        if (getCastle().addToTreasuryNoTax((-1) * amount))
                            player.addAdena("Castle", amount, this, true);
                    }
                }
            }
            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile(filename);
            html.replace("%objectId%", getObjectId());
            html.replace("%tax_income%", StringUtil.formatNumber(getCastle().getTreasury()));
            html.replace("%withdraw_amount%", StringUtil.formatNumber(amount));
            player.sendPacket(html);
        } else if (actualCommand.equalsIgnoreCase("operate_door")) // door control
        {
            if (!validatePrivileges(player, Clan.CP_CS_OPEN_DOOR))
                return;

            if (val.isEmpty()) {
                final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                html.setFile("data/html/chamberlain/" + getNpcId() + "-d.htm");
                html.replace("%objectId%", getObjectId());
                player.sendPacket(html);
                return;
            }

            boolean open = (Integer.parseInt(val) == 1);
            while (st.hasMoreTokens())
                getCastle().openCloseDoor(player, Integer.parseInt(st.nextToken()), open);

            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile((open) ? "data/html/chamberlain/doors-open.htm" : "data/html/chamberlain/doors-close.htm");
            html.replace("%objectId%", getObjectId());
            player.sendPacket(html);

        } else if (actualCommand.equalsIgnoreCase("tax_set")) // tax rates control
        {
            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

            if (!validatePrivileges(player, Clan.CP_CS_TAXES))
                html.setFile("data/html/chamberlain/tax.htm");
            else {
                if (!val.isEmpty())
                    getCastle().setTaxPercent(player, Integer.parseInt(val));

                html.setFile("data/html/chamberlain/tax-adjust.htm");
            }

            html.replace("%objectId%", getObjectId());
            html.replace("%tax%", getCastle().getTaxPercent());
            player.sendPacket(html);
        } else if (actualCommand.equalsIgnoreCase("manor")) {
            if (!validatePrivileges(player, Clan.CP_CS_MANOR_ADMIN))
                return;

            String filename = "";
            if (!Config.ALLOW_MANOR)
                filename = "data/html/npcdefault.htm";
            else {
                int cmd = Integer.parseInt(val);
                filename = switch (cmd) {
                    case 0 -> "data/html/chamberlain/manor/manor.htm";

                    // TODO: correct in html's to 1
                    case 4 -> "data/html/chamberlain/manor/manor_help00" + st.nextToken() + ".htm";
                    default -> "data/html/chamberlain/no.htm";
                };
            }

            if (filename.length() != 0) {
                final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                html.setFile(filename);
                html.replace("%objectId%", getObjectId());
                player.sendPacket(html);
            }
        } else if (command.startsWith("manor_menu_select")) {
            if (!validatePrivileges(player, Clan.CP_CS_MANOR_ADMIN))
                return;

            final CastleManorManager manor = CastleManorManager.getInstance();
            if (manor.isUnderMaintenance()) {
                player.sendPacket(ActionFailed.STATIC_PACKET);
                player.sendPacket(SystemMessageId.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
                return;
            }

            final String params = command.substring(command.indexOf("?") + 1);
            final StringTokenizer str = new StringTokenizer(params, "&");

            final int ask = Integer.parseInt(str.nextToken().split("=")[1]);
            final int state = Integer.parseInt(str.nextToken().split("=")[1]);
            final boolean time = str.nextToken().split("=")[1].equals("1");

            final int castleId = (state == -1) ? getCastle().getCastleId() : state;

            switch (ask) {
                case 3: // Current seeds (Manor info)
                    player.sendPacket(new ExShowSeedInfo(castleId, time, true));
                    break;

                case 4: // Current crops (Manor info)
                    player.sendPacket(new ExShowCropInfo(castleId, time, true));
                    break;

                case 5: // Basic info (Manor info)
                    player.sendPacket(new ExShowManorDefaultInfo(true));
                    break;

                case 7: // Edit seed setup
                    if (manor.isManorApproved())
                        player.sendPacket(SystemMessageId.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_6_AM_AND_8_PM);
                    else
                        player.sendPacket(new ExShowSeedSetting(castleId));
                    break;

                case 8: // Edit crop setup
                    if (manor.isManorApproved())
                        player.sendPacket(SystemMessageId.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_6_AM_AND_8_PM);
                    else
                        player.sendPacket(new ExShowCropSetting(castleId));
                    break;
            }
        } else if (actualCommand.equalsIgnoreCase("siege_change")) // set siege time
        {
            if (!validatePrivileges(player, Clan.CP_CS_MANAGE_SIEGE))
                return;

            if (getCastle().getSiege().getSiegeRegistrationEndDate() < Calendar.getInstance().getTimeInMillis())
                sendFileMessage(player, "data/html/chamberlain/siegetime1.htm");
            else if (getCastle().getSiege().isTimeRegistrationOver())
                sendFileMessage(player, "data/html/chamberlain/siegetime2.htm");
            else
                sendFileMessage(player, "data/html/chamberlain/siegetime3.htm");
        } else if (actualCommand.equalsIgnoreCase("siege_time_set")) // set preDay
        {
            switch (Integer.parseInt(val)) {
                case 1:
                    _preHour = Integer.parseInt(st.nextToken());
                    break;

                default:
                    break;
            }

            if (_preHour != 6) {
                getCastle().getSiegeDate().set(Calendar.HOUR_OF_DAY, _preHour + 12);

                // now store the changed time and finished next Siege Time registration
                getCastle().getSiege().endTimeRegistration(false);
                sendFileMessage(player, "data/html/chamberlain/siegetime8.htm");
                return;
            }

            sendFileMessage(player, "data/html/chamberlain/siegetime6.htm");
        } else if (actualCommand.equals("give_crown")) {
            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

            if (condition == NpcTalkCond.OWNER) {
                if (player.getInventory().getItemByItemId(6841) == null) {
                    player.addItem("Castle Crown", 6841, 1, player, true);

                    html.setFile("data/html/chamberlain/gavecrown.htm");
                    html.replace("%CharName%", player.getName());
                    html.replace("%FeudName%", getCastle().getName());
                } else
                    html.setFile("data/html/chamberlain/hascrown.htm");
            } else
                html.setFile("data/html/chamberlain/noprivs.htm");

            player.sendPacket(html);
        } else if (actualCommand.equals("manor_certificate")) {
            if (!validatePrivileges(player, Clan.CP_CS_USE_FUNCTIONS))
                return;

            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

            // Player is registered as dusk, or we aren't in the good side of competition.
            if (SevenSignsManager.getInstance().isSealValidationPeriod()) {
                if (SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId()) == CabalType.DUSK)
                    html.setFile("data/html/chamberlain/not-dawn-or-event.htm");
                    // We already reached the tickets limit.
                else if (getCastle().getLeftCertificates() == 0)
                    html.setFile("data/html/chamberlain/not-enough-ticket.htm");
                else {
                    html.setFile("data/html/chamberlain/sell-dawn-ticket.htm");
                    html.replace("%left%", getCastle().getLeftCertificates());
                    html.replace("%bundle%", CERTIFICATES_BUNDLE);
                    html.replace("%price%", CERTIFICATES_PRICE);
                }
            } else
                html.setFile("data/html/chamberlain/not-dawn-or-event.htm");

            html.replace("%objectId%", getObjectId());
            player.sendPacket(html);
        } else if (actualCommand.equals("validate_certificate")) {
            if (!validatePrivileges(player, Clan.CP_CS_USE_FUNCTIONS))
                return;

            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

            // Player is registered as dusk, or we aren't in the good side of competition.
            if (SevenSignsManager.getInstance().isSealValidationPeriod()) {
                if (SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId()) == CabalType.DUSK)
                    html.setFile("data/html/chamberlain/not-dawn-or-event.htm");
                    // We already reached the tickets limit.
                else if (getCastle().getLeftCertificates() == 0)
                    html.setFile("data/html/chamberlain/not-enough-ticket.htm");
                else if (player.reduceAdena("Certificate", CERTIFICATES_BUNDLE * CERTIFICATES_PRICE, this, true)) {
                    // We add certificates.
                    player.addItem("Certificate", 6388, CERTIFICATES_BUNDLE, this, true);

                    // We update that castle certificates count.
                    getCastle().setLeftCertificates(getCastle().getLeftCertificates() - 10, true);

                    html.setFile("data/html/chamberlain/sell-dawn-ticket.htm");
                    html.replace("%left%", getCastle().getLeftCertificates());
                    html.replace("%bundle%", CERTIFICATES_BUNDLE);
                    html.replace("%price%", CERTIFICATES_PRICE);
                } else
                    html.setFile("data/html/chamberlain/not-enough-adena.htm");
            } else
                html.setFile("data/html/chamberlain/not-dawn-or-event.htm");

            html.replace("%objectId%", getObjectId());
            player.sendPacket(html);
        } else if (actualCommand.equalsIgnoreCase("castle_devices")) {
            if (!validatePrivileges(player, Clan.CP_CS_SET_FUNCTIONS))
                return;

            sendFileMessage(player, "data/html/chamberlain/devices.htm");
        } else if (actualCommand.equalsIgnoreCase("doors_update")) {
            if (!validatePrivileges(player, Clan.CP_CS_SET_FUNCTIONS))
                return;

            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            if (val.isEmpty())
                html.setFile("data/html/chamberlain/" + getNpcId() + "-gu.htm");
            else {
                html.setFile("data/html/chamberlain/doors-update.htm");
                html.replace("%id%", val);
                html.replace("%type%", st.nextToken());
            }
            html.replace("%objectId%", getObjectId());
            player.sendPacket(html);
        } else if (actualCommand.equalsIgnoreCase("doors_choose_upgrade")) {
            if (!validatePrivileges(player, Clan.CP_CS_SET_FUNCTIONS))
                return;

            final String id = val;
            final String type = st.nextToken();
            final String level = st.nextToken();

            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile("data/html/chamberlain/doors-confirm.htm");
            html.replace("%objectId%", getObjectId());
            html.replace("%id%", id);
            html.replace("%level%", level);
            html.replace("%type%", type);
            html.replace("%price%", getDoorCost(Integer.parseInt(type), Integer.parseInt(level)));
            player.sendPacket(html);
        } else if (actualCommand.equalsIgnoreCase("doors_confirm_upgrade")) {
            if (!validatePrivileges(player, Clan.CP_CS_SET_FUNCTIONS))
                return;

            final int type = Integer.parseInt(st.nextToken());
            final int level = Integer.parseInt(st.nextToken());
            final int price = getDoorCost(type, level);

            if (price == 0)
                return;

            final int id = Integer.parseInt(val);
            final Door door = getCastle().getDoor(id);
            if (door == null)
                return;

            final int currentHpRatio = door.getStatus().getUpgradeHpRatio();

            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

            if (currentHpRatio >= level) {
                html.setFile("data/html/chamberlain/doors-already-updated.htm");
                html.replace("%level%", currentHpRatio * 100);
            } else if (!player.reduceAdena("doors_upgrade", price, player, true))
                html.setFile("data/html/chamberlain/not-enough-adena.htm");
            else {
                getCastle().upgradeDoor(id, level, true);

                html.setFile("data/html/chamberlain/doors-success.htm");
            }
            html.replace("%objectId%", getObjectId());
            player.sendPacket(html);
        } else if (actualCommand.equalsIgnoreCase("traps_update")) {
            if (!validatePrivileges(player, Clan.CP_CS_SET_FUNCTIONS))
                return;

            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            if (val.isEmpty())
                html.setFile("data/html/chamberlain/" + getNpcId() + "-tu.htm");
            else {
                html.setFile("data/html/chamberlain/traps-update" + ((getCastle().getName().equalsIgnoreCase("aden")) ? "1" : "") + ".htm");
                html.replace("%trapIndex%", val);
            }
            html.replace("%objectId%", getObjectId());
            player.sendPacket(html);
        } else if (actualCommand.equalsIgnoreCase("traps_choose_upgrade")) {
            if (!validatePrivileges(player, Clan.CP_CS_SET_FUNCTIONS))
                return;

            final String trapIndex = val;
            final String level = st.nextToken();

            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile("data/html/chamberlain/traps-confirm.htm");
            html.replace("%objectId%", getObjectId());
            html.replace("%trapIndex%", trapIndex);
            html.replace("%level%", level);
            html.replace("%price%", getTrapCost(Integer.parseInt(level)));
            player.sendPacket(html);
        } else if (actualCommand.equalsIgnoreCase("traps_confirm_upgrade")) {
            if (!validatePrivileges(player, Clan.CP_CS_SET_FUNCTIONS))
                return;

            final int level = Integer.parseInt(st.nextToken());
            final int price = getTrapCost(level);

            if (price == 0)
                return;

            final int trapIndex = Integer.parseInt(val);
            final int currentLevel = getCastle().getTrapUpgradeLevel(trapIndex);

            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

            if (currentLevel >= level) {
                html.setFile("data/html/chamberlain/traps-already-updated.htm");
                html.replace("%level%", currentLevel);
            } else if (!player.reduceAdena("traps_upgrade", price, player, true))
                html.setFile("data/html/chamberlain/not-enough-adena.htm");
            else {
                getCastle().setTrapUpgrade(trapIndex, level, true);

                html.setFile("data/html/chamberlain/traps-success.htm");
            }
            html.replace("%objectId%", getObjectId());
            player.sendPacket(html);
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player) {
        player.sendPacket(ActionFailed.STATIC_PACKET);

        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

        final NpcTalkCond condition = getNpcTalkCond(player);
        if (condition == NpcTalkCond.NONE)
            html.setFile("data/html/chamberlain/no.htm");
        else if (condition == NpcTalkCond.UNDER_SIEGE)
            html.setFile("data/html/chamberlain/busy.htm");
        else
            html.setFile("data/html/chamberlain/chamberlain.htm");

        html.replace("%objectId%", getObjectId());
        player.sendPacket(html);
    }

    @Override
    protected NpcTalkCond getNpcTalkCond(Player player) {
        if (getCastle() != null && player.getClan() != null) {
            if (getCastle().getSiege().isInProgress())
                return NpcTalkCond.UNDER_SIEGE;

            if (getCastle().getOwnerId() == player.getClanId()) {
                if (player.isClanLeader())
                    return NpcTalkCond.OWNER;

                return NpcTalkCond.CLAN_MEMBER;
            }
        }
        return NpcTalkCond.NONE;
    }

    private boolean validatePrivileges(Player player, int privilege) {
        if (!player.hasClanPrivileges(privilege)) {
            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            html.setFile("data/html/chamberlain/noprivs.htm");
            player.sendPacket(html);
            return false;
        }
        return true;
    }

    private void sendFileMessage(Player player, String htmlMessage) {
        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(htmlMessage);
        html.replace("%objectId%", getObjectId());
        html.replace("%npcId%", getNpcId());
        html.replace("%time%", getCastle().getSiegeDate().getTime().toString());
        player.sendPacket(html);
    }

    /**
     * Retrieve the price of the door, following its type, required level of upgrade and current Seven Signs state.
     *
     * @param type  : The type of doors (1: normal gates, 2: metallic gates, 3: walls).
     * @param level : The required level of upgrade (x2, x3 or x5 HPs).
     * @return The price modified by Seal of Strife state (-20% if Dawn is winning, x3 if Dusk is winning).
     */
    private static int getDoorCost(int type, int level) {
        int price = 0;

        switch (type) {
            case 1:
                switch (level) {
                    case 2:
                        price = 300000;
                        break;
                    case 3:
                        price = 400000;
                        break;
                    case 5:
                        price = 500000;
                        break;
                }
                break;

            case 2:
                switch (level) {
                    case 2:
                        price = 750000;
                        break;
                    case 3:
                        price = 900000;
                        break;
                    case 5:
                        price = 1000000;
                        break;
                }
                break;

            case 3:
                switch (level) {
                    case 2:
                        price = 1600000;
                        break;
                    case 3:
                        price = 1800000;
                        break;
                    case 5:
                        price = 2000000;
                        break;
                }
                break;
        }

        switch (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE)) {
            case DUSK:
                price *= 3;
                break;

            case DAWN:
                price *= 0.8;
                break;
        }

        return price;
    }

    /**
     * Retrieve the price of traps, following its level.
     *
     * @param level : The required level of upgrade.
     * @return The price modified by Seal of Strife state (-20% if Dawn is winning, x3 if Dusk is winning).
     */
    private static int getTrapCost(int level) {
        int price = 0;

        switch (level) {
            case 1:
                price = 3000000;
                break;

            case 2:
                price = 4000000;
                break;

            case 3:
                price = 5000000;
                break;

            case 4:
                price = 6000000;
                break;
        }

        switch (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE)) {
            case DUSK:
                price *= 3;
                break;

            case DAWN:
                price *= 0.8;
                break;
        }

        return price;
    }
}