-- Created by privetdruk for L2jSpace
DROP TABLE IF EXISTS event;

CREATE TABLE event
(
    id                    SMALLINT     NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'Event ID',
    type                  VARCHAR(32)  NOT NULL DEFAULT '' COMMENT 'Event type (see enum EventType)',
    loading_order         INT(2)       NOT NULL DEFAULT 0 COMMENT 'Loading order',
    name                  VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'Event name',
    description           VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'Event description',
    registration_location VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'Registration locations name',
    min_level             INT(4)       NOT NULL DEFAULT 0 COMMENT 'Minimum level for registration',
    max_level             INT(4)       NOT NULL DEFAULT 0 COMMENT 'Maximum level for registration',
    npc_id                INT(8)       NOT NULL DEFAULT 0 COMMENT 'Npc ID',
    npc_x                 INT(11)      NOT NULL DEFAULT 0 COMMENT 'Npc spawn position X',
    npc_y                 INT(11)      NOT NULL DEFAULT 0 COMMENT 'Npc spawn position Y',
    npc_z                 INT(11)      NOT NULL DEFAULT 0 COMMENT 'Npc spawn position Z',
    npc_heading           INT(11)      NOT NULL DEFAULT 0 COMMENT 'Npc heading',
    teams_count           INT(4)       NOT NULL DEFAULT 0 COMMENT 'Teams count',
    time_registration     INT(11)      NOT NULL DEFAULT 0 COMMENT 'Time for registration',
    duration_event        INT(11)      NOT NULL DEFAULT 0 COMMENT 'Duration of the event',
    min_players           INT(4)       NOT NULL DEFAULT 0 COMMENT 'Min players for registration',
    max_players           INT(4)       NOT NULL DEFAULT 0 COMMENT 'Max players for registration',
    delay_next_event      BIGINT       NOT NULL DEFAULT 0 COMMENT 'Delay for next event'
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;

ALTER TABLE event
    COMMENT = 'Events general settings';

INSERT INTO event (id, type, loading_order, name, description, registration_location, min_level, max_level,
                   npc_id, npc_x, npc_y, npc_z, npc_heading,
                   teams_count, time_registration, duration_event, min_players, max_players, delay_next_event)
VALUES (1, 'CTF', 1, 'CTF 1', 'Battle for Shutgartt', 'Giran', 1, 80, 50009, 82580, 148552, -3468, 16972,
        2, 5, 5, 2, 50, 300000),
       (2, 'CTF', 2, 'CTF 2', 'Battle for Giran', 'Giran', 1, 80, 50009, 82580, 148552, -3468, 16972,
        2, 5, 5, 2, 50, 300000),
       (3, 'LAST_EMPEROR', 1, 'Last Emperor', 'Последний император', 'Giran', 1, 80, 50009, 82580, 148552, -3468, 16972,
        1, 5, 5, 2, 50, 300000);