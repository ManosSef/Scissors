package me.manossef.scissors.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import me.manossef.scissors.DevGuild;

public class DiscordAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private Layout<ILoggingEvent> layout;

    @Override
    protected void append(ILoggingEvent event) {

        DevGuild.log(this.layout.doLayout(event));

    }

    public Layout<ILoggingEvent> getLayout() {

        return this.layout;

    }

    public void setLayout(Layout<ILoggingEvent> layout) {

        this.layout = layout;

    }

}