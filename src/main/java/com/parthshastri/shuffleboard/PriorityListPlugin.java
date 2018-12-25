package org.usfirst.frc.team1923.shuffleboard;

import com.google.common.collect.ImmutableMap;

import edu.wpi.first.shuffleboard.api.data.DataType;
import edu.wpi.first.shuffleboard.api.plugin.Description;
import edu.wpi.first.shuffleboard.api.plugin.Plugin;
import edu.wpi.first.shuffleboard.api.widget.ComponentType;
import edu.wpi.first.shuffleboard.api.widget.WidgetType;

import org.usfirst.frc.team1923.shuffleboard.PriorityListWidget;
import org.usfirst.frc.team1923.shuffleboard.SendablePriorityListType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Description(
    group = "org.usfirst.frc.team1923.shuffleboard",
    name = "Priority List Plugin",
    summary = "Plugin with Priority List Widget",
    version = "0.0.0"
)
public final class PriorityListPlugin extends Plugin {
    @Override
    public List<DataType> getDataTypes() {
        return Arrays.asList(SendablePriorityListType.Instance);
    }

    @Override
    public List<ComponentType> getComponents() {
        return Arrays.asList(WidgetType.forAnnotatedWidget(PriorityListWidget.class));
    }

    @Override
    public Map<DataType, ComponentType> getDefaultComponents() {
        return ImmutableMap.of(SendablePriorityListType.Instance, WidgetType.forAnnotatedWidget(PriorityListWidget.class));
    }
}
