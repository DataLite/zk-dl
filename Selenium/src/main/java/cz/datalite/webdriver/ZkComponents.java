package cz.datalite.webdriver;

import cz.datalite.webdriver.components.*;
import org.openqa.selenium.By;

import org.openqa.selenium.WebElement;

/**
 * Defines components which can be found in HTML. There are defined
 * styles and rules how to work with it. This is used in By clausules
 * and ElementFactory.
 *
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public enum ZkComponents {

    TEXTBOX( "z-textbox", true ) {

        @Override
        public ZkElement create( final ZkElement parent, final WebElement webElement ) {
            return new Textbox( parent, webElement );
        }
    },
    INTBOX( "z-intbox", true ) {

        @Override
        public ZkElement create( final ZkElement parent, final WebElement webElement ) {
            return new Intbox( parent, webElement );
        }
    },
    MODAL_WINDOW( "z-window-highlighted" ) {

        @Override
        public Window create( final ZkElement parent, final WebElement webElement ) {
            return new Window( parent, webElement );
        }
    },
    WINDOW( "z-window-embedded" ) {

        @Override
        public Window create( final ZkElement parent, final WebElement webElement ) {
            return new Window( parent, webElement );
        }
    },
    DOUBLEBOX( "z-doublebox", true ) {

        @Override
        public ZkElement create( final ZkElement parent, final WebElement webElement ) {
            return new Doublebox( parent, webElement );
        }
    },
    DECIMALBOX( "z-decimalbox", true ) {

        @Override
        public ZkElement create( final ZkElement parent, final WebElement webElement ) {
            return new Decimalbox( parent, webElement );
        }
    },
    DATEBOX( "z-datebox", true ) {

        @Override
        public ZkElement create( final ZkElement parent, final WebElement webElement ) {
            return new Datebox( parent, webElement );
        }
    },
    LOVBOX( "z-lovbox", true ) {

        @Override
        public ZkElement create( final ZkElement parent, final WebElement webElement ) {
            return new Lovbox( parent, webElement );
        }
    },
    LISTBOX( "z-listbox" ) {

        @Override
        public ZkElement create( final ZkElement parent, final WebElement webElement ) {
            return new Listbox( parent, webElement );
        }
    },
    CHECKBOX( "z-checkbox", true ) {

        @Override
        public ZkElement create( final ZkElement parent, final WebElement webElement ) {
            return new Checkbox( parent, webElement );
        }
    },
    BUTTON( "z-button-os" ) {

        @Override
        public ZkElement create( final ZkElement parent, final WebElement webElement ) {
            return new Button( parent, webElement );
        }
    },
    GRID( "z-grid" ) {

        @Override
        public ZkElement create( final ZkElement parent, final WebElement webElement ) {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    },
    COMBOBOX( "z-combobox", true ) {

        @Override
        public ZkElement create( final ZkElement parent, final WebElement webElement ) {
            return new Combobox( parent, webElement );
        }
    };
    /*
     *
     *
     *
     * 
     */
    protected final By by;
    protected final String styleClass;
    protected final boolean formElement;

    ZkComponents( final String styleClass ) {
        this( styleClass, false );
    }

    ZkComponents( final String styleClass, final boolean isFormElement ) {
        this.styleClass = styleClass;
        this.formElement = isFormElement;
        by = By.className( styleClass );
    }

    public abstract ZkElement create( final ZkElement parent, final WebElement webElement );

    /**
     * Returns style class name
     * @return style class name
     */
    public String getStyleClass() {
        return styleClass;
    }

    /**
     * Returns how to find the element in the given context
     * @return searching criterion
     */
    public By getBy() {
        return by;
    }

    /**
     * Returns if the component is part of form
     * and will be looked up in form
     * @return if the element can be part of form
     */
    public boolean isFormElement() {
        return formElement;
    }
}
