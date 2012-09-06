package cz.datalite.webdriver;

import cz.datalite.webdriver.components.Listbox;
import cz.datalite.webdriver.components.Window;
import cz.datalite.webdriver.components.ZkElement;
import org.junit.*;
import org.junit.rules.MethodRule;
import org.junit.rules.TestName;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * This is parent class of SeleniumUnitTests which
 * can detect ZkWindow annotation and according to
 * it prepares the page for each unitTest. Also
 * invokes some standard tests which test listboxes
 * etc. After each test there is controlled if the
 * error window was thrown. If it is found then the
 * exception is thrown and test fails. Also this class
 * looks for login page and tries to log in.
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class SeleniumUnitTest {

    /** browser's driver */
    protected static ZkDriver driver;
    /** name of actual test */
    protected static String testName; // name of actual test
    /** actual tested window */
    protected Window window;
    /** if the detail is tested then there is parent
     * window. Otherwise it is null */
    protected Window parentWindow;
    /** window server id */
    protected String windowId;
    /** detailed window server id */
    protected String detailId;
    /** page url without prefix */
    protected String page;
    /** if the detail is tested  */
    protected boolean definedDetail;

    @BeforeClass
    public static void setUpClass() {
        driver = ZkDriver.init();
    }

    @AfterClass
    public static void tearDownClass() {
        driver.close();
    }
    @Rule
    public TestName name = new TestName();

    @Rule
    public MethodRule watchman = new TestWatchman() {
        @Override
        public void failed(Throwable e, FrameworkMethod method) {
            ZkElement.takeScreenshot();
            super.failed(e, method);
        }
    };

    @Before
    public void setUp() {
        testName = getClass().getSimpleName() + "." + name.getMethodName();
        driver.get( page );
        initPage();
    }

    /**
     * Finds window on the page and opens the detail if is defined.
     */
    protected void initPage() {
        driver.waitForProcessing();
        window = ZkElement.findWindow( By.serverId( windowId ), true );
        if ( definedDetail ) {
            openDetail( window );
            parentWindow = window;
            window = ZkElement.findWindow( By.serverId( detailId ), true );
        }
    }

    @After
    public void tearDown() {
        ZkElement.handleError();
    }

    public SeleniumUnitTest() {
        init();
    }

    /**
     * Loads annotation ZkWindow
     * @throws MissingAnnotationException
     */
    protected void init() throws MissingAnnotationException {
        for ( Annotation annot : getClass().getAnnotations() ) {
            if ( annot.annotationType().isAssignableFrom( ZkWindow.class ) ) {
                loadZkWindow( ( ZkWindow ) annot );
                return;
            }
        }
        throw new MissingAnnotationException( "The annotation ZkWindow is missing on current test." );
    }

    /**
     * After the page is loaded and main window is set
     * then the detail has to be opened. It is purpose
     * of this method.
     * @param window main page window
     */
    protected void openDetail( final Window window ) {
        throw new UnsupportedOperationException( "Detail window couldn't be opened. Opening method is not defined." );
    }

    protected void loadZkWindow( final ZkWindow annot ) {
        page = annot.url();
        windowId = annot.id();
        detailId = annot.detailId();
        definedDetail = "".equals( detailId );
        definedDetail ^= true;
    }

    /**
     * Test is valid only in fullTest context. This test clicks
     * on every listheader on the page. This should determine if
     * the sorting is properly defined.
     */
    @Test
    public void listboxSortingTest() {
        if ( !driver.isFullTest() ) {
            return;
        }

        final Set<Listbox> listboxes = window.findListboxes();
        for ( Listbox listbox : listboxes ) {
            listbox.testListheaders();
        }
    }

    /**
     * This test tries to set some values into quick filters.
     * The values are dummy so there should be no results
     * but also not errors. It tests standard conversions
     * in find all test. This test is executed only in full
     * test mode.
     */
    @Test
    public void listboxQuickFilterTest() {
        if ( !driver.isFullTest() ) {
            return;
        }

        final Set<Listbox> listboxes = window.findListboxes();
        for ( Listbox listbox : listboxes ) {
            testQuickFilter( "zxasdfdjafl341513dfala", listbox );
            testQuickFilter( "5353434544146454545454", listbox );
            testQuickFilter( "05.10.2284", listbox );
        }
    }

    /**
     * This test says if the page is opened without error.
     * It is executed also in simple test mode.
     */
    @Test
    public void openPageTest() {
        if ( definedDetail ) {
            window.close();
        }
    }

    protected void testQuickFilter( final String value, final Listbox listbox ) {
        if ( listbox.isQuickFilter() ) {
            listbox.setQuickFilter( value );
            if ( listbox.isPaging() ) {
                Assert.assertEquals( "Listbox quick filter probably doesn't work. There were expected empty list. Page count is not 0.", listbox.getPageCount(), 0 );
            }
            Assert.assertEquals( "Listbox quick filter probably doesn't work. There were expected empty list. Row count is not 0.", listbox.getRowCount(), 0 );
        }
    }

    /**
     * Returns name of actual test including its class
     * @return name of actual test
     */
    public static String getActualTestName() {
        return testName;
    }
}

class MissingAnnotationException extends RuntimeException {

    public MissingAnnotationException( final String message, final Throwable cause ) {
        super( message, cause );
    }

    public MissingAnnotationException( final String message ) {
        super( message );
    }
}
