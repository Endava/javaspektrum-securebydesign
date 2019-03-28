package com.java4spektrum.configuration;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(name="LogMaskingConverter", category = "Converter")
@ConverterKeys({"m"})
public class LogMaskingConverter extends LogEventPatternConverter{

    private LogMaskingConverter(String name, String style) {
        super(name, style);
    }

    public static LogMaskingConverter newInstance(String[] options) {
        return new LogMaskingConverter("m",Thread.currentThread().getName());
    }

    @Override
    public void format(LogEvent event, StringBuilder outputMessage) {
        String message = event.getMessage().getFormattedMessage();
        outputMessage.append(message.replaceAll("([0-9]{16})", "****************"));
    }

}

