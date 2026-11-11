# WS16AS — import the five *original* bookcase PNGs

This folder is intentionally missing the actual art until the real five original image files are transferred. **Do not substitute crops of old bookcase screens, recreate artwork, use decorative proof shelves, or copy a flattened screenshot.**

Source: `WS16AS_Modular_Library_Art_Kit.zip`, `assets/shell/` inside that ZIP. Upload these exact PNGs into this folder on the draft development branch:

- `top_crown.png` SHA256 `c4365f20bf839e504cd5828fdacf2f89187a4770c72911e955de1a7b3ccebc63`
- `left_pilaster.png` SHA256 `caea09cae3e4114b99fa6fd7c8b9a3d736471b389864b9efb28b8c2f58d21dd9`
- `recessed_backboard.png` SHA256 `6d63a53556cbc8ae7059418d8210ca79796becfa7e8642b217debca696c17c64`
- `right_pilaster.png` SHA256 `843346dca3c6f711bd5d2d55076e664bcece16131a17efbffb27d996434a9e98`
- `bottom_base.png` SHA256 `1192023c1f02efd042a4e75fc5d3ec56a45bdff6f573c741a0a0f0b0112578a2`

Each file must retain original name, dimensions, pixel data, and byte hash. Debug only; these are unapproved visual candidates.

The existing `WS16ATModularLibraryScene.kt` checks for these exact five PNG filenames; it hides the modular QA option while any are missing. Once all five are uploaded, the existing QA option can render the new architecture while keeping the prior Library available for rollback. After import, verify SHA256s from the source kit, rerun both Android and art-release workflows and test on physical Android before approval.

This README creates a stable upload location; **it does not contain art itself and does not enable the scene**.
