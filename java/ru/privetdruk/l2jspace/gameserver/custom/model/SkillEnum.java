package ru.privetdruk.l2jspace.gameserver.custom.model;

public enum SkillEnum {
    CLASS;

    public enum Admin {
        SUPER_HASTE(7029, 4);

        private final int id;
        private final int maxLevel;

        Admin(int id, int maxLevel) {
            this.id = id;
            this.maxLevel = maxLevel;
        }

        public int getId() {
            return id;
        }

        public int getMaxLevel() {
            return maxLevel;
        }
    }

    public enum Prophet {
        MAGIC_BARRIER(1036);

        private final int id;

        Prophet(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public enum Warlock {
        ICON_BLESSING_OF_QUEEN(1331),
        BLESSING_OF_QUEEN(4699);

        private final int id;

        Warlock(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public enum ElementalSummoner {
        ICON_GIFT_OF_SERAPHIM(1332),
        GIFT_OF_SERAPHIM(4703);

        private final int id;

        ElementalSummoner(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }


    public enum GeneralBuff {
        MAGIC_BARRIER(1036);

        private final int id;

        GeneralBuff(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public enum Bishop {
        GREATER_BATTLE_HEAL(1218),
        REPOSE(1034);

        private final int id;

        Bishop(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public enum Spellsinger {
        CANCELLATION(1056);

        private final int id;

        Spellsinger(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public enum HotSprings {
        ICON(4037),
        FLU(4553),
        MALARIA(4554);

        private final int id;

        HotSprings(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public enum Mount {
        TYPE;

        public enum Wyvern {
            WYVERN_BREATH(4289);

            private final int id;

            Wyvern(int id) {
                this.id = id;
            }

            public int getId() {
                return id;
            }
        }
    }
}
