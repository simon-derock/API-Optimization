package dev.port;

import dev.port.model.Yard;
import dev.port.service.PlacementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

class YardTest {
    private final PlacementService service = new PlacementService();

    @Test
    @DisplayName("Basic placement - closest available slot")
    void testBasicPlacement() {
        Map<String, Object> request = Map.of(
            "container", Map.of(
                "id", "C1",
                "size", "small",
                "needsCold", false,
                "x", 1,
                "y", 1
            ),
            "yardMap", List.of(
                Map.of("x", 1, "y", 2, "sizeCap", "small", "hasColdUnit", false, "occupied", false),
                Map.of("x", 2, "y", 2, "sizeCap", "big", "hasColdUnit", true, "occupied", false)
            )
        );
        
        Yard.Result result = service.process(request);
        assertEquals("C1", result.containerId());
        assertEquals(1, result.targetX());
        assertEquals(2, result.targetY());
    }
    
    @Test
    @DisplayName("No suitable slot - all slots too small")
    void testNoSuitableSlot() {
        Map<String, Object> request = Map.of(
            "container", Map.of(
                "id", "C1",
                "size", "big",
                "needsCold", true,
                "x", 1,
                "y", 1
            ),
            "yardMap", List.of(
                Map.of("x", 1, "y", 2, "sizeCap", "small", "hasColdUnit", false, "occupied", false),
                Map.of("x", 2, "y", 2, "sizeCap", "small", "hasColdUnit", true, "occupied", false)
            )
        );
        
        Yard.Result result = service.process(request);
        assertNull(result.targetX());
        assertNull(result.targetY());
    }

    @Test
    @DisplayName("Cold storage requirement test")
    void testColdStorageRequirement() {
        Map<String, Object> request = Map.of(
            "container", Map.of(
                "id", "C2",
                "size", "small",
                "needsCold", true,
                "x", 1,
                "y", 1
            ),
            "yardMap", List.of(
                Map.of("x", 1, "y", 2, "sizeCap", "small", "hasColdUnit", false, "occupied", false),
                Map.of("x", 2, "y", 2, "sizeCap", "small", "hasColdUnit", true, "occupied", false)
            )
        );
        
        Yard.Result result = service.process(request);
        assertEquals("C2", result.containerId());
        assertEquals(2, result.targetX());
        assertEquals(2, result.targetY());
    }

    @Test
    @DisplayName("Occupied slot avoidance test")
    void testOccupiedSlotAvoidance() {
        Map<String, Object> request = Map.of(
            "container", Map.of(
                "id", "C3",
                "size", "small",
                "needsCold", false,
                "x", 1,
                "y", 1
            ),
            "yardMap", List.of(
                Map.of("x", 1, "y", 2, "sizeCap", "small", "hasColdUnit", false, "occupied", true),
                Map.of("x", 2, "y", 2, "sizeCap", "small", "hasColdUnit", false, "occupied", false)
            )
        );
        
        Yard.Result result = service.process(request);
        assertEquals("C3", result.containerId());
        assertEquals(2, result.targetX());
        assertEquals(2, result.targetY());
    }

    @Test
    @DisplayName("Full yard test")
    void testFullYard() {
        Map<String, Object> request = Map.of(
            "container", Map.of(
                "id", "C4",
                "size", "small",
                "needsCold", false,
                "x", 1,
                "y", 1
            ),
            "yardMap", List.of(
                Map.of("x", 1, "y", 2, "sizeCap", "small", "hasColdUnit", false, "occupied", true),
                Map.of("x", 2, "y", 2, "sizeCap", "small", "hasColdUnit", false, "occupied", true)
            )
        );
        
        Yard.Result result = service.process(request);
        assertNull(result.targetX());
        assertNull(result.targetY());
    }

    @Test
    @DisplayName("Distance optimization test")
    void testDistanceOptimization() {
        Map<String, Object> request = Map.of(
            "container", Map.of(
                "id", "C5",
                "size", "small",
                "needsCold", false,
                "x", 1,
                "y", 1
            ),
            "yardMap", List.of(
                Map.of("x", 3, "y", 3, "sizeCap", "small", "hasColdUnit", false, "occupied", false),
                Map.of("x", 1, "y", 2, "sizeCap", "small", "hasColdUnit", false, "occupied", false)
            )
        );
        
        Yard.Result result = service.process(request);
        assertEquals("C5", result.containerId());
        assertEquals(1, result.targetX());
        assertEquals(2, result.targetY());
    }

    @Test
    @DisplayName("Big container in big slot test")
    void testBigContainerPlacement() {
        Map<String, Object> request = Map.of(
            "container", Map.of(
                "id", "C6",
                "size", "big",
                "needsCold", false,
                "x", 1,
                "y", 1
            ),
            "yardMap", List.of(
                Map.of("x", 1, "y", 2, "sizeCap", "small", "hasColdUnit", false, "occupied", false),
                Map.of("x", 2, "y", 2, "sizeCap", "big", "hasColdUnit", false, "occupied", false)
            )
        );
        
        Yard.Result result = service.process(request);
        assertEquals("C6", result.containerId());
        assertEquals(2, result.targetX());
        assertEquals(2, result.targetY());
    }
}