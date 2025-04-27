package dev.port.model;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Yard {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final int INVALID = 10_000;
    
    public record Container(String id, String size, boolean needsCold, int x, int y) {
        public static Container from(Map<String, Object> map) {
            return new Container(
                (String) map.get("id"),
                (String) map.get("size"),
                Boolean.TRUE.equals(map.get("needsCold")),
                ((Number) map.get("x")).intValue(),
                ((Number) map.get("y")).intValue()
            );
        }
    }
    
    public record Slot(int x, int y, String sizeCap, boolean hasColdUnit, boolean occupied) {
        public static Slot from(Map<String, Object> map) {
            return new Slot(
                ((Number) map.get("x")).intValue(),
                ((Number) map.get("y")).intValue(),
                (String) map.get("sizeCap"),
                Boolean.TRUE.equals(map.get("hasColdUnit")),
                Boolean.TRUE.equals(map.get("occupied"))
            );
        }
    }
    
    public record Result(String containerId, Integer targetX, Integer targetY) {
        public static Result success(String id, int x, int y) {
            return new Result(id, x, y);
        }
        
        public static Result error() {
            return new Result(null, null, null);
        }
    }
    
    public static int score(Container c, Slot s) {
        int distance = Math.abs(c.x - s.x) + Math.abs(c.y - s.y);
        int sizePenalty = c.size.equals("big") && s.sizeCap.equals("small") ? INVALID : 0;
        int coldPenalty = c.needsCold && !s.hasColdUnit ? INVALID : 0;
        int occupiedPenalty = s.occupied ? INVALID : 0;
        
        return distance + sizePenalty + coldPenalty + occupiedPenalty;
    }
}