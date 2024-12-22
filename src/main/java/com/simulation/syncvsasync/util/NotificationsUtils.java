package com.simulation.syncvsasync.util;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static com.simulation.syncvsasync.util.DemoConstants.DAEMON;
import static com.simulation.syncvsasync.util.DemoConstants.RESULT_MAP;
import static com.simulation.syncvsasync.util.DemoConstants.THREADGROUP;
import static com.simulation.syncvsasync.util.DemoConstants.THREAD_NAME;
import static com.simulation.syncvsasync.util.DemoConstants.THREAD_STATE;

/**
 * Util interface for show errors.
 *
 * @author rubn
 */
public interface NotificationsUtils {


    /**
     * Show message error to the client!
     *
     * @param msg the message
     */
    default void showError(String msg) {
        final Notification n = new Notification("😱 " + msg);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        n.setPosition(Position.MIDDLE);
        n.setDuration(2500);
        n.open();
    }

    /**
     * Show message to the client!
     *
     * @param msg the message
     */
    default void showMessage(final String msg) {
        final Notification n = new Notification("\uD83E\uDD19\uD83C\uDFFF ".concat(msg));
        n.setPosition(Position.MIDDLE);
        n.setDuration(2500);
        n.open();
    }

    /**
     * Show logging
     *
     * @param log the Logger not Log4Shell
     * @param resultMap the result map
     */
    default void showLogger(Logger log, final Map<Integer, Long> resultMap) {
        log.info(RESULT_MAP, resultMap);
        log.info(DAEMON, Thread.currentThread().isDaemon());
        log.info(THREAD_NAME, Thread.currentThread().getName());
        log.info(THREADGROUP, Thread.currentThread().getThreadGroup());
        log.info(THREAD_STATE, Thread.currentThread().getState());
    }

}
