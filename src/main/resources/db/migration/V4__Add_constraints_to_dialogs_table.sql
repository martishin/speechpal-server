ALTER TABLE speechpal.dialogs
    ADD FOREIGN KEY (user_id) REFERENCES speechpal.users (id) ON DELETE CASCADE;
