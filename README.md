## Redstone Counter

This is a simple Counter for redstone components within a chunk.
I made it for the **StarnixMC Minecraft Server**

Compatible with Paper/Spigot/Purpur/Bukkit 1.20.x-1.21.x

**Usage:**

By typing /redstone in the Chat, you can count all the configured Blocks within the chunk you are standing in. And you can see if the amount of components go over a configurable Limit.

In the config.yml you can configure the Blocks you want to be counted and add a maximum to them as well. When no maximum is added, the plugin will show an infinity sign next to the Block in the Chat Output.

If you are within the limit, the number is green, and the rest gray. If you are over the set Limit then both numbers will be shown in red.

Since this is not Redstone Limiter, but just a counter, you will need an external Plugin like MobFarmManager, Clearlag or similar to enforce the limits! 

This plugin however can let your players know the redstone limitations just by typing a simple command. It can help them to stay within the limits when building contraptions!

![Chat Output](https://cdn.modrinth.com/data/cached_images/ddda1bb05e90760e4af199b3362a888951dd04cb.png)

**Permissions:**

redstone.use - Allows a player to use the /redstone command, it is true by default
redstone.reload- Allows you to reload the config.yml in game, defaults to OP

Config.yml:


```
# List of blocks to count, just add or remove blocks here. Then you can set a max value for them below. 
# If you dont set a value it will default to "∞"
# The Plugin supports normalized names for most redstone components, e.g "STICKY_PISTON" becomes "Sticky Piston" in the Chat Output.
blocks:
  - PISTON
  - STICKY_PISTON
  - REPEATER
  - COMPARATOR
  - HOPPER
  - OBSERVER
  - DROPPER
  - DISPENSER


# Maximum allowed blocks per chunk (must be enforced by another plugin like MobFarmManager). Set to 0 or -1 to display ∞ inseatd of a max value.
# If no value is given, it defaults to "∞"
max:
  PISTON: 32
  STICKY_PISTON: 32
  REPEATER: 32
  COMPARATOR: 32
  HOPPER: 32
  OBSERVER: 16

```

**Source:**

Just check out the source right here:
https://pastebin.com/9b0C0T5V

**Server Plug:**

> play.starnixmc.xyz

> ind.starnixmc.xyz

> Bedrock Port: 25565

Discord: https://discord.com/invite/mxSPg9fxET
