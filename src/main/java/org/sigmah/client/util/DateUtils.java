package org.sigmah.client.util;

import java.util.Comparator;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

/**
 * Utility class to manipulates dates.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class DateUtils {

	/**
	 * Short date format.
	 * 
	 * @see PredefinedFormat#DATE_SHORT
	 */
	public static final DateTimeFormat DATE_SHORT = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);

	/**
	 * Short date/time format.
	 * 
	 * @see PredefinedFormat#DATE_TIME_SHORT
	 */
	public static final DateTimeFormat DATE_TIME_SHORT = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT);

	/**
	 * Compare two dates. The comparison ignores hours, minutes and seconds.
	 */
	@SuppressWarnings("deprecation")
	public static final Comparator<Date> DAY_COMPARATOR = new Comparator<Date>() {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final Date d1, final Date d2) {

			if (d1 == null) {
				if (d2 == null) {
					return 0;
				} else {
					return -1;
				}
			}

			if (d2 == null) {
				return 1;
			}

			if (d1.getYear() < d2.getYear()) {
				return -1;
			} else if (d1.getYear() > d2.getYear()) {
				return 1;
			} else {
				if (d1.getMonth() < d2.getMonth()) {
					return -1;
				} else if (d1.getMonth() > d2.getMonth()) {
					return 1;
				} else {
					if (d1.getDate() < d2.getDate()) {
						return -1;
					} else if (d1.getDate() > d2.getDate()) {
						return 1;
					} else {
						return 0;
					}
				}
			}
		}
	};

	/**
	 * Private constructor.
	 */
	private DateUtils() {
		// Only provides static constants.
	}

}
