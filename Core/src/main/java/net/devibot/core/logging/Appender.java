package net.devibot.core.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import net.devibot.core.Config;
import net.devibot.core.Core;
import net.devibot.core.database.DatabaseManager;
import org.bson.Document;
import org.slf4j.Marker;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Appender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        Document log = getBasicLog(iLoggingEvent);

        try {
            logException(iLoggingEvent.getThrowableProxy(), log);
            DatabaseManager.getInstance().addLog(log);
        } catch (Exception e) {
            //doesn't work
        }

    }

    private Document getBasicLog(ILoggingEvent evt) {
        Document log = new Document();
        log.put("logger", evt.getLoggerName());
        log.put("timestamp", new Date(evt.getTimeStamp()));
        log.put("level", String.valueOf(evt.getLevel())); //in case getLevel returns null
        Marker m = evt.getMarker();
        if (m != null) {
            log.put("marker", m.getName());
        }
        log.put("thread", evt.getThreadName());
        log.put("message", evt.getFormattedMessage());
        log.put("type", Core.TYPE.name());
        return log;
    }

    private void logException(IThrowableProxy tp, Document log) {
        if (tp == null) return;
        String tpAsString = ThrowableProxyUtil.asString(tp); //the stack trace basically
        List<String> stackTrace = Arrays.asList(tpAsString.replace("\t", "").split(CoreConstants.LINE_SEPARATOR));
        if (stackTrace.size() > 0) {
            log.put("exception", stackTrace.get(0));
        }
        if (stackTrace.size() > 1) {
            log.put("stacktrace", stackTrace.subList(1, stackTrace.size()));
        }
    }
}
