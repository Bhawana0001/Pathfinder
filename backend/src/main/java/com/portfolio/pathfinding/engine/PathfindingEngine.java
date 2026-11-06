package com.portfolio.pathfinding.engine;

import com.portfolio.pathfinding.api.RouteRequest;
import com.portfolio.pathfinding.api.RouteRequest.Cell;
import com.portfolio.pathfinding.api.RouteResponse;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PathfindingEngine {

    private record State(Cell cell, int priority) {}

    private static final int[][] DIRECTIONS = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };

    public RouteResponse solve(RouteRequest request) {
        validate(request);
        long startTime = System.nanoTime();
        Set<Cell> blocked = new HashSet<>(request.obstacles());
        Map<Cell, Cell> parent = new HashMap<>();
        Map<Cell, Integer> distance = new HashMap<>();
        Set<Cell> visited = new HashSet<>();
        List<Cell> explored = new ArrayList<>();
        var algorithm = request.algorithm();
        if (algorithm == RouteRequest.Algorithm.BFS || algorithm == RouteRequest.Algorithm.DFS) {
            Deque<Cell> fringe = new ArrayDeque<>();
            fringe.add(request.start());
            visited.add(request.start());
            while (!fringe.isEmpty()) {
                Cell current =
                    algorithm == RouteRequest.Algorithm.BFS
                        ? fringe.removeFirst()
                        : fringe.removeLast();
                explored.add(current);
                if (current.equals(request.end())) break;
                for (Cell next : neighbors(current, request.rows(), request.cols())) {
                    if (!blocked.contains(next) && visited.add(next)) {
                        parent.put(next, current);
                        fringe.addLast(next);
                    }
                }
            }
        } else {
            PriorityQueue<State> queue = new PriorityQueue<>(
                Comparator.comparingInt(State::priority)
            );
            distance.put(request.start(), 0);
            queue.add(
                new State(request.start(), heuristic(request.start(), request.end(), algorithm))
            );
            while (!queue.isEmpty()) {
                Cell current = queue.poll().cell();
                if (!visited.add(current)) continue;
                explored.add(current);
                if (current.equals(request.end())) break;
                for (Cell next : neighbors(current, request.rows(), request.cols())) {
                    if (blocked.contains(next) || visited.contains(next)) continue;
                    int newCost = distance.get(current) + 1;
                    if (newCost < distance.getOrDefault(next, Integer.MAX_VALUE)) {
                        distance.put(next, newCost);
                        parent.put(next, current);
                        queue.add(
                            new State(next, newCost + heuristic(next, request.end(), algorithm))
                        );
                    }
                }
            }
        }
        boolean found = visited.contains(request.end());
        List<Cell> path = new ArrayList<>();
        if (found) {
            for (Cell at = request.end(); at != null; at = parent.get(at)) path.add(at);
            Collections.reverse(path);
        }
        return new RouteResponse(
            found,
            path,
            explored,
            Math.max(0, path.size() - 1),
            explored.size(),
            System.nanoTime() - startTime,
            algorithm.name()
        );
    }

    private static int heuristic(Cell a, Cell b, RouteRequest.Algorithm algorithm) {
        return algorithm == RouteRequest.Algorithm.ASTAR
            ? Math.abs(a.row() - b.row()) + Math.abs(a.col() - b.col())
            : 0;
    }

    private static List<Cell> neighbors(Cell c, int rows, int cols) {
        List<Cell> result = new ArrayList<>(4);
        for (int[] d : DIRECTIONS) {
            int r = c.row() + d[0],
                col = c.col() + d[1];
            if (r >= 0 && r < rows && col >= 0 && col < cols) result.add(new Cell(r, col));
        }
        return result;
    }

    private static void validate(RouteRequest r) {
        if (
            r.start().row() >= r.rows() ||
            r.start().col() >= r.cols() ||
            r.end().row() >= r.rows() ||
            r.end().col() >= r.cols()
        ) bad("Start or end is outside the grid");
        Set<Cell> unique = new HashSet<>(r.obstacles());
        for (Cell c : unique)
            if (c.row() >= r.rows() || c.col() >= r.cols()) bad("Obstacle outside the grid");
        if (unique.contains(r.start()) || unique.contains(r.end())) bad(
            "Start/end cannot be obstacles"
        );
    }

    private static void bad(String message) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
