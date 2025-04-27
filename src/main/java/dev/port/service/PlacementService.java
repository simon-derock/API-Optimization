package dev.port.service;

import dev.port.model.Yard;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class PlacementService {
    
    public Yard.Result process(Map<String, Object> request) {
        // Parse container
        Yard.Container container = Yard.Container.from((Map<String, Object>) request.get("container"));
        
        // Parse yard map
        List<Map<String, Object>> rawSlots = (List<Map<String, Object>>) request.get("yardMap");
        List<Yard.Slot> slots = new ArrayList<>(rawSlots.size());
        for (Map<String, Object> raw : rawSlots) {
            slots.add(Yard.Slot.from(raw));
        }
        
        // Find best slot
        Yard.Slot best = null;
        int bestScore = Integer.MAX_VALUE;
        
        for (Yard.Slot slot : slots) {
            int score = Yard.score(container, slot);
            if (score < bestScore) {
                bestScore = score;
                best = slot;
            }
        }
        
        // Return result
        return bestScore >= 10_000 
            ? Yard.Result.error() 
            : Yard.Result.success(container.id(), best.x(), best.y());
    }
}