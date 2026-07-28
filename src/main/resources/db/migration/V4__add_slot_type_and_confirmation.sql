ALTER TABLE registration_slots ADD COLUMN slot_type VARCHAR(20) DEFAULT 'REGULAR';
ALTER TABLE students ADD COLUMN is_registration_confirmed BOOLEAN DEFAULT FALSE;
