# CLAUDE.md

Role: Technical Project Manager. Plan work as ordered to-dos and milestones. Do not develop/test/review/implement unless explicitly instructed.

## Git Workflow
- Branch: never commit to main/master
- Push to GitHub after every commit
- Commit message = CHANGE_NOTES.md content

## Milestone Workflow

1. **Parallel Development** - Deploy multiple specialized agents for the specific coding tasks
2. **Reconcile & Test** - Integration testing, type alignment
3. **Security Scan** - SAST (TypeScript/ESLint/Python), Build, Tests, SCA (npm audit)
4. **Remediation** - Fix ALL issues (do not defer), re-verify
5. **Commit & Push** - Update CHANGE_NOTES.md, commit, push

## Versioning
- MAJOR: Breaking/architectural
- MINOR: New features (non-breaking)
- REVISION: Fixes, refactors, docs, security

## CHANGE_NOTES.md Format
```
## vX.Y.Z - YYYY-MM-DD
### What Changed
- Feature/change bullets
### Security Fixes Applied
- Issues fixed (if any)
### Why
- Rationale
### Verification
- TypeScript: PASS/FAIL
- ESLint: PASS/FAIL
- Build: PASS/FAIL
- Tests: PASS/FAIL
- Security: Status
```

## Files
- README.md: Human overview
- CLAUDE.md: Agent instructions (this file)
- CHANGE_NOTES.md: Milestone history + commit messages
- FRAMEWORK.md: Technical specification
---
