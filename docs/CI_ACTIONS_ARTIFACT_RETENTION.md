# GitHub Actions artifact storage

Production source and puzzle saves are independent of workflow artifacts.

- Routine Android QA APK: **3 days** on main and the current WS-16 development branch.
- WS-16 art/release-isolation preview artifact: **3 days** on the current development branch.
- Keep a verified milestone APK elsewhere before its Actions artifact expires.
- Historical artifacts are **not** retroactively shortened.

To reclaim old storage, use [Manage old QA artifacts (manual)](../../actions/workflows/manage-qa-artifact-storage.yml). Choose **dry-run** to inspect candidates, then **delete-obsolete** only if the reported candidates are expendable. Cleanup excludes recent artifacts, the newest ten, signed/pinned/certificate/rollback/release items and at least one of each normalized artifact type.

Account-level GitHub Actions spending budgets are managed by the account owner under Settings → Billing → Budgets and alerts. Repository workflows cannot configure billing limits.
