package com.portfolio.pathfinding.api;

import com.portfolio.pathfinding.engine.PathfindingEngine;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/routes")
@CrossOrigin(origins = "http://localhost:5173")
public class RouteController {

    private final PathfindingEngine engine;

    public RouteController(PathfindingEngine engine) {
        this.engine = engine;
    }

    @PostMapping("/compute")
    public RouteResponse compute(@Valid @RequestBody RouteRequest request) {
        return engine.solve(request);
    }
}
