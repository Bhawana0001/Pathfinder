# Pathfinding & Route Optimization Platform — MVP starter

A portfolio-oriented, modular-monolith starter. React/TypeScript renders an editable 16×24 grid; Java 21/Spring Boot calculates routes using BFS, DFS, Dijkstra, or A*.

## Requirements
- Java 21, Maven 3.9+
- Node.js 22.12+ and npm

## Run (two terminals)
```bash
cd backend
mvn spring-boot:run
```
```bash
cd frontend
npm install
npm run dev
```
Open http://localhost:5173. The Vite dev server forwards `/api` requests to http://localhost:8080.

## Test backend
```bash
cd backend
mvn test
```

## API contract
`POST http://localhost:8080/api/v1/routes/compute`
```json
{"rows":3,"cols":3,"start":{"row":0,"col":0},"end":{"row":2,"col":2},"obstacles":[{"row":1,"col":1}],"algorithm":"ASTAR"}
```
`algorithm` can be `BFS`, `DFS`, `DIJKSTRA`, or `ASTAR`. Coordinates are zero-based, 4-directional, uniform-cost. Response returns `found`, `path`, `explored`, `pathLength`, `nodesExplored`, `durationNanos`, and `algorithm`.

## Decisions
- Start with a single Spring Boot service, not microservices.
- Keep the algorithm engine separate from the controller, no database needed to compute routes.
- Use 4-direction uniform-cost movement for MVP: BFS, Dijkstra, A* all find shortest paths; DFS does not promise shortest path.
- The `durationNanos` metric measures engine execution, **not** end-to-end API latency.
- Obstacles limited to 60×60 grids by validation; avoid premature scaling and resource abuse.
- PostgreSQL comes with saved scenarios; Redis comes with measurable repeated computation benefits; Docker follows once both services are stable.

## Next tasks
1. Add step-by-step playback (use `explored` order).
2. Move existing visualizer styling/maze-generation logic into React components.
3. Add weighted terrain with weighted Dijkstra/A*, tests and clear BFS restrictions.
4. Add PostgreSQL persistence for named scenarios with migration scripts.
5. Add benchmark harness and fair comparisons (warm-up, repeated runs, p95).
6. Add Redis after measuring cache hit rate; add Docker and CI.

This is starter code, not production hardened: authentication, persistent storage, worker queues and distributed deployment are deliberately deferred.
