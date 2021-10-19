package com.pearrudas

import org.bukkit.Bukkit.getPlayer
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands(plugin: Main): CommandExecutor {
    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        when(command?.name?.toLowerCase()){
            "supermode" -> {
                when(args?.size){
                    0 -> {
                        sender!!.sendMessage(plugin.name + " V" + plugin.description.version)
                        return true
                    }

                    1 -> {
                        when(args[0]){
                            "toggle" -> {
                                if (sender is Player) {

                                    if(!(sender.hasPermission("supermode.toggle"))){
                                        sender.sendMessage("${ChatColor.RED} You do not have this permission.")
                                        return true
                                    }

                                    if (plugin.pState.containsKey(sender)) {
                                        if (plugin.pState[sender] == java.lang.Boolean.TRUE) {
                                            sender.sendMessage("Supermode is now disabled!")
                                            plugin.pState[sender] = false
                                            return true
                                        } else if (plugin.pState[sender] == java.lang.Boolean.FALSE) {
                                            plugin.pState[sender] = true
                                            sender.sendMessage("Supermode is now enabled!")
                                            return true
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED.toString() + "PLAYER NOT FOUND")
                                        return true
                                    }
                                } else {
                                    sender?.sendMessage("Only players can do this! You can pass an target player after toggle.")
                                }
                            }

                            "list" -> {
                                if(!(sender!!.hasPermission("supermode.list"))){
                                    sender.sendMessage("${ChatColor.RED} You do not have this permission.")
                                    return true
                                }

                                for(p in plugin.pState){
                                    return if(p.value){
                                        sender.sendMessage("The player ${p.key.name} has supermode on.")
                                        true
                                    } else {
                                        sender.sendMessage("No players are using supermode at the moment")
                                        true
                                    }
                                }
                            }

                            "help" -> {
                                sender?.sendMessage(ChatColor.YELLOW.toString() + "" + ChatColor.BOLD + "The available arguments for /supermode are:")
                                sender?.sendMessage(ChatColor.YELLOW.toString() + "" + ChatColor.BOLD + "toggle //Toggles super mode.")
                                sender?.sendMessage(ChatColor.YELLOW.toString() + "" + ChatColor.BOLD + "toggle <PLAYERNAME> //Toggles supermode for the specified player.")
                                sender?.sendMessage(ChatColor.YELLOW.toString() + "" + ChatColor.BOLD + "help //Shows this message.")
                                return true
                            }
                        }
                    }

                    2 -> {
                        if (sender!!.hasPermission("supermode.toggle.others")) {
                            when {
                                args[0] == "toggle" -> if (getPlayer(args[1]) == null) {
                                    sender.sendMessage("This player doesn't exist.")
                                    return true
                                } else {
                                    val target = getPlayer(args[1])
                                    if (!target.isOnline) {
                                        sender.sendMessage("The target player is offline.")
                                        return true
                                    }
                                    val newState = !(plugin.pState[target] as Boolean)
                                    plugin.pState[target] = newState
                                    sender.sendMessage("Supermode for player " + args[1] + " has been set to " + newState)
                                    return true
                                }
                                else -> {
                                }
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED.toString() + "YOU DO NOT HAVE THIS PERMISSION!")
                            return true
                        }
                    }
                }
            }
        }
        return false
    }
}