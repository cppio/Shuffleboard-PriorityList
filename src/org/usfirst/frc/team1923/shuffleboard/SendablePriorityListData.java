package org.usfirst.frc.team1923.shuffleboard;

import com.google.common.collect.ImmutableMap;

import edu.wpi.first.shuffleboard.api.data.ComplexData;
import edu.wpi.first.shuffleboard.api.util.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class SendablePriorityListData extends ComplexData<SendablePriorityListData> {

    private final String[] items;

    public SendablePriorityListData(Map<String, Object> map) {
        this.items = ((String[]) map.getOrDefault("values", new String[0])).clone();
    }

    public SendablePriorityListData(List<String> values) {
        this.items = values.toArray(new String[0]);
    }

    public String[] getItems() {
        return this.items.clone();
    }

    @Override
    public String toString() {
        return String.format("SendablePriorityListData(items=%s)", Arrays.toString(this.items));
    }

    @Override
    public Map<String, Object> asMap() {
        return ImmutableMap.of("values", items.clone());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SendablePriorityListData)) {
            return false;
        }
        return Arrays.equals(this.items, ((SendablePriorityListData) obj).items);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.items);
    }
}
