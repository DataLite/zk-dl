package cz.datalite.dao.plsql.impl;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static cz.datalite.dao.plsql.impl.OraError.COULD_NOT_FIND_PROGRAM_UNIT;
import static cz.datalite.dao.plsql.impl.OraError.NOT_EXECUTED_ALTERD_DROPPED;
import static org.junit.Assert.*;

public class OraErrorTest {

    @Test
    public void testIs() {
        assertTrue(COULD_NOT_FIND_PROGRAM_UNIT.is(6508));
        assertFalse(COULD_NOT_FIND_PROGRAM_UNIT.is(-222));
    }

    @Test
    public void testUnique() {
        assertEquals(
                "Nektery kod je v enumu vicekrat.",
                OraError.values().length,
                Arrays.stream(OraError.values()).map(OraError::getErrorCode).distinct().count());
    }

    @Test
    public void testToString() {
        assertNotNull("kontrola zda nepadne",
                COULD_NOT_FIND_PROGRAM_UNIT.toString());
    }


    @Test
    public void testAny() {
        assertTrue(OraError.any(Collections.singleton(COULD_NOT_FIND_PROGRAM_UNIT), 6508));
        assertTrue(OraError.any(Arrays.asList(COULD_NOT_FIND_PROGRAM_UNIT, NOT_EXECUTED_ALTERD_DROPPED), 6508));

        assertFalse(OraError.any(Arrays.asList(COULD_NOT_FIND_PROGRAM_UNIT, NOT_EXECUTED_ALTERD_DROPPED), 22));
    }
}