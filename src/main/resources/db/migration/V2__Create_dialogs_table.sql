CREATE TABLE speechpal.dialogs
(
    id         SERIAL PRIMARY KEY,
    messages   JSONB,
    model      VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT current_timestamp,
    updated_at TIMESTAMP DEFAULT current_timestamp,
    user_id    INT
);
