package com.management.util.constant;


public final class SqlConstant {

    public static final String RESET_EXPIRED_OTP_ATTEMPT =
            "UPDATE OTP_LIMIT olu SET "
                    + "olu.UPDATED_DATE = SYSDATE, "
                    + "olu.PER_ROUND_ATTEMPT = 0, "
                    + "olu.LOCKED_TO = NULL "
                    + "WHERE olu.ID IN (SELECT ol.ID FROM OTP_LIMIT ol "
                    + "JOIN OTP_LIMIT_CONFIG olc ON ol.CONFIG_ID = olc.ID "
                    + "WHERE ol.LOCKED_TO IS NOT NULL "
                    + "AND olc.service =:service "
                    + "AND TO_CHAR(ol.LOCKED_TO, 'YYYY-MM-DD HH24:MI:SS')<=TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'))";

    public static final String RESET_WRONG_REGISTER_ATTEMPTS_LOCK =
            "UPDATE REGISTER_LIMIT rlu"
                    + " SET rlu.UPDATED_DATE  = SYSDATE,"
                    + "    rlu.ATTEMPT_COUNT = 0,"
                    + "    rlu.FIRST_ATTEMPT = NULL,"
                    + "    rlu.LAST_ATTEMPT  = NULL"
                    + " WHERE rlu.ID IN ("
                    + "    SELECT DISTINCT rl.ID "
                    + "    FROM REGISTER_LIMIT rl "
                    + "    JOIN REGISTER_LIMIT_CONFIG rlc ON rl.CONFIG_ID = rlc.ID"
                    + "    WHERE rl.LAST_ATTEMPT IS NOT NULL"
                    + "      AND rl.FIRST_ATTEMPT IS NOT NULL"
                    + "      AND rlc.ATTEMPT_COUNT <= rl.ATTEMPT_COUNT"
                    + "      AND rlc.service =:service"
                    + "      AND TO_CHAR(rl.LAST_ATTEMPT + rlc.LOCK_TIME / (24 * 60), 'YYYY-MM-DD HH24:MI:SS') <="
                    + "          TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'))";


    public static final String RESET_WRONG_REGISTER_ATTEMPT =
            "UPDATE REGISTER_LIMIT rlu"
                    + " SET rlu.UPDATED_DATE  = SYSDATE,"
                    + "    rlu.ATTEMPT_COUNT = 0,"
                    + "    rlu.FIRST_ATTEMPT = NULL,"
                    + "    rlu.LAST_ATTEMPT  = NULL"
                    + " WHERE rlu.ID IN ("
                    + "    SELECT DISTINCT rl.ID "
                    + "    FROM REGISTER_LIMIT rl "
                    + "    JOIN REGISTER_LIMIT_CONFIG rlc ON rl.CONFIG_ID = rlc.ID"
                    + "    WHERE rl.LAST_ATTEMPT IS NOT NULL"
                    + "      AND rl.FIRST_ATTEMPT IS NOT NULL"
                    + "      AND rl.ATTEMPT_COUNT < rlc.ATTEMPT_COUNT"
                    + "      AND rlc.service =:service"
                    + "      AND TO_CHAR(rl.FIRST_ATTEMPT + 1440 / (24 * 60), 'YYYY-MM-DD HH24:MI:SS') <="
                    + "          TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'))";


    public static final String RESET_LOCKED_AUTHORIZATION_ATTEMPT =
            "UPDATE AUTHORIZATION_LIMIT alu"
                    + " SET alu.UPDATED_DATE  = SYSDATE,"
                    + "    alu.ATTEMPT_COUNT = 0,"
                    + "    alu.FIRST_ATTEMPT = NULL,"
                    + "    alu.LAST_ATTEMPT  = NULL"
                    + " WHERE alu.ID IN ("
                    + "    SELECT DISTINCT al.ID "
                    + "    FROM AUTHORIZATION_LIMIT al "
                    + "    JOIN AUTHORIZATION_LIMIT_CONFIG alc ON al.CONFIG_ID = alc.ID"
                    + "    WHERE al.LAST_ATTEMPT IS NOT NULL"
                    + "      AND al.FIRST_ATTEMPT IS NOT NULL"
                    + "      AND alc.ATTEMPT_COUNT <= al.ATTEMPT_COUNT"
                    + "      AND alc.service =:service"
                    + "      AND TO_CHAR(al.LAST_ATTEMPT + alc.LOCK_TIME / (24 * 60), 'YYYY-MM-DD HH24:MI:SS') <="
                    + "          TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'))";


    public static final String RESET_WRONG_AUTHORIZATION_ATTEMPT =
            "UPDATE AUTHORIZATION_LIMIT alu"
                    + " SET alu.UPDATED_DATE  = SYSDATE,"
                    + "    alu.ATTEMPT_COUNT = 0,"
                    + "    alu.FIRST_ATTEMPT = NULL,"
                    + "    alu.LAST_ATTEMPT  = NULL"
                    + " WHERE alu.ID IN ("
                    + "    SELECT DISTINCT al.ID "
                    + "    FROM AUTHORIZATION_LIMIT al "
                    + "    JOIN AUTHORIZATION_LIMIT_CONFIG alc ON al.CONFIG_ID = alc.ID"
                    + "    WHERE al.LAST_ATTEMPT IS NOT NULL"
                    + "      AND al.FIRST_ATTEMPT IS NOT NULL"
                    + "      AND al.ATTEMPT_COUNT < alc.ATTEMPT_COUNT"
                    + "      AND alc.service =:service"
                    + "      AND TO_CHAR(al.FIRST_ATTEMPT + 1440 / (24 * 60), 'YYYY-MM-DD HH24:MI:SS') <="
                    + "          TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'))";

    private SqlConstant() {

    }

}
