package com.settle.pg;

public class StringUtil {
	public static String join(String delimiter, Iterable<?> values) {
		if (delimiter == null) {
			throw new IllegalArgumentException("delimiter must not be null");
		}
		if (values == null) {
			throw new IllegalArgumentException("values must not be null");
		}

		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Object value : values) {
			if (!first) {
				result.append(delimiter);
			}
			result.append(String.valueOf(value));
			first = false;
		}
		return result.toString();
	}

	public static String isNull(Object obj) {
		if (obj == null) {
			return "";
		} else if (obj instanceof String) {
			if( "null".equals((String)obj)) {
				return "";
			}else {
				return (String) obj;
			}
		} else {
			return String.valueOf(obj);
		}
	}
}
