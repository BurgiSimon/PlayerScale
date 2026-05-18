# PlayerScale

A client-side Fabric mod for Minecraft 1.21.11 that lets you visually resize player models. The scaling is purely cosmetic and only visible to you - other players are not affected.
This was created since i used a similar feature when playing Hypixel Skyblock with the Skytils mod.

## Features

- Scale your own player model and other players' models (0.1x - 10x)
- Smooth scale transitions with linear interpolation
- Toggle keybind to quickly switch your scale on/off (unbound by default)
- Config GUI with logarithmic sliders and quick presets (Tiny, Normal, Large, Giant)
- Toggle crosshair visibility in 3rd person view
- Configurable keybind to open the config screen (unbound by default)
- Player name tab-completion in commands
- Settings persist across restarts (saved to config file)
- [Mod Menu](https://modrinth.com/mod/modmenu) integration - access settings from the mod list
- Client-side only - no server installation needed
- Works in singleplayer and multiplayer

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.18.0+
- Fabric API
- [Mod Menu](https://modrinth.com/mod/modmenu) (optional, for config button in mod list)

## Usage

### Config GUI

Open the settings screen using any of these methods:

- `/playerscale` command
- Keybind (set one in Options -> Controls -> PlayerScale, or in the mod's config screen)
- Mod Menu -> PlayerScale -> Config button

### Commands

```
/playerscale                         Open the config GUI
/playerscale set <player> <scale>    Set a player's visual scale (0.1 - 10.0)
/playerscale reset <player>          Reset a player to normal size
/playerscale resetall                Reset all players
```

## Building

```
./gradlew build
```

The mod jar will be in `build/libs/`.

## Support

If you enjoy this mod, consider supporting me on [Ko-fi](https://ko-fi.com/chliburgi)!
