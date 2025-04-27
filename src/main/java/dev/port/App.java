package dev.port;

import dev.port.model.Yard;
import dev.port.service.PlacementService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@SpringBootApplication
@RestController
public class App {
    private final PlacementService service = new PlacementService();
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    @PostMapping("/pickSpot")
    public ResponseEntity<?> pickSpot(@RequestBody Map<String, Object> request) {
        try {
            Yard.Result result = service.process(request);
            return result.targetX() != null 
                ? ResponseEntity.ok(Map.of(
                    "containerId", result.containerId(), 
                    "targetX", result.targetX(), 
                    "targetY", result.targetY()))
                : ResponseEntity.badRequest().body(Map.of("error", "no suitable slot"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}