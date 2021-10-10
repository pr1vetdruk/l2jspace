DROP TABLE IF EXISTS event_winner;

CREATE TABLE IF NOT EXISTS event_winner
(
    id           BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'Идентификатор записи',
    player_id    INT UNSIGNED NOT NULL COMMENT 'Идентификатор игрока',
    event_type   VARCHAR(32)  NOT NULL COMMENT 'Тип ивента (enum EventType)',
    victory_date DATETIME     NOT NULL COMMENT 'Дата победы',
    status       VARCHAR(32)  NOT NULL COMMENT 'Статус (enum EventWinnerStatus)',
    CONSTRAINT event_winner_player_id_fk FOREIGN KEY (player_id) REFERENCES characters (obj_id)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;

ALTER TABLE event_winner
    COMMENT = 'Победители ивентов';