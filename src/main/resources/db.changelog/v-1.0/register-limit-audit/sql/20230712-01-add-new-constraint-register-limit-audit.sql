ALTER TABLE REGISTER_LIMIT_AUD
    ADD CONSTRAINT CHECK_CONSTRAINT_REGISTER_LIMIT_AUD
        CHECK (STATE >= 0 AND LENGTH(PIN) >= 5 AND LENGTH(PIN) <= 7 AND LENGTH(PHONE) = 12
            AND LENGTH(CARD) > 15 AND LENGTH(CARD) < 23);