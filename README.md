# SuperMode
Minecraft spigot plugin, made in July 2018.
For archival purposes only

**[This plugin's oficial release](https://www.spigotmc.org/resources/supermode.58766/)**
## Original description
This is my first plugin, it adds cool new abilities that are toggle on and off with the command /supermode toggle

- Any blocks you punch (while sneaking), will go flying away bringing with it some blocks that are near.
- When you punch entities they receive more damage and knockback.
- When you jump while sprinting, you'll be launched in the direction you look, and the fall damage from that will be turned into an explosion, that does not damage you, when you land.

You can also do `/supermode toggle [playername]` to toggle for other player.
Do `/supermode list` to get players that have it enabled.
### TODO
- [X] Add permissions
- [X] Add config with protected blocks or affected blocks control
- [X] Rewrite to Kotlin (V2.0)
### Configuration
```yaml
Punch-Radius: 2
Protected-Blocks:
- bedrock
```
To protect a block, just add the text id to the `Protected-Blocks` list:
```yaml
Protected-Blocks:
- bedrock
- birch_planks
```
Now birch planks won't be affected by the plugin
### Permissions
- supermode.toggle
- supermode.toggle.others
- supermode.jump
- supermode.list
