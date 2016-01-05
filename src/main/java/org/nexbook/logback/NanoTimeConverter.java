package org.nexbook.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Created by milczu on 05.01.16.
 */
public class NanoTimeConverter extends ClassicConverter {

	@Override
	public String convert(ILoggingEvent event) {
		return Long.toString(System.nanoTime());
	}
}
