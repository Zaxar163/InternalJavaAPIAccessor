package ru.zaxar163.util.dynamicgen;

import lombok.experimental.UtilityClass;
import ru.zaxar163.util.proxies.ProxyList;
import ru.zaxar163.util.proxies.UnsafeProxy;

@UtilityClass
public class FastUtil {
	private static final UnsafeProxy acc = ProxyList.UNSAFE;
	private static final int bbaseOffset = (Integer) ProxyList.UNSAFE_FIELDS.get("ARRAY_BYTE_BASE_OFFSET");

	public static boolean fastEquals(final byte[] b1, final byte[] b2) {
		if (b1 == b2)
			return true;
		if (b1.length != b2.length)
			return false;

		final int numLongs = (int) Math.ceil(b1.length / 8.0);
		for (int i = 0; i < numLongs; ++i) {
			final long currentOffset = bbaseOffset + i * 8;
			final long l1 = acc.getLong(b1, currentOffset);
			final long l2 = acc.getLong(b2, currentOffset);
			if (0L != (l1 ^ l2))
				return false;
		}
		return true;
	}
}
