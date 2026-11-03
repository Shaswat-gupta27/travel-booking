ALTER TABLE bookings
    ADD COLUMN user_id        VARCHAR(255),
    ADD COLUMN status         VARCHAR(255) NOT NULL DEFAULT 'Confirmed',
    ADD COLUMN flight_id      UUID,
    ADD COLUMN departure_date TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN arrive_date    TIMESTAMP WITHOUT TIME ZONE;
