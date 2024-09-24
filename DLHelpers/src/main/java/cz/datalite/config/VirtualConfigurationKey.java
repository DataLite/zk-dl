package cz.datalite.config;

/**
 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
 */
public class VirtualConfigurationKey
		implements ConfigurationKey {

	private final String key;

	public VirtualConfigurationKey(String key) {
		this.key = key;
	}

	@Override
	public String getValue() {
		return key;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("VirtualConfigurationKey{");
		sb.append("key='").append(key).append('\'');
		sb.append('}');
		return sb.toString();
	}
}