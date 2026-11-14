# WS16AS original modular Library source ZIP — one-file transfer point

**Upload exactly one existing source ZIP here** on the draft development branch:

`WS16AS_Modular_Library_Art_Kit.zip`

This is the original 5-piece conversation art kit, **not** the smaller five-PNG quick-upload ZIP. It includes:
- `original_candidate_sheet.png`
- `manifest.json` with SHA256 for the original and five separate assets
- `assets/shell/` containing crown, left/right pilasters, recessed wood back, and base
- movable proof assets and separate plaque candidates; these are NOT imported into the app

After this ZIP has been uploaded, Android/Art QA workflows will use `tools/asset_pipeline/import_ws16as_modular_library.py` to verify the pinned original source, exact five-part reconstruction, no baked shelves/books/placards, and extract **only** those five art PNGs into the QA APK. The new modular scene remains optional, and the previous Library is retained as a rollback.

Owner has NOT approved this scene on a physical phone. Keep this PR draft and do not promote to release. This README is not an art asset.
