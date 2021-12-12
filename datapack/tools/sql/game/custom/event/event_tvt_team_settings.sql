DROP TABLE IF EXISTS event_tvt_team_setting;

CREATE TABLE IF NOT EXISTS event_tvt_team_setting
(
    event_id                 SMALLINT     NOT NULL COMMENT 'Event ID',
    id                       INT(4) NOT NULL DEFAULT 0 COMMENT 'Team ID',
    name                     VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'Team name',
    name_color               VARCHAR(6) NOT NULL DEFAULT 0 COMMENT 'Name color (hex)',
    offset                   INT(11) NOT NULL DEFAULT 300 COMMENT 'Displacement of the player after teleporting to the event.',
    position_x               INT(11) NOT NULL DEFAULT 0 COMMENT 'X-axis team spawn coordinates',
    position_y               INT(11) NOT NULL DEFAULT 0 COMMENT 'Y-axis team spawn coordinates',
    position_z               INT(11) NOT NULL DEFAULT 0 COMMENT 'Z-axis team spawn coordinates',
    primary key (event_id, id)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;

ALTER TABLE event_tvt_team_setting COMMENT = 'TvT Настройки команд';

INSERT INTO event_tvt_team_setting (event_id, id, name, name_color,
                                    offset, position_x, position_y, position_z)
VALUES (4, 1, 'Blue', 'FF0000', 300, 87357, -145722, -1288),
       (4, 2, 'Red', '0000FF', 300, 87351, -139984, -1536);