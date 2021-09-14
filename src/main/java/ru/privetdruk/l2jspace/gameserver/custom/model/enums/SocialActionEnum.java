package ru.privetdruk.l2jspace.gameserver.custom.model.enums;

public enum SocialActionEnum {
    VICTORY(3),
    BOW(7),
    AMAZING_GLOW(16);

    private final int id;

    SocialActionEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public enum Npc {
        GLADNESS(1);

        private final int id;

        Npc(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
