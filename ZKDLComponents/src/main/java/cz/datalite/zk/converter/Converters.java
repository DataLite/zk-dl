package cz.datalite.zk.converter;

import cz.datalite.zk.components.intbox.BigDecimalConverter;

/**
 * Converters holder ("Register Application Level Converters": Available in ZK EE only)
 * <p>Can be registered as a bean and accessed by ZK EL.</p>
 */
@SuppressWarnings("UnusedDeclaration")
public class Converters {

	private SimpleDateTimeConverter dateTime = new SimpleDateTimeConverter();

	private DurationConverter duration = new DurationConverter();

	private EnumConverter enumConverter = new EnumConverter();

	private BooleanConverter booleanConverter = new BooleanConverter();

	private ClassNameConverter className = new ClassNameConverter();

	private CollectionConverter collection = new CollectionConverter();

	private ArrayConverter arrayConverter = new ArrayConverter();

	private AbbreviateConverter abbreviate = new AbbreviateConverter();

	private BigDecimalConverter bigDecimal = new BigDecimalConverter();

	private NonPrintableCharactersConverter nonPrintableCharacters = new NonPrintableCharactersConverter();

	private CoordinateConverter coordinateConverter = new CoordinateConverter();

	private EmailConverter email = new EmailConverter();

	private DateOnlyConverter dateOnly = new DateOnlyConverter();

	private SmartConverter smart = new SmartConverter();

	private PercentConverter percent = new PercentConverter();

	private CollectionPropertyConverter collectionProperty = new CollectionPropertyConverter();

	public SimpleDateTimeConverter getDateTime() {
		return dateTime;
	}

	public DurationConverter getDuration() {
		return duration;
	}

	public EnumConverter getEnumConverter() {
		return enumConverter;
	}

	public BooleanConverter getBooleanConverter() {
		return booleanConverter;
	}

	public ClassNameConverter getClassName() {
		return className;
	}

	public CollectionConverter getCollection() {
		return collection;
	}

	public ArrayConverter getArrayConverter() {
		return arrayConverter;
	}

	public AbbreviateConverter getAbbreviate() {
		return abbreviate;
	}

	public BigDecimalConverter getBigDecimal() {
		return bigDecimal;
	}

	public CoordinateConverter getCoordinateConverter() {
		return coordinateConverter;
	}

	public NonPrintableCharactersConverter getNonPrintableCharacters() {
		return nonPrintableCharacters;
	}

	public EmailConverter getEmail() {
		return email;
	}

	public DateOnlyConverter getDateOnly() {
		return dateOnly;
	}

	public SmartConverter getSmart() {
		return smart;
	}

	public PercentConverter getPercent() {
		return percent;
	}

	public CollectionPropertyConverter getCollectionProperty() {
		return collectionProperty;
	}
}
