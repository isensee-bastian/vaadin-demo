package com.github.isenseebastian.vaadindemo.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@Route(value = "", layout = AuthenticatedLayout.class)
@PageTitle("Dashboard")
@PermitAll
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        // Add an example table for demo purposes.
        var grid = new Grid<>(Measurement.class, false);
        grid.addColumn(Measurement::sensor).setHeader("Sensor");
        grid.addColumn(Measurement::value).setHeader("Value");
        grid.addColumn(Measurement::status).setHeader("Unit");
        grid.addColumn(Measurement::status).setHeader("Status");

        List<Measurement> sample = List.of(
                new Measurement("temperature", 56, "Celsius", "normal"),
                new Measurement("volume", 10, "Decibel", "excellent"),
                new Measurement("load", 95, "Percent", "warning")
        );
        grid.setItems(sample);

        add(grid);
    }

    private record Measurement(String sensor, int value, String unit, String status) {
    }
}
