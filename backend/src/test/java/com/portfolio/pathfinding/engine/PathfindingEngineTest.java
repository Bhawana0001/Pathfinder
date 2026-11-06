package com.portfolio.pathfinding.engine;
import com.portfolio.pathfinding.api.RouteRequest;
import com.portfolio.pathfinding.api.RouteRequest.Algorithm;
import com.portfolio.pathfinding.api.RouteRequest.Cell;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class PathfindingEngineTest {
 private final PathfindingEngine engine=new PathfindingEngine();
 @Test void bfsFindsShortestPath(){var response=engine.solve(new RouteRequest(4,4,new Cell(0,0),new Cell(3,3),List.of(),Algorithm.BFS));assertTrue(response.found());assertEquals(6,response.pathLength());}
 @Test void astarAvoidsWalls(){var response=engine.solve(new RouteRequest(3,3,new Cell(0,0),new Cell(0,2),List.of(new Cell(0,1)),Algorithm.ASTAR));assertTrue(response.found());assertEquals(4,response.pathLength());}
 @Test void reportsUnreachable(){var response=engine.solve(new RouteRequest(3,3,new Cell(0,0),new Cell(2,2),List.of(new Cell(0,1),new Cell(1,0)),Algorithm.DIJKSTRA));assertFalse(response.found());assertTrue(response.path().isEmpty());}
}
