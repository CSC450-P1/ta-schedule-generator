package edu.missouristate.taschedulegenerator.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TA {
    private String name;
    private boolean isGA;
    private int maxHours;
    private List<TimeBlock> notAvailable = new ArrayList<TimeBlock>();
}
