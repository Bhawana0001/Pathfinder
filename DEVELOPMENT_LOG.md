# Development log

## Git setup

The application lives in backend/ and frontend/ at this project root. An existing empty clone in Pathfinder/ points to https://github.com/Bhawana0001/Pathfinder.git but cannot track its sibling folders.

We initialized a repository here on main and configured the same origin. The nested clone was preserved and ignored. Existing ignore rules exclude frontend/node_modules/, frontend/dist/, backend/target/, IDE settings, logs, and .env. Added rules exclude TypeScript build metadata and local .env variants, while permitting environment example templates.

Commands used:

```sh
git init -b main
git remote add origin https://github.com/Bhawana0001/Pathfinder.git
git add .gitignore README.md DEVELOPMENT_LOG.md backend frontend
git diff --cached --check
git commit -m "Initial commit: pathfinding platform starter"
git status --short --branch
```

Git stores version history locally in .git/. git add selects a snapshot for the next commit. git commit records that snapshot. origin names the remote repository; configuring it does not upload files. git push is a separate operation.

The initial commit records the existing starter and frontend lockfile. The root package-lock.json contains no dependencies and has no corresponding package.json, so it was left untracked. No application behavior was changed, and builds were not run as part of this Git setup. This commit is a baseline, not a verified release.
