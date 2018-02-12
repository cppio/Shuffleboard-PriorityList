package org.usfirst.frc.team1923.shuffleboard;

import edu.wpi.first.shuffleboard.api.data.ComplexData;
import edu.wpi.first.shuffleboard.api.util.Maps;

import java.util.Arrays;
import java.util.Map;

public final class SendablePriorityListData extends ComplexData<SendablePriorityListData> {

    private final String[] items;

    public SendablePriorityListData(Map<String, Object> map) {
        this.items = new String[map.size()];
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            this.items[Integer.parseInt(entry.getKey())] = (String) entry.getValue();
        }
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
        Maps.MapBuilder<String, Object> builder = Maps.builder();
        for (int i = 0; i < this.items.length; ++i) {
            builder.put(String.valueOf(i), this.items[i]);
        }
        return builder.build();
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
