package cz.datalite.dao.plsql.impl;

import java.util.Arrays;
import java.util.Collection;

/**
 * Seznam ORA chyb
 */
public enum OraError {
    /**
     * existing state of packages string has been discarded
     */
    PACKAGE_STATE_DISCARTED(4068),

    /**
     * Existing state of string has been invalidated
     */
    STRING_STATE_INVALIDATED(4061),
    /**
     * not executed, altered or dropped
     */
    NOT_EXECUTED_ALTERD_DROPPED(4065),

    /**
     * could not find program unit being called
     */
    COULD_NOT_FIND_PROGRAM_UNIT(6508),

    ;

    /**
     * Kod ORA- chyby
     */
    final private int errorCode;

    OraError(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Zda je zadana chyba tou onou
     * @param errorCode vstup k porovnani
     * @return {@code true} pokud jsou stejne
     */
    public boolean is(int errorCode) {
        return this.errorCode == errorCode;
    }

    /**
     * Seznam chyb, ktere mohou byt pouze docasneho charakteru a pri opakovani akce se akce jiz muze povest
     * @return seznam chyb
     */
    public static Collection<OraError> getRetriable() {
        return Arrays.asList(PACKAGE_STATE_DISCARTED, STRING_STATE_INVALIDATED, NOT_EXECUTED_ALTERD_DROPPED, COULD_NOT_FIND_PROGRAM_UNIT);
    }

    /**
     * Zda je v seznamu chyb nejaka, ktera odpovida chybovemu kodu
     * @param errors seznam chyb mezi kterymi je hledano
     * @param errorCode cislo chyby
     * @return {@code true}, pokud je mezi nimi ona hledana
     */
    public static boolean any(Collection<OraError> errors, int errorCode) {
        return errors.stream().anyMatch(e -> e.is(errorCode));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OraError{");
        sb.append(name());
        sb.append(", errorCode=").append(errorCode);
        sb.append('}');
        return sb.toString();
    }
}
