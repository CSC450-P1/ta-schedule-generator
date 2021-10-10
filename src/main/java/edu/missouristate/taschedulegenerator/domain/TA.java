package edu.missouristate.taschedulegenerator.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TA {
    private String name;
    private boolean isGA;
    private int maxHours;
    private List<TimeBlock> notAvailable = new ArrayList<>();
}
