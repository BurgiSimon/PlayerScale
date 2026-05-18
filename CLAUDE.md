# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew build          # Build the mod (output: build/libs/)
./gradlew runClient      # Launch Minecraft with the mod loaded
```

No test suite exists. Verification is done by running the client in-game.

## Project Overview

PlayerScale is a client-side Fabric mod for Minecraft 1.21.11 that visually scales player models (0.1x-10x). Scaling is cosmetic and local only. Built with Java 21, Fabric Loader 0.19.2, Fabric API, and Loom 1.16.2.

## Architecture

All mod code lives under `src/client/java/dev/solerbus/playerscale/` (Loom split source sets - client only).

**Data flow for scaling:**
1. `PlayerEntityRendererUpdateMixin` injects at TAIL of `updateRenderState` to map each player's entity ID to their UUID, and identifies the local player
2. `PlayerEntityRendererMixin` injects at TAIL of `scale` to apply `matrices.scale(s, s, s)` using the scale looked up from `ScaleManager`
3. `ScaleManager.getScaleByEntityId(int)` resolves: local player -> `selfScale`, per-player UUID override -> `SCALES` map, everyone else -> `othersScale`

**Key components:**
- `ScaleManager` - Static singleton holding all scale state (selfScale, othersScale, crosshair toggle, per-player UUID map, entityId-to-UUID bridge). Thread-safe via ConcurrentHashMap.
- `ScaleConfig` - JSON config persistence using Gson. Saves to `.minecraft/config/playerscale.json`. Loads on init, saves on screen close and command execution.
- `PlayerScaleMod` - Client entrypoint. Registers keybind (unbound by default), commands, and loads config.
- `PlayerScaleScreen` - Config GUI with sliders, preset buttons, keybind config, and crosshair toggle.
- `InGameHudMixin` - Redirects `isFirstPerson()` in `renderCrosshair` to show crosshair in 3rd person when toggled.
- `ModMenuIntegration` - Optional Mod Menu config screen factory.

## Minecraft 1.21.11 API Notes

These differ from older versions and are easy to get wrong:

- `PlayerEntityRenderer` is generic: `PlayerEntityRenderer<AvatarlikeEntity extends PlayerLikeEntity & ClientPlayerLikeEntity>`. Mixin method parameters erase to `PlayerLikeEntity`, NOT `LivingEntity`.
- `PlayerEntityRenderState` has `id` (int entity ID) but no UUID. The UUID-to-entityId bridge in `ScaleManager` works around this.
- `GameProfile.id()` not `getId()` (authlib 7.0.61 record-style accessor).
- `Screen.keyPressed(KeyInput)` and `mouseClicked(Click, boolean)` use record types, not individual int/double parameters.
- `KeyBinding.Category` is a record created via `KeyBinding.Category.create(Identifier)`, not a string.

## Conventions

- Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
- No co-authored-by lines in commits
- NEVER use em dash or en dash characters anywhere - not in code, comments, markdown, commits, release notes, titles, or any other text. Always use a regular dash (-) instead.
