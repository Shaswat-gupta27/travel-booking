ALTER TABLE passengers
    ADD COLUMN user_id VARCHAR(255);

-- A passport number can be saved once per owner (older passengers without an owner are left out).
CREATE UNIQUE INDEX uq_passengers_user_id_passport_number
    ON passengers (user_id, passport_number)
    WHERE is_deleted = FALSE AND user_id IS NOT NULL;
