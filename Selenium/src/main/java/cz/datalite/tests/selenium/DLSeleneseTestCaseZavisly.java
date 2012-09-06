//package cz.datalite.tests.selenium;
//
//import com.thoughtworks.selenium.Wait;
//import org.junit.Test;
//
///**
// * Třída určená pro závislé selenium testy ZK aplikací.<br />
// * Jedná se o testy, které se naklikají přes browser a poté se v javě duplikují
// * všechny události jakoby je dělal znovu uživatel.
// * Závislé testy jsou na sobě nějakým způsobem závislé (např. wizard).
// *
// * @author Michal Pavlusek
// * @version 1.5
// * @since 1.0.1 (selenium testu)
// * @since 4.0 (JUnit testu)
// * @see <a href="http://wiki.praha.datalite.cz/twiki/bin/view/Intranet/SeleniumTesty">
// * Selenium testy na wiki praha datalite</a>
// */
//public abstract class DLSeleneseTestCaseZavisly
//{
//    public DLSelenium test;         // zde si vedu objekt, který přes browser testuje aplikaci
//
//    public void init(DLSelenium test)
//    {
//        this.test = test;
//    }
//
//    /**
//     * Z důvodu, že závislé testy jsou nespustitelné, tak je zde výchozí prázdný
//     * test pro tyto testy, aby nevyhazovali ERROR (No runnable methods).
//     */
//    @Test
//    public void zavisly()
//    {
//
//    }
//
//    /**
//     * Funkce čeká na zadaný element, až bude vykreslen do stránky.<br />
//     * Pokud se funkce nedočká, vypíše po vypršení timeout limitu zprávu.<br />
//     * <i>Defaultní timeout je v systému nastaven na 30 sekund.</i>
//     * @param waitingElement element, na který se čeká
//     * @param timeoutMessage zpráva na vypsání po vypršení timeoutu
//     */
//    public void pockejNaElement(final String waitingElement, String timeoutMessage)
//    {
//        new Wait()
//        {
//            public boolean until()
//            {
//                return test.isElementPresent(waitingElement);
//            }
//        }.wait(timeoutMessage);
//    }
//
//}
