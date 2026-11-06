package com.portfolio.pathfinding.api;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record RouteRequest(
 @Min(2) @Max(60) int rows,
 @Min(2) @Max(60) int cols,
 @NotNull @Valid Cell start,
 @NotNull @Valid Cell end,
 @NotNull List<@Valid Cell> obstacles,
 @NotNull Algorithm algorithm
) {
 public record Cell(@Min(0) int row, @Min(0) int col) {}
 public enum Algorithm { BFS, DFS, DIJKSTRA, ASTAR }
}
