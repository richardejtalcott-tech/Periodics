# Periodic v5 Visual Acceptance Gate

This branch must not be merged or released until emulator screenshots pass every item below.

## Reference direction
- Dark, premium science-museum atmosphere.
- High-detail, polished presentation matching the approved concept mockups.
- Readability takes priority over dramatic perspective.
- No reuse of the rejected v4 magnifying-glass layout.

## Splash screen
- Oxygen atom is the visual focus.
- Compact nucleus; clearly visible electrons.
- Multiple elliptical orbital paths with continuous motion.
- Rich depth, glow, particles, and restrained dark palette.
- Title and subtitle centered and fully readable.
- No empty placeholder appearance.

## Periodic table
- Straight, front-facing default view.
- Zero permanent X/Y tilt.
- No free rotation gesture.
- Pan and zoom only.
- All 118 tiles readable at the default overview.
- Family colors are consistent and intentional.
- Tiles have dimensional depth without skewing symbols or numbers.
- Lanthanides and actinides are aligned and labeled clearly.
- No clipping, overlap, upside-down text, or excessive empty space.

## Element screen
- New approved-style atom explorer, not the old v4 magnifying-glass chain.
- Compact rotating nucleus and visible oval orbital paths.
- Correct symbol, atomic number, proton, neutron, electron, and shell values.
- Element-specific common bonds.
- Balanced information hierarchy with no crowded text.
- All labels upright and inside the safe display area.
- Premium glass/metal/science-museum styling consistent with the table.

## Automatic rejection conditions
- Any permanent table tilt.
- Any upside-down or clipped text.
- Any old magnifying-glass interface.
- Any 28 KB or otherwise invalid APK artifact.
- APK missing from expected Gradle output.
- Screenshots not produced for splash, table, and element detail.
- Placeholder-looking UI or materially lower detail than the approved mockups.

## Release rule
The branch remains unmerged until screenshots are inspected against the approved mockups and explicitly accepted.
