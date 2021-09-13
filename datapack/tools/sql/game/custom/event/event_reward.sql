CREATE TABLE event_reward
(
    id            BIGINT auto_increment primary KEY COMMENT 'Идентификатор записи',
    event_id      SMALLINT NOT NULL COMMENT 'Идентификатор ивента',
    reward_id     BIGINT   NOT NULL COMMENT 'Идентификатор награды',
    reward_amount INT      NOT NULL COMMENT 'Кол-во награды',
    CONSTRAINT `fk_event_reward_event_id`
        FOREIGN KEY (event_id) REFERENCES event (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE event_reward
    COMMENT = 'Список наград за ивент';

INSERT INTO event_reward (event_id, reward_id, reward_amount)
VALUES (1, 8752, 1),
       (2, 8752, 1);

INSERT INTO event_reward (event_id, reward_id, reward_amount)
VALUES (3, 6662, 1), -- core
       (3, 6661, 1), -- orfen
       (3, 6660, 1), -- aq
       (3, 6659, 1), -- zaken
       (3, 6658, 1), -- baium
       (3, 6656, 1), -- antharas
       (3, 6657, 1); -- valakas