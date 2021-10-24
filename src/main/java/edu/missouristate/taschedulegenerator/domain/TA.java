package edu.missouristate.taschedulegenerator.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TA {
    private String name;
    private boolean isGA;
    private int maxHours;
    private List<TimeBlock> notAvailable = new ArrayList<TimeBlock>();
}
