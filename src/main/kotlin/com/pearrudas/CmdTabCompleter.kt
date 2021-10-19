package com.pearrudas

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import java.util.ArrayList

class CmdTabCompleter(plugin: Main): TabCompleter {
    override fun onTabComplete(sender: CommandSender?, command: Command?, alias: String?, args: Array<out String>?): MutableList<String>? {
        if (command!!.name.equals("supermode", ignoreCase = true) && args!!.size < 2) {
            val returnArgs = ArrayList<String>()
            returnArgs.add("toggle")
            returnArgs.add("help")
            returnArgs.add("list")
            return returnArgs
        } else {
            return null
        }
    }
}