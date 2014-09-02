package cz.datalite.collection;

import java.io.Serializable;
import java.util.Objects;

/**
 * Par dvou hodnot.
 * 
 * Neco jako org.elasticsearch.common.collect.Tuple;
 * 
 * @author mstastny
 */
public class Pair<LT, RT>
		implements Serializable {

	private final LT lv;
	private final RT rv;

	public Pair(LT lv, RT rv) {
		this.lv = lv;
		this.rv = rv;
	}

	public LT lv() {
		return this.lv;
	}

	public RT rv() {
		return this.rv;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;

		if (!(o instanceof Pair))
			return false;

		Pair<?, ?> other = (Pair<?, ?>) o;

		if (!Objects.equals(this.lv, other.lv)) {
			return false;
		}

		if (!Objects.equals(this.rv, other.rv)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = (this.lv != null) ? this.lv.hashCode() : 0;
		result = 31 * result + ((this.rv != null) ? this.rv.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Pair [lv=").append(lv);
		builder.append(", rv=").append(rv).append("]");
		return builder.toString();
	}

}
