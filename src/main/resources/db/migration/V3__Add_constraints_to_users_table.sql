ALTER TABLE speechpal.users
    ADD FOREIGN KEY (current_dialog_id) REFERENCES speechpal.dialogs (id);
