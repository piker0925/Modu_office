package com.modu.office.entity.converter;

import com.modu.office.entity.enums.LogAction;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class LogActionConverter implements AttributeConverter<LogAction, String> {

    @Override
    public String convertToDatabaseColumn(LogAction action) {
        if (action == null) {
            return null;
        }
        return action.getValue();
    }

    @Override
    public LogAction convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return Stream.of(LogAction.values())
                .filter(c -> c.getValue().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
