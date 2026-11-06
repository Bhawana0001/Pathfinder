package com.portfolio.pathfinding.api;

import java.util.List;

public record RouteResponse(
    boolean found,
    List<RouteRequest.Cell> path,
    List<RouteRequest.Cell> explored,
    int pathLength,
    int nodesExplored,
    long durationNanos,
    String algorithm
) {}
