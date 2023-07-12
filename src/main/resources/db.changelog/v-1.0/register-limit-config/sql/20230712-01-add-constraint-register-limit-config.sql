ALTER TABLE REGISTER_LIMIT_CONFIG
    ADD CONSTRAINT CHECK_CONSTRAINT_REGISTER_LIMIT_CONFIG
        CHECK (STATE >= 0 AND SERVICE > 0 AND ATTEMPT_COUNT > 0 AND LOCK_TIME > 0);