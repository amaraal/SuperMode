package com.pearrudas

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var plugin: Main

class Main: JavaPlugin(){

    internal var pState: HashMap<Player, Boolean> = HashMap()

    fun loadConfig() {
        config.options().copyDefaults(true)

        saveDefaultConfig()
    }

    override fun onEnable() {
        loadConfig()

        plugin = this

        server.pluginManager.registerEvents(EventListener(this), this)

        getCommand("supermode").executor = Commands(this)

        getCommand("supermode").tabCompleter = CmdTabCompleter(this)

        for (p in server.onlinePlayers) {
            pState.putIfAbsent(p, false)
        }

        logger.info { "The plugin has loaded."}
        logger.info { "V${description.version}"}
    }

    override fun onDisable() {
        logger.info { "The plugin was unloaded" }
    }
}