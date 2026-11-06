import { useState } from 'react';
type Cell = { row: number; col: number };
type Algorithm = 'BFS' | 'DFS' | 'DIJKSTRA' | 'ASTAR';
type Mode = 'wall' | 'start' | 'end';
type RouteResponse = {
  found: boolean;
  path: Cell[];
  explored: Cell[];
  pathLength: number;
  nodesExplored: number;
  durationNanos: number;
  algorithm: string;
};
const ROWS = 16,
  COLS = 24;
const key = (c: Cell) => `${c.row}:${c.col}`;
export default function App() {
  const [start, setStart] = useState<Cell>({ row: 7, col: 3 });
  const [end, setEnd] = useState<Cell>({ row: 7, col: 20 });
  const [walls, setWalls] = useState<Set<string>>(new Set());
  const [algorithm, setAlgorithm] = useState<Algorithm>('ASTAR');
  const [mode, setMode] = useState<Mode>('wall');
  const [result, setResult] = useState<RouteResponse | null>(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const path = new Set(result?.path.map(key) ?? []),
    explored = new Set(result?.explored.map(key) ?? []);

  function edit(cell: Cell) {
    setResult(null);
    setError('');
    if (mode === 'start') {
      if (key(cell) !== key(end)) {
        setStart(cell);
        setWalls((w) => {
          const next = new Set(w);
          next.delete(key(cell));
          return next;
        });
      }
    } else if (mode === 'end') {
      if (key(cell) !== key(start)) {
        setEnd(cell);
        setWalls((w) => {
          const next = new Set(w);
          next.delete(key(cell));
          return next;
        });
      }
    } else if (key(cell) !== key(start) && key(cell) !== key(end)) {
      setWalls((w) => {
        const next = new Set(w);
        next.has(key(cell)) ? next.delete(key(cell)) : next.add(key(cell));
        return next;
      });
    }
  }

  async function run() {
    setBusy(true);
    setError('');
    setResult(null);
    try {
      const obstacles = [...walls].map((v) => {
        const [row, col] = v.split(':').map(Number);
        return { row, col };
      });
      const response = await fetch('/api/v1/routes/compute', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ rows: ROWS, cols: COLS, start, end, obstacles, algorithm }),
      });
      if (!response.ok) throw new Error(`API error ${response.status}`);
      setResult(await response.json());
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Unknown error');
    } finally {
      setBusy(false);
    }
  }
  return (
    <main>
      <header>
        <div className="eyebrow">ALGORITHM ENGINEERING LAB</div>
        <h1>Pathfinding & Route Optimization</h1>
        <p>Design a grid, place obstacles, and compute routes through a Java Spring Boot engine.</p>
      </header>
      <section className="toolbar">
        <label>
          Algorithm{' '}
          <select
            value={algorithm}
            onChange={(e) => {
              setAlgorithm(e.target.value as Algorithm);
              setResult(null);
            }}
          >
            <option value="ASTAR">A* Search</option>
            <option value="DIJKSTRA">Dijkstra</option>
            <option value="BFS">Breadth-first search</option>
            <option value="DFS">Depth-first search</option>
          </select>
        </label>
        <label>
          Edit grid{' '}
          <select value={mode} onChange={(e) => setMode(e.target.value as Mode)}>
            <option value="wall">Toggle obstacle</option>
            <option value="start">Move start</option>
            <option value="end">Move destination</option>
          </select>
        </label>
        <button onClick={run} disabled={busy}>
          {busy ? 'Computing...' : 'Find route'}
        </button>
        <button
          className="secondary"
          onClick={() => {
            setWalls(new Set());
            setResult(null);
            setError('');
          }}
        >
          Clear obstacles
        </button>
      </section>
      <section className="panel">
        <div className="legend">
          <span>🟢 Start</span>
          <span>🔴 End</span>
          <span>⬛ Obstacle</span>
          <span>🟦 Explored</span>
          <span>🟨 Route</span>
        </div>
        <div className="grid" style={{ gridTemplateColumns: `repeat(${COLS}, minmax(0,1fr))` }}>
          {Array.from({ length: ROWS * COLS }, (_, n) => {
            const cell = { row: Math.floor(n / COLS), col: n % COLS };
            const k = key(cell);
            let cls = 'cell';
            if (walls.has(k)) cls += ' wall';
            if (explored.has(k)) cls += ' visited';
            if (path.has(k)) cls += ' path';
            if (k === key(start)) cls += ' start';
            if (k === key(end)) cls += ' end';
            return (
              <button
                key={k}
                className={cls}
                aria-label={`Row ${cell.row + 1} column ${cell.col + 1}`}
                title={k}
                onClick={() => edit(cell)}
              />
            );
          })}
        </div>
        <div className="metrics">
          {result ? (
            <>
              <strong>{result.found ? 'Route found' : 'No route exists'}</strong>
              <span>Path length: {result.pathLength} steps</span>
              <span>Explored: {result.nodesExplored} cells</span>
              <span>Engine runtime: {(result.durationNanos / 1_000_000).toFixed(3)} ms</span>
            </>
          ) : (
            <span>Select a tool and click grid cells. Then run an algorithm.</span>
          )}
          {error && <span className="error">{error} — is Spring Boot running on port 8080?</span>}
        </div>
      </section>
      <footer>
        Phase 1 · Uniform-cost 4-directional grids · Weighted terrain and animation planned next
      </footer>
    </main>
  );
}
