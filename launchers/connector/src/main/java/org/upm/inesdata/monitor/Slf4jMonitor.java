package org.upm.inesdata.monitor;

import org.eclipse.edc.spi.monitor.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class Slf4jMonitor implements Monitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jMonitor.class);

    @Override
    public void debug(Supplier<String> supplier, Throwable... errors) {
        debug(supplier.get(), errors);
    }

    @Override
    public void debug(String message, Throwable... errors) {
        if (errors.length == 0) {
            LOGGER.debug(message);
        } else {
            for (Throwable error : errors) {
                LOGGER.debug(message, error);
            }
        }
    }

    @Override
    public void info(Supplier<String> supplier, Throwable... errors) {
        info(supplier.get(), errors);
    }

    @Override
    public void info(String message, Throwable... errors) {
        if (errors.length == 0) {
            LOGGER.info(message);
        } else {
            for (Throwable error : errors) {
                LOGGER.info(message, error);
            }
        }
    }

    @Override
    public void severe(Supplier<String> supplier, Throwable... errors) {
        severe(supplier.get(), errors);
    }

    @Override
    public void severe(String message, Throwable... errors) {
        if (errors.length == 0) {
            LOGGER.error(message);
        } else {
            for (Throwable error : errors) {
                LOGGER.error(message, error);
            }
        }
    }

    @Override
    public void warning(Supplier<String> supplier, Throwable... errors) {
        warning(supplier.get(), errors);
    }

    @Override
    public void warning(String message, Throwable... errors) {
        if (errors.length == 0) {
            LOGGER.warn(message);
        } else {
            for (Throwable error : errors) {
                LOGGER.warn(message, error);
            }
        }
    }
}