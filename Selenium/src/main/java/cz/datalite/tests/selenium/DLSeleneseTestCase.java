//package cz.datalite.tests.selenium;
//
//import com.thoughtworks.selenium.Wait;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.zkoss.zk.ui.UiException;
//
///**
// * Třída určená pro selenium testy ZK aplikací.<br />
// * Jedná se o testy, které se naklikají přes browser a poté se v javě duplikují
// * všechny události jakoby je dělal znovu uživatel.
// *
// * @author Michal Pavlusek
// * @version 2.0
// * @since 1.0.1 (selenium testu)
// * @since 4.0 (JUnit testu)
// * @see <a href="http://wiki.praha.datalite.cz/twiki/bin/view/Intranet/SeleniumTesty">
// * Selenium testy na wiki praha datalite</a>
// */
//public abstract class DLSeleneseTestCase
//{
//    public static DLSelenium test;         // zde si vedu objekt, který přes browser testuje aplikaci
//    private static Properties lokalniPromenne = new Properties();   // proměnná, ve které mám uložené lokalní nastavení
//    private static Properties globalniPromenne  = new Properties();   // proměnná, ve které mám uložen globální nastavení
//
//    private static String getProperty(String property)
//    {
//        return lokalniPromenne.getProperty(property) != null ?
//            lokalniPromenne.getProperty(property) : globalniPromenne.getProperty(property);
//    }
//
//    /**
//     * Nastavení na počátku života třídy
//     * @throws Exception
//     */
//    @BeforeClass
//    public static void setUpClass() throws Exception
//    {
//        try
//        {
//            InputStream global = Thread.currentThread().getContextClassLoader().getResourceAsStream("seleniumGlobal.properties");
//            globalniPromenne.load(global);
//        }
//        catch(IOException ioe)
//        {
//            throw new UiException("Nepovedlo se načíst obsah properties souboru pro Selenium testy!");
//        }
//        catch(NullPointerException npe)
//        {
//            throw new UiException("V projektu neexistuje properties soubor pro Selenium testy!");
//        }
//
//        // lokalni promenne nejsou povinne
//        InputStream local = Thread.currentThread().getContextClassLoader().getResourceAsStream("seleniumLocal.properties");
//        if (local != null)
//            lokalniPromenne.load(local);
//
//        test = new DLSelenium(getProperty("adresaSeleniumServeru"), new Integer(getProperty("portSeleniumServeru")), getProperty("prohlizec"), getProperty("adresaNaSpusteni"));
//        test.setAdresaAplikace(getProperty("adresaAplikace"));
//        test.start();
//
//        try
//        {
//            test.prihlasit(getProperty("adresaAplikace"), getProperty("prihlasovaciJmenoIdInput"), getProperty("prihlasovaciJmeno"), getProperty("prihlasovaciHesloIdInput"), getProperty("prihlasovaciHeslo"), getProperty("prihlaseniId"), getProperty("prihlasitCekat"));
//        }
//        catch (Throwable e)
//        {
//            // pokud se nepovede ani prihlasit, tak zavru browser a koncim s chybou
//            tearDownClass();
//            throw new Error("Do aplikace se nepodarilo prihlasit.", e);
//        }
//    }
//
//    /**
//     * Události na konci života třídy
//     * @throws Exception
//     */
//    @AfterClass
//    public static void tearDownClass() throws Exception
//    {
//        try
//        {
//            test.odhlasit(getProperty("odhlaseniId"));
//        }
//        catch (Throwable e)
//        {
//            // pokud se nepovede odhlasit, tak nevadi, stejne zaviram browser
//        }
//
//        test.stop();
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
