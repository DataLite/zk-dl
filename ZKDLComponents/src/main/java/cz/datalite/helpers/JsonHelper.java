package cz.datalite.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.datalite.zk.components.profile.DLFilterBean;

public class JsonHelper {
	
	@SuppressWarnings("rawtypes")
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
			List<String> stringCollection = new ArrayList<String>();
			
			for (Object value : collection) {
				if (isBasicType(value)) {
					return collection;
				} else if (value.getClass().isEnum()) {
					stringCollection.add(value.toString());
				} else {
					throw new IllegalArgumentException("Unserializable filter type: " + value.getClass() + " in collection");
				}
			}
			
			return stringCollection;
		} else {
			throw new IllegalArgumentException("Unserializable filter type: " + object.getClass());
		}
	}
	
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
			
			if (DLFilterBean.class.isAssignableFrom(typeClazz)) {
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
			} else if (Date.class.isAssignableFrom(typeClazz)) {
				return new Date((Long) object);
			} else if (Collection.class.isAssignableFrom(typeClazz)) {
				Collection<?> collection = (Collection<?>) object;

				if (collectionClazz != null && collectionClazz.isEnum()) {
					Collection<Enum> enumCollection = null;

					if (List.class.isAssignableFrom(typeClazz)) {
						enumCollection = new ArrayList<Enum>();
					} else if (Set.class.isAssignableFrom(typeClazz)) {
						enumCollection = new HashSet<Enum>();
					}

					for (Object value : collection) {
						enumCollection.add(Enum.valueOf((Class<? extends Enum>) collectionClazz, value.toString()));
					}
					return enumCollection;
				}
				return collection;
			}
		}
		
		return object;
	}
	
	public static boolean isBasicType(Object object) {
		return object.getClass().isPrimitive() || object instanceof Number || object instanceof String || object instanceof Boolean;				
	}

}
