package cz.datalite.dao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Definice sestavení textového popisu entity pro audit. 
 * Využivá se pokud se audituje sloupec a tato entita je z něj odkazovaná (typicky nějaký číselník).</p>
 * 
 * <p>
 * Pokud není definovaná bere se jako default pole označené jako @MainColumn. Pokud není žádné takové pole, berou se všechny sloupce.
 * </p>
 * <p>Typicky se zadává vlastnost columns(), která definuje seznam sloupců ze kterých se má text složit (oddělené mezerou). Pro složitější 
 * případy lze napsat komplexní sql. Pokud jsou zadané obě vlastnosti, přednost má sql.
 * </p>
 *
 * @author Jiri Bubnik <bubnik at datalite.cz>
 */
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface DBAuditTarget {

    /**
     * Seznam sloupců pro složení textového popisu. Pro audit budou mezerou oddělené.
     */
    public String[] columns() default "";

    /**
     * <p>Pro komplexní případy lze zadat přímo SQL, které se v auditovacím triggeru vykoná a jeho výsledek se zapíše jako popis.</p>
     *
     * <p>Potřeba je to např. pro vazební tabulky (firma -> firma_adresa -> adresa). Při auditování adresy ve firmě je target fima_adresa,
     * ta však neobsahuje žádný uživatelsky srozumitelný popis. Proto je nutné napsat dotaz, který se bude ptát až adresy.</p>
     *
     * <p>Dotaz musí obsahovat pole :VALUE a :ID. :ID bude aktuální hodnota pro kterou se vykonává, :VALUE je holder, do kterého se má vložit hodnota:<br/>
     * DBAuditTarget(sql="select to_upper(NAZEV_ODDELENI) into :VALUE from ODDELENI where ID_ODDELENI = :ID")
     */
    public String sql() default "";
}
