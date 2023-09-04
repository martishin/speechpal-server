CREATE SCHEMA IF NOT EXISTS speechpal;

CREATE TABLE speechpal.users (
    id                SERIAL PRIMARY KEY,
    telegram_user_id  BIGINT NOT NULL,
    chat_id           BIGINT NOT NULL,
    username          VARCHAR(255) NOT NULL,
    first_name        VARCHAR(255),
    last_name         VARCHAR(255),
    created_at        TIMESTAMP DEFAULT current_timestamp,
    updated_at        TIMESTAMP DEFAULT current_timestamp,
    current_dialog_id INT
);

CREATE UNIQUE INDEX telegram_user_id_unique_idx ON speechpal.users (telegram_user_id);
