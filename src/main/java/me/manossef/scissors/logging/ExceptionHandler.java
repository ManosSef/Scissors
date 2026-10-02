package me.manossef.scissors.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import me.manossef.scissors.Issues;

public class ExceptionHandler extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private Layout<ILoggingEvent> layout;

    @Override
    protected void append(ILoggingEvent event) {
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if(throwableProxy == null) return;
        Throwable t = ((ThrowableProxy) throwableProxy).getThrowable();
        if(t == null) return;
        Issues.createForException(t, "", "{{" + this.layout.doLayout(event) + "}}");
    }

    public Layout<ILoggingEvent> getLayout() {
        return this.layout;
    }

    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }
}