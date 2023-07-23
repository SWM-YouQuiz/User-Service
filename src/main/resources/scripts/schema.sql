DROP TABLE IF EXISTS user;

CREATE TABLE user
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    username     VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    nickname     VARCHAR(255) NOT NULL,
    role         VARCHAR(255) NOT NULL,
    created_date TIMESTAMP DEFAULT now(),
    allow_push   TINYINT      NOT NULL,
    PRIMARY KEY (id)
);