ALTER TABLE credentials
DROP CONSTRAINT IF EXISTS chk_credentials_type;

ALTER TABLE credentials
    ADD CONSTRAINT chk_credentials_type
        CHECK (type IN ('HVAC_LICENSE', 'EPA_608', 'INSURANCE', 'STATE_LICENSE'));
