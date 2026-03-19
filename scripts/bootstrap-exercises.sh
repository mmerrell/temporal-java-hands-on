#!/bin/bash
# scripts/bootstrap-exercises.sh
#
# Populates exercises/ from a workshop source repo.
# Run once per source repo, then commit exercises/ so the Dockerfile can COPY it in.
#
# Usage:
#   ./scripts/bootstrap-exercises.sh <path-to-workshop-repo> [exercise-dir ...]
#
# Examples:
#   # Pull all four Coupang exercises (default when no exercises specified)
#   ./scripts/bootstrap-exercises.sh ~/Projects/coupang-mountainview-mar-2026
#
#   # Pull specific exercises only
#   ./scripts/bootstrap-exercises.sh ~/Projects/coupang-mountainview-mar-2026 1_converting 2_child_workflows
#
#   # Pull from a different workshop repo entirely
#   ./scripts/bootstrap-exercises.sh ~/Projects/apollo-workshop-apr-2026
#
# The script discovers exercises automatically: any top-level directory in the
# source repo that contains both a practice/ and solution/ subdirectory is
# treated as an exercise. You can override this by listing exercises explicitly.

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DEST="$REPO_ROOT/exercises"

# ─── Usage / arg parsing ──────────────────────────────────────────────────────
if [[ $# -lt 1 ]]; then
  echo "Usage: $0 <path-to-workshop-repo> [exercise-dir ...]"
  echo ""
  echo "Examples:"
  echo "  $0 ~/Projects/coupang-mountainview-mar-2026"
  echo "  $0 ~/Projects/coupang-mountainview-mar-2026 1_converting 2_child_workflows"
  echo "  $0 ~/Projects/some-other-workshop"
  exit 1
fi

SOURCE_REPO="${1%/}"   # strip trailing slash if present
shift                  # remaining args (if any) are explicit exercise names

if [[ ! -d "$SOURCE_REPO" ]]; then
  echo "ERROR: Source repo not found: $SOURCE_REPO"
  exit 1
fi

# ─── Discover or accept explicit exercise list ────────────────────────────────
if [[ $# -gt 0 ]]; then
  # Explicit list provided on command line
  EXERCISES=("$@")
  echo "Using explicit exercise list: ${EXERCISES[*]}"
else
  # Auto-discover: any directory with both practice/ and solution/ subdirs
  EXERCISES=()
  while IFS= read -r dir; do
    name="$(basename "$dir")"
    if [[ -d "$dir/practice" && -d "$dir/solution" ]]; then
      EXERCISES+=("$name")
    fi
  done < <(find "$SOURCE_REPO" -maxdepth 1 -mindepth 1 -type d | sort)

  if [[ ${#EXERCISES[@]} -eq 0 ]]; then
    echo "ERROR: No directories with both practice/ and solution/ found in $SOURCE_REPO"
    exit 1
  fi
  echo "Auto-discovered exercises: ${EXERCISES[*]}"
fi

# ─── Validate all exercises exist before touching anything ────────────────────
for exercise in "${EXERCISES[@]}"; do
  if [[ ! -d "$SOURCE_REPO/$exercise" ]]; then
    echo "ERROR: Exercise directory not found: $SOURCE_REPO/$exercise"
    exit 1
  fi
done

# ─── Copy ─────────────────────────────────────────────────────────────────────
mkdir -p "$DEST"

for exercise in "${EXERCISES[@]}"; do
  echo "Copying $exercise..."
  cp -r "$SOURCE_REPO/$exercise" "$DEST/"
done

# Remove any compiled artifacts that crept in
find "$DEST" -type d -name target -exec rm -rf {} + 2>/dev/null || true

# ─── Summary ──────────────────────────────────────────────────────────────────
echo ""
echo "Done. exercises/ now contains:"
ls "$DEST"
echo ""
echo "Next steps:"
echo "  git add exercises/ && git commit -m 'Add exercises from $(basename "$SOURCE_REPO")'"
echo "  git push origin main  # triggers Docker image rebuild"
