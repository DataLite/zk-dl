package cz.datalite.helpers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.datalite.zk.components.profile.DLFilterBean;

public class JsonHelper {
	
	/**
	 * Method converts object to json serializable format.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final Object toJsonObject(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof DLFilterBean) {
			return ((DLFilterBean) object).toJson();
		} else if (isBasicType(object)) {
			return object;
		}  else if (object.getClass().isEnum()) {
			return object.toString();
		} else if (Date.class.isAssignableFrom(object.getClass())) {
			return ((Date) object).getTime();
		} else if (Collection.class.isAssignableFrom(object.getClass())) {
			Collection<?> collection = (Collection<?>) object;
			List serializableCollection = new ArrayList();
			
			for (Object value : collection) {
				serializableCollection.add(toJsonObject(value));
			}
			
			return serializableCollection;
		} else {
			throw new IllegalArgumentException("Unserializable filter type: " + object.getClass());
		}
	}
	
	/**
	 * Method restores original object. 
	 */	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final Object fromJsonObject(Object object, String type) {
		if (object == null) {
			return null;
		} else if (!StringHelper.isNull(type)) {
			Class<?> typeClazz = null;
			Class<?> collectionClazz = null;
			try {
				if (type.contains("#")) {
					typeClazz = Class.forName(type.substring(0, type.indexOf("#")));
					collectionClazz = Class.forName(type.substring(type.indexOf("#") + 1));
				} else {
					typeClazz = Class.forName(type);
				}
			} catch (ClassNotFoundException e) {
				return null;
			}

			if (Collection.class.isAssignableFrom(typeClazz)) {
				Collection<?> collection = (Collection<?>) object;
				Collection typeCollection = null;

				if (List.class.isAssignableFrom(typeClazz)) {
					typeCollection = new ArrayList();
				} else if (Set.class.isAssignableFrom(typeClazz)) {
					typeCollection = new HashSet();
				}

				if (collectionClazz != null) {
					for (Object value : collection) {
						typeCollection.add(restoreType(value, collectionClazz));
					}
				} else {
					typeCollection.addAll((Collection<?>) object);
				}
				return typeCollection;
			} else {
				return restoreType(object, typeClazz);
			}
		}

		return object;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object restoreType(Object object, Class<?> typeClazz) {
		if (object == null) {
			return null;
		} else if (typeClazz == null) {
			return object;
		} else if (DLFilterBean.class.isAssignableFrom(typeClazz)) {
			try {
				return ((DLFilterBean) typeClazz.newInstance()).fromJson(object.toString());
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		} else if (typeClazz.isEnum()) {
			return Enum.valueOf((Class<? extends Enum>) typeClazz, object.toString());
		} else if (String.class.isAssignableFrom(typeClazz)) {
			return String.valueOf(object);
		} else if (Long.class.isAssignableFrom(typeClazz)) {
			return Long.valueOf(object.toString());
		} else if (Double.class.isAssignableFrom(typeClazz)) {
			return Double.valueOf(object.toString());
		} else if (BigDecimal.class.isAssignableFrom(typeClazz)) {
			return new BigDecimal(object.toString());
		} else if (Float.class.isAssignableFrom(typeClazz)) {
			return Float.valueOf(object.toString());
		} else if (Date.class.isAssignableFrom(typeClazz)) {
			return new Date((Long) object);
		} else {
			return object;
		}
	}
	
	public static String getType(Object value) {
		String valueType = ((value != null) ? value.getClass().getName() : "");

		if (value != null && Collection.class.isAssignableFrom(value.getClass())) {
			Object o = ((Collection<?>) value).iterator().next();
			if (o != null) {
				valueType = valueType + "#" + o.getClass().getName();
			}
		}

		return valueType;
	}
	
	public static boolean isBasicType(Object object) {
		return object.getClass().isPrimitive() || object instanceof Number || object instanceof String || object instanceof Boolean;
	}
}
