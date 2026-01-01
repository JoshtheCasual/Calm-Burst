#!/bin/bash
# GitHub Actions Build Monitor
# Checks the status of the latest workflow run

echo "=========================================="
echo "Calm Burst - Build Monitor"
echo "=========================================="
echo ""

REPO="JoshtheCasual/Calm-Burst"
BRANCH="claude/motivational-notification-app-73f0U"

echo "Repository: $REPO"
echo "Branch: $BRANCH"
echo ""
echo "GitHub Actions URL:"
echo "https://github.com/$REPO/actions"
echo ""
echo "Direct workflow runs:"
echo "https://github.com/$REPO/actions/workflows/build-apk.yml"
echo ""
echo "To check build status manually:"
echo "1. Visit https://github.com/$REPO/actions"
echo "2. Look for the most recent 'Build Android APK' workflow"
echo "3. Click on it to see detailed logs"
echo ""
echo "Expected build time: 5-10 minutes"
echo ""
echo "If build succeeds, download APK from:"
echo "  Actions → Workflow run → Artifacts → calm-burst-debug-apk"
echo ""
