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
    public String name;
    public boolean isGA;
    public int maxHours;
    public List<TimeBlock> notAvailable = new ArrayList<>();
}
