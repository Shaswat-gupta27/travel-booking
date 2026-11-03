ALTER TABLE airports
  ADD COLUMN city    VARCHAR(255) NOT NULL DEFAULT '',
  ADD COLUMN region  VARCHAR(255) NOT NULL DEFAULT '',
  ADD COLUMN country VARCHAR(255) NOT NULL DEFAULT '';

UPDATE airports SET city = 'Lisbon',    region = 'Lisbon',    country = 'Portugal' WHERE code = 'LIS';
UPDATE airports SET city = 'Sao Paulo', region = 'Sao Paulo', country = 'Brazil'   WHERE code = 'BRZ';
