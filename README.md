# Speed Tweaks

A Fabric mod for Minecraft Java Edition 1.20.1 with an in-game menu to set your **fly speed** and **minecart speed**, all the way up to "infinite".

## Install

1. Install [Minecraft Java Edition](https://www.minecraft.net/) 1.20.1 and run it once.
2. Install the [Fabric Loader](https://fabricmc.net/use/installer/) for 1.20.1 and pick the new Fabric profile in the launcher.
3. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 1.20.1 (it is a separate mod and is required).
4. Download [`dist/speedtweaks-1.2.0.jar`](dist/speedtweaks-1.2.0.jar) from this repo.
5. Put both jars (Fabric API and `speedtweaks-1.2.0.jar`) in your `mods` folder:
   - Windows: `%appdata%\.minecraft\mods`
   - macOS: `~/Library/Application Support/minecraft/mods`
   - Linux: `~/.minecraft/mods`
6. Launch the game using the Fabric profile.

## Usage

Press `]` in game to open the Speed Tweaks menu. You can rebind the key under **Options > Controls > Key Binds > Speed Tweaks**.

- **Fly Speed** slider: vanilla (0.05) up to 5.0.
- **Infinite Fly Speed** toggle: sets fly speed to 1000.
- **Minecart Speed** slider: vanilla (0.4) up to 10.
- **Infinite Minecart Speed** toggle: sets minecart speed to the mod's maximum (about 100 blocks per tick).
- **Keep Cart Speed** toggle: carts hold their speed forever, including around turns, with no powered rails or redstone torches needed. Give the cart a push to start it. An unpowered powered rail still stops it.
- **Real-Time Sunrise/Sunset** toggle: syncs Minecraft's day/night cycle to your computer's clock. Sunrise is at 6:00, noon at 12:00, sunset at 18:00 and midnight at 0:00, in your local time.

Settings are saved to `config/speedtweaks.json` and persist between sessions.

## Notes

- "Infinite" is a very large finite number. Real infinity would corrupt entity positions, and at these speeds you can outrun chunk loading and fall through the world.
- Fly speed only applies while you can already fly (creative or spectator mode). The mod does not grant flight.
- Minecarts follow the track at any speed, including corners. If the next chunk isn't loaded yet, the cart pauses until it loads rather than leaving the track. Detector and activator rails only trigger where the cart ends each tick.
- Minecarts are simulated by the server. Minecart speed works in singleplayer and LAN. On a dedicated server, the mod must be installed on the server too.
- Real-Time Sunrise/Sunset sets the time on the server (works in singleplayer and LAN) and also on your own screen, so on other servers the sky follows your clock but mobs and sleeping still use the server's time.
- Servers with anti-cheat may reject the extra speed.

## Build from source

Requires JDK 17 or newer.

```bash
./gradlew build
```

The jar is written to `build/libs/speedtweaks-1.2.0.jar`.
