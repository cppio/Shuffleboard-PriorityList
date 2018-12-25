package org.usfirst.frc.team1923.shuffleboard;

import edu.wpi.first.shuffleboard.api.data.ComplexDataType;

import org.usfirst.frc.team1923.shuffleboard.SendablePriorityListData;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public final class SendablePriorityListType extends ComplexDataType<SendablePriorityListData> {

    public static final SendablePriorityListType Instance = new SendablePriorityListType();

    private SendablePriorityListType() {
        super("Priority List", SendablePriorityListData.class);
    }

    @Override
    public Function<Map<String, Object>, SendablePriorityListData> fromMap() {
        return SendablePriorityListData::new;
    }

    @Override
    public SendablePriorityListData getDefaultValue() {
        return new SendablePriorityListData(Collections.emptyMap());
    }

}
