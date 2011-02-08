//package cz.datalite.tests.selenium;
//
//import com.thoughtworks.selenium.DefaultSelenium;
//import cz.datalite.helpers.StringHelper;
//import java.util.ArrayList;
//
///**
// * Objekt, přes který java testuje pomocí browseru aplikaci.<br />
// * <i>Selenium jsou testy, které se naklikají přes browser a poté se v javě duplikují
// * všechny události, jakoby je dělal znovu uživatel.</i><br />
// *
// * @author Michal Pavlusek
// * @version 2.0
// * @since 1.0.1 (Selenium testu)
// * @since 4.0 (JUnit testu)
// * @see <a href="http://wiki.praha.datalite.cz/twiki/bin/view/Intranet/SeleniumTesty">
// * Selenium testy na wiki praha datalite</a>
// */
//public class DLSelenium extends DefaultSelenium
//{
//    private String adresaAplikace;  // adresa, na které je dostupná aplikace na serveru
//    public static final int DVE_VTERINY = 2000;
//    public static final int TRI_VTERINY = 3000;
//
//    /**
//     * Výchozí konstruktor.
//     * @param serverHost adresa selenium serveru
//     * @param serverPort port selenim serveru
//     * @param browserStartCommand prohlížeč, ve kterém se má test otevřít
//     * @param browserURL adresa, která se má otevřít
//     */
//    public DLSelenium(String serverHost, int serverPort, String browserStartCommand, String browserURL)
//    {
//        super(serverHost, serverPort, browserStartCommand, browserURL);
//    }
//
//    /**
//     * Nastavuje adresu aplikace na serveru.
//     * @param adresaAplikace na serveru
//     */
//    public void setAdresaAplikace(String adresaAplikace)
//    {
//        this.adresaAplikace = adresaAplikace;
//    }
//
//    /**
//     * Funkce slouží pro 0 úrovňové kliky v ZK komponentě menu.
//     * @param id položka v menu, na níž chceme kliknout
//     */
//    public void menuClick(String id)
//    {
//        focusAndKey(id, java.awt.event.KeyEvent.VK_SPACE);
//    }
//
//    /**
//     * Funkce slouží pro 1 úrovňové kliky v ZK komponentě menu.
//     * @param id položka v menu, na níž vyvoláváme popup
//     * @param index pořadí menuItemu odzhora (první = 1), na který cheme kliknout
//     */
//    public void menuClickOne(String id, Integer index)
//    {
//        focusAndKey(id, java.awt.event.KeyEvent.VK_SPACE);
//
//        for(int i = 0; i < index; i++)
//        {
//            this.zmackniKlavesu(java.awt.event.KeyEvent.VK_DOWN);
//        }
//
//        this.zmackniKlavesu(java.awt.event.KeyEvent.VK_ENTER);
//    }
//
//    /**
//     * Funkce kliká na button ve stránce. <br />
//     * Jedná se totiž o to, že ZK button (mold="default") není ve skutečnosti button,
//     * ale pouze tabulka vypadající jako button.<br /><br />
//     * Pokud klikáte na normální, skutečný button (mold="os"), pak stačí použí
//     * normálně test.click();
//     * @param id buttonu na stisknutí
//     */
//    public void buttonClick(String id)
//    {
//        id = id + "!real";
//        this.click(id);
//    }
//
//    /**
//     * Funkce čeká stanovený čas, před prováděním dalších řádků programu.
//     * @param pauza délka čekání v milisekundách
//     * @deprecated snažte se funkci používát co nejméně, není vhodné časovat
//     * absolutně
//     * @see cz.datalite.tests.selenium.DLSeleneseTestCase#pockejNaElement(java.lang.String, java.lang.String)
//     * @see #vyckejNaDataVTabulce(java.lang.String)
//     */
//    public void pockej(Integer pauza)
//    {
//        try
//        {
//            Thread.sleep(pauza);
//        }
//        catch(InterruptedException ie)
//        {
//            notifyAll();
//        }
//    }
//
//    /**
//     * Funkce potvrdí vyskočenou otázku (modální okno zk question).<br /><br />
//     * <strong>Otázka již musí být vyskočená v době volání této funkce</strong>
//     */
//    public void potvrditOtazku()
//    {
//        this.pockej(DVE_VTERINY);
//        this.zmackniKlavesu(java.awt.event.KeyEvent.VK_SPACE);
//    }
//
//    /**
//     * Funkce zruší vyskočenou otázku (modální okno zk question).<br /><br />
//     * <strong>Otázka již musí být vyskočená v době volání této funkce</strong>
//     */
//    public void zrusitOtazku()
//    {
//        this.pockej(DVE_VTERINY);
//        this.zmackniKlavesu(java.awt.event.KeyEvent.VK_ESCAPE);
//    }
//
//    /**
//     * Funkce stiske klávesu a drží dokud není puštěna.
//     * @param klavesa identifikační číslo klávesy
//     * @see #pustKlavesu(java.lang.Integer)
//     */
//    public void drzKlavesu(Integer klavesa)
//    {
//        this.keyDownNative(klavesa.toString());
//    }
//
//    /**
//     * Funkce pustí drženou klávesu.
//     * @param klavesa identifikační číslo klávesy
//     * @see #zmackniKlavesu(java.lang.Integer)
//     */
//    public void pustKlavesu(Integer klavesa)
//    {
//        this.keyUpNative(klavesa.toString());
//    }
//
//    /**
//     * Funkce zmáčkne klávesu identifikovanou pod číslem "klavesa".<br /><br />
//     * <strong>Funkci můžete poslat buď rovnou číslo klávesy, nebo ho vrací
//     * <i>java.awt.event.KeyEvent.</i>požadovaná klávesa</strong>
//     * @see <a href="http://publib.boulder.ibm.com/infocenter/wsadhelp/v5r1m2/index.jsp?topic=/com.sun.api.doc/java/awt/event/KeyEvent.html">
//     * Přehled kláves</a>
//     * @param klavesa identifikační číslo klávesy
//     */
//    public void zmackniKlavesu(Integer klavesa)
//    {
//        this.keyPressNative(klavesa.toString());
//    }
//
//    /**
//     * Funkce zmáčkne klávesu známou jako "klavesa". <br /><br />
//     * <i>Zatím funkce zná: <u>enter</u>, <u>space</u>, <u>nahoru</u>,
//     * <u>dolu</u>;
//     * @param klavesa název klávesy
//     */
//    public void zmackniKlavesu(String klavesa)
//    {
//        ArrayList seznam = new ArrayList();
//        seznam.add("enter");seznam.add("space");seznam.add("nahoru");seznam.add("dolu");
//
//        switch(seznam.indexOf(klavesa))
//        {
//            case 0: zmackniKlavesu(java.awt.event.KeyEvent.VK_ENTER); break;
//            case 1: zmackniKlavesu(java.awt.event.KeyEvent.VK_SPACE); break;
//            case 2: zmackniKlavesu(java.awt.event.KeyEvent.VK_UP); break;
//            case 3: zmackniKlavesu(java.awt.event.KeyEvent.VK_DOWN); break;
//
//            default: break;
//        }
//    }
//
//    /**
//     * Funkce vepíše text do jakéhokoliv vstupního pole "kam".
//     * @param kam vstupní pole
//     * @param text na vypsání
//     */
//    public void napis(String kam, String text)
//    {
//        this.type(kam, text);
//        this.fireEvent(kam, "blur");
//    }
//
//    /**
//     * Funkce napíše volný text na vstup klávesnice.
//     */
//    public void napisText()
//    {
//        napisDlouhyText(1, java.awt.event.KeyEvent.VK_SPACE);
//    }
//
//    /**
//     * Funkce napíše volný text na vstup klávesnice o určené délce a zakončení.<br />
//     * Každá věta má 30 znaků (včetně naší klávesy zakončení).
//     * @param kolikVet zadává počet vět, který chceme vypsat za sebou
//     * @param oddelovacVet je identifikační číslo klávesy, která má být stisknuta
//     * po každé větě
//     */
//    public void napisDlouhyText(Integer kolikVet, Integer oddelovacVet)
//    {
//        for(int i=0; i<kolikVet; i++)
//        {
//            drzKlavesu(java.awt.event.KeyEvent.VK_SHIFT);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_A);
//            pustKlavesu(java.awt.event.KeyEvent.VK_SHIFT);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_H);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_O);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_J);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_COMMA);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_SPACE);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_J);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_8);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_SPACE);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_J);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_S);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_E);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_M);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_SPACE);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_Z);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_K);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_O);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_U);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_3);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_K);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_O);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_V);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_7);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_SPACE);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_T);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_E);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_X);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_T);
//            zmackniKlavesu(java.awt.event.KeyEvent.VK_PERIOD);
//            zmackniKlavesu(oddelovacVet);
//        }
//    }
//
//    /**
//     * Funkce smaže všechen text. <br />
//     * <strong>Pozor! Spoléhá se na to, že již máme focus na správném místě.</strong>
//     */
//    public void smazVsechenText()
//    {
//        drzKlavesu(java.awt.event.KeyEvent.VK_CONTROL);
//        zmackniKlavesu(java.awt.event.KeyEvent.VK_A);
//        pustKlavesu(java.awt.event.KeyEvent.VK_CONTROL);
//        zmackniKlavesu(java.awt.event.KeyEvent.VK_DELETE);
//    }
//
//    /**
//     * Funkce smaže všechen text na specifikováném místě.<br />
//     * @param kde je id políčka, kde chceme smazat text
//     */
//    public void smazVsechenText(String kde)
//    {
//        focus(kde);
//        smazVsechenText();
//        this.fireEvent(kde, "blur");
//    }
//
//    /**
//     * Funkce nás přihlasí do aplikace.
//     * @param kam adresa požadované stránky
//     * @param idJmeno je ID inputu, do kterého se píše jméno
//     * @param jmeno přihlašovací jméno
//     * @param idHeslo je ID inputu, do kterého se píše heslo
//     * @param heslo přihlašovací heslo
//     * @param odeslat je ID buttonu, na který se kliká pro přihlášení
//     * @param cekat zda se má čekat na nahrátí další stránky nebo ne (nesmí se čekat na modální okna)
//     */
//    public void prihlasit(String kam, String idJmeno, String jmeno, String idHeslo, String heslo, String odeslat, String cekat)
//    {
//        this.open(kam);
//        this.type(idJmeno, jmeno);
//        this.type(idHeslo, heslo);
//        this.click(odeslat);
//
//        if(cekat.equalsIgnoreCase("A"))
//        {
//            this.waitForPageToLoad("30000");
//        }
//    }
//
//    /**
//     * Funkce nás odhlásí z aplikace.
//     * @param odhlasit ID buttonu, na který se kliká pro odhlášení
//     */
//    public void odhlasit(String odhlasit)
//    {
//        this.click(odhlasit);
//        this.waitForPageToLoad("30000");
//        this.pockej(DVE_VTERINY);
//    }
//
//    /**
//     * Funkce otevře novou stránku v naší aplikaci nalézající se na adrese "kam"e.
//     * @param stranka adresa stránky pro otevření
//     */
//    public void otevrit(String stranka)
//    {
//        this.open(adresaAplikace + "/" + stranka);
//        this.waitForPageToLoad("30000");
//    }
//
//    /**
//     * Funkce vybere první záznam v comboboxu "combo". <br />
//     * <u>Combobox ovšem musí být do této chvíle prázdný, aby funkce fungovala
//     * podle představ, jinak používejte spíše funkci vyberVComboboxu</u>
//     * @param combo je combobox, v němž chceme vybrat první položku
//     * @see #vyberVComboboxu(java.lang.String, java.lang.Integer)
//     * @see #vyberVComboboxu(java.lang.String, java.lang.Integer, boolean)
//     * @deprecated tato funkce se nechová vůbec podle očekávání z jejího názvu,
//     * časem bude přepsána na jinou funkcionalitu, aby se tak chovala
//     */
//    public void vyberPrvniVComboboxu(String combo)
//    {
//        vyberVComboboxu(combo, 1);
//    }
//
//    /**
//     * Funkce vybere záznam v comboboxu "combo", který je na pozici "pozice" od
//     * zhora dolů.<br />
//     * Chceme-li vybrat záznam směrem nahoru, zadáme číslo záporné.
//     * @param combo je combobox, v němž chceme vybrat záznam
//     * @param pozice je pozice záznamu, který chceme vybrat (další záznamy = 1
//     * a větší, předchozí záznamy -1 a menší <strong>Od FOCUSu!</strong>).
//     */
//    public void vyberVComboboxu(String combo, Integer pozice)
//    {
//        vyberVComboboxu(combo, pozice, StringHelper.isNull(this.getValue(combo)));
//    }
//
//    /**
//     * Funkce vybere záznam v comboboxu "combo", který je na pozici "pozice" od
//     * zhora dolů.<br />
//     * Chceme-li vybrat záznam směrem nahoru, zadáme číslo záporné.
//     * @param combo je combobox, v němž chceme vybrat záznam
//     * @param pozice je pozice záznamu, který chceme vybrat (další záznamy = 1
//     * a větší, předchozí záznamy -1 a menší).
//     * @param prazdne říká, jestli je combo prázdné nebo jestli už má nějaký
//     * záznam vybraný
//     */
//    public void vyberVComboboxu(String combo, Integer pozice, boolean prazdne)
//    {
//        String kam;
//
//        if(pozice > 0) {kam = "dolu";}
//        else {kam = "nahoru";}
//
//        pozice = Math.abs(pozice);
//        if(!prazdne) {pozice++;}
//
//        for(int i = 1; i <= pozice; i++)
//        {
//            this.focus(combo);
//            this.zmackniKlavesu(kam);
//            this.fireEvent(combo, "blur");
//        }
//    }
//
//    /**
//     * Funkce přidá v FCK editoru smajlík do textu, který se nachází na pozici,
//     * kde se protíná řádek se sloupcem.<br /><br />
//     * <span style="color: red;"><strong>Pozor!</strong> Je problém u smajlíků,
//     * kteří se kryjí s jinými hodnatami. Problém je u: smajlíka - <br />
//     * 1 řádek, 1 sloupec<br />
//     * 1 řádek, 2 sloupec<br />
//     * 1 řádek, 3 sloupec<br /></span>
//     * @param radek číslo řádku v okně smajlíků (začíná od 1)
//     * @param sloupec číslo sloupce v okně smajlíků (začíná od 1)
//     */
//    public void fckAddSmile(Integer radek, Integer sloupec)
//    {
//        String adresaSmajliku = "//tr[o]/td[a]/img";
//
//        adresaSmajliku = adresaSmajliku.replace("o", radek.toString());
//        adresaSmajliku = adresaSmajliku.replace("a", sloupec.toString());
//
//        this.click("//td[@id='xToolbar']/table[9]/tbody/tr/td[6]/div/img");
//        this.pockej(TRI_VTERINY);
//        this.click(adresaSmajliku);
//    }
//
//    /**
//     * Funkce zavolá focus na "id" a poté zmáčkne klávesu "key".<br />
//     * <strong><i>Používá se pouze interně pro funkce jako menuClick</i></strong>
//     * @param id id komponenty
//     * @param key je identifikační číslo klávesy pro stisknutí
//     *
//     * @see <a href="http://publib.boulder.ibm.com/infocenter/wsadhelp/v5r1m2/index.jsp?topic=/com.sun.api.doc/java/awt/event/KeyEvent.html">
//     * Přehled kláves</a>
//     */
//    private void focusAndKey(String id, Integer key)
//    {
//        this.focus(id);
//        this.zmackniKlavesu(key);
//    }
//
//    /**
//     * Funkce vrací aktuální počet záznamů v tabulce "tabulka".
//     * @param tabulka , ve které chceme zjistit počet záznamů
//     * @return počet záznamů v tabulce
//     */
//    public Integer pocetZaznamuVTabulce(String tabulka)
//    {
//        String xpathAdresa = "//table[@id='xxx!cave']/tbody[2]/tr";
//        xpathAdresa = xpathAdresa.replace("xxx", tabulka);
//
//        return this.getXpathCount(xpathAdresa).intValue();
//    }
//
//    /**
//     * Funkce vrací kompletní xpath adresu pro element v tabulce nacházející se
//     * na prusečíku hodnoty řádku a sloupce.<br /><br />
//     * <strong>Příklad:</strong> <br />
//     * <i>test.napis(xpathAdresaProElementVTabulce(dtl_tabulka, 2, 3));</i>
//     * @param tabulka je tabulka, ve které chceme nalést element
//     * @param radek řádek v tabulce, ve kterém se nachází element
//     * @param sloupec sloupec v tabulce, ve kterém se nachází element
//     * @param element element v tabulce (zda se tam jedná o label, textbox,
//     * combobox apod.
//     * @return xpath adresu na element, nad kterou se dají provádět jakékoliv
//     * další události
//     */
//    public String xpathAdresaProElementVTabulce(String tabulka, Integer radek, Integer sloupec, HTMLElementy element)
//    {
//        String xpathAdresa = "xpath=//table[@id='x%t!cave']/tbody[2]/tr[x%r]/td[x%s]/div/x%e";
//
//        xpathAdresa = xpathAdresa.replace("x%t", tabulka);
//        xpathAdresa = xpathAdresa.replace("x%r", radek.toString());
//        xpathAdresa = xpathAdresa.replace("x%s", sloupec.toString());
//        xpathAdresa = xpathAdresa.replace("x%e", element.getHtml());
//
//        return xpathAdresa;
//    }
//
//    /**
//     * Funkce čeká, až se do tabulky "tabulka" promítne námi požadovaný počet
//     * záznamů "pocetZaznamu".
//     * <ul>
//     * <li>Až se promítne, funkce se ukončí a vrátí true.</li>
//     * <li>Nepromítne-li se, funkce vrací false.</li>
//     * </ul>
//     * @param tabulka je tabulka ve které chceme záznamy
//     * @param pocetZaznamu je počet záznamů, který chceme v tabulce
//     * @return false = počet záznamů nenalezen; true = nalezeno
//     */
//    public boolean vyckejNaDataVTabulce(String tabulka, Integer pocetZaznamu)
//    {
//        Integer i = 0;
//        Integer zaznamy;
//
//        for( ; ; )
//        {
//            if(i > 29)
//            {
//                return false;
//            }
//
//            i++;
//            zaznamy = pocetZaznamuVTabulce(tabulka);
//
//            if(pocetZaznamu == zaznamy)
//            {
//                this.pockej(100);
//                return true;
//            }
//            else
//            {
//                this.pockej(500);
//                continue;
//            }
//        }
//    }
//
//    /**
//     * Funkce čeká na to, až se do tabulky "tabulka" promítne nějaká změna v
//     * počtu záznamů.
//     * <ul>
//     * <li>Nepromítne-li se, funkce vrací false.</li>
//     * <li>Promítne-li se, ale výsledný počet záznamů = 0, vrací false.</li>
//     * <li>Promítne-li se a najde nějaké nové záznamy, vrací true.</li>
//     * </ul>
//     * @param tabulka je tabulka ve které chceme záznamy
//     * @return false = nenalezena změna v tabulce nebo je počet nových záznamů 0;
//     * true = nalezena změna a nové záznamy
//     */
//    public boolean vyckejNaDataVTabulce(String tabulka)
//    {
//        Integer predchoziZaznamy = pocetZaznamuVTabulce(tabulka);
//        Integer zaznamy;
//
//        for(int i = 0; i < 30; i++)
//        {
//            zaznamy = pocetZaznamuVTabulce(tabulka);
//
//            if(predchoziZaznamy == zaznamy)
//            {
//                this.pockej(500);
//                continue;
//            }
//            else if(zaznamy == 0)
//            {
//                return false;
//            }
//            else
//            {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * Funkce klikne na prvek pod id "kam" a předá mu i rovnou focus (nutné
//     * např. pro tabulky)
//     * @param kam id prvku, na který cheme kliknout
//     */
//    public void klikni(String kam)
//    {
//        this.click(kam);
//        this.focus(kam);
//    }
//
//    /**
//     * Výčtový typ pro HTML elementy. <br />
//     * Používá se ve funkci xpathAdresaProElementVTabulce.
//     */
//    public enum HTMLElementy
//    {
//        LABEL   (""),
//        TEXTBOX ("input"),
//        COMBOBOX("/span/input"),
//        CHECKBOX("/span/input");
//
//        private final String html;
//
//        HTMLElementy(String html)
//        {
//            this.html = html;
//        }
//
//        public String getHtml()
//        {
//            return html;
//        }
//    }
//
//}
