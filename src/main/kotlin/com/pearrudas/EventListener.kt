package com.pearrudas

import com.google.common.collect.Sets
import com.pearrudas.Main
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.util.*


class EventListener(plugin: Main) : Listener {

    internal val players: ArrayList<Player> = ArrayList<Player>()
    private var eventByThis: Boolean = false
    private val prevPlayersOnGround = Sets.newHashSet<UUID>()

    internal fun getCardinalDirection(p: Player): String {
        var rot: Double = ((p.location.yaw -90) % 360).toDouble()
        if(rot < 0){
            rot += 360.0
        }
        return getDirection(rot)
    }

    private fun getDirection(rot: Double): String{
        return if (0 <= rot && rot < 67.5) {
            "North"
        } else if (67.5 <= rot && rot < 157.5) {
            "East"
        } else if (157.5 <= rot && rot < 247.5) {
            "South"
        } else if (247.5 <= rot && rot < 337.5) {
            "West"
        } else if (337.5 <= rot && rot < 360.0) {
            "North"
        } else {
            "North"
        }
    }

    private fun getBlocks(start: Block, radius: Int): ArrayList<Block>{
        val blocks = ArrayList<Block>()
        var x = start.location.x - radius
        while (x <= start.location.x + radius) {
            var y = start.location.y - radius
            while (y <= start.location.y + radius) {
                var z = start.location.z - radius
                while (z <= start.location.z + radius) {
                    val loc = Location(start.world, x, y, z)
                    blocks.add(loc.block)
                    z++
                }
                y++
            }
            x++
        }
        return blocks
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        var p: Player = event.player
        plugin.pState[p] = false
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent){
        var p: Player = event.player
        plugin.pState.remove(p)
    }

    @EventHandler
    fun superPunchEntity(e: EntityDamageByEntityEvent){
        if(e.damager is Player){
            var damager: Player = e.damager as Player
            var victim: Entity = e.entity

            if(!plugin.pState.containsKey(damager)){
                return
            }

            if(!(plugin.pState[damager])!!){
                return
            }

            e.damage = 10.0

            for (i in 0..5) {
                victim.world.spawnParticle(Particle.CRIT_MAGIC, victim.location, 30)
            }

            if(victim.isInsideVehicle) victim.leaveVehicle()

            victim.velocity = damager.location.direction.setY(0).normalize().multiply(5)
        }
    }

    @EventHandler
    fun superPunchBrick(e: PlayerInteractEvent){
        if(e.action != Action.LEFT_CLICK_BLOCK || !e.player.isSneaking){
            return
        }

        val p: Player = e.player
        players.add(p)

        if(!(plugin.pState.containsKey(p))){return}

        if(!(plugin.pState[p])!!){return}

        var loc: Location = e.player.location
        val block: Block = e.clickedBlock

        val blocks: ArrayList<Block> = getBlocks(block, plugin.config.getInt("Punch-Range"))
        for (b in blocks) {
            var doThisForBlock = true
            for(i: String in plugin.config.getStringList("Protected-Blocks")){
                if(b.type == Material.matchMaterial(i)){
                    doThisForBlock = false
                }
            }

            var x = 0f
            val y = 0.4f
            var z = 0f

            var lastPlayer: Player = players[players.size - 1]
            var facing: String = getCardinalDirection(lastPlayer)

            when (facing) {
                "North" -> x = -.9f //West
                "East"  -> z = -.9f //North
                "South" -> x = +.9f //South
                "West"  -> z = +.9f //East
            }

            if(doThisForBlock) {
                b.world.createExplosion(b.location, 0.0f)
                var fb: FallingBlock = b.world.spawnFallingBlock(b.location, b.type, b.data)
                (fb as Entity).velocity = Vector(x, y, z)
                b.world.playEffect(b.location, Effect.STEP_SOUND, b.type)
                b.world.playSound(b.location, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 20f, 0f)
                b.type = Material.AIR
            }

        }
    }

    @EventHandler
    fun runAndJump(e: PlayerMoveEvent){
        val player = e.player

        if (!player.hasPermission("supermode.jump")) {
            return
        }

        if (!plugin.pState.containsKey(player)) {
            return
        }

        if (!plugin.pState[player]!!) {
            return
        }

        if (player.velocity.y > 0) {
            var jumpVelocity = 0.42f.toDouble()
            if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                jumpVelocity += ((player.getPotionEffect(PotionEffectType.JUMP).amplifier + 1).toFloat() * 0.1f).toDouble()
            }
            if (e.player.location.block.type != Material.LADDER && prevPlayersOnGround.contains(player.uniqueId)) {
                if (!player.isOnGround && java.lang.Double.compare(player.velocity.y, jumpVelocity) == 0) {
                    if (player.isSprinting) {
                        val blockUnder = player.location.block.getRelative(BlockFace.DOWN)
                        player.velocity = player.location.direction.setY(0.7f).normalize().multiply(3)
                        eventByThis = true
                    }
                }
            }
        }
        if(player.isOnGround){
            prevPlayersOnGround.add(player.uniqueId)
        } else {
            prevPlayersOnGround.remove(player.uniqueId)
        }
    }

    @EventHandler
    fun onFall(e: EntityDamageEvent){
        if (e.entity !is Player) {
            return
        }

        val p = e.entity as Player
        val blockUnder = p.location.block.getRelative(BlockFace.DOWN)

        if (!plugin.pState.containsKey(p)) {
            return
        }

        if (!plugin.pState[p]!!) {
            return
        }

        if (!eventByThis) {
            return
        }

        if (e.cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            e.isCancelled = true
        }

        if (e.cause != EntityDamageEvent.DamageCause.FALL) {
            return
        }

        if(!p.isSneaking){
            blockUnder.world.createExplosion(blockUnder.location, 3f)
        }

        e.isCancelled = true
        eventByThis = false
    }

    @EventHandler
    fun onBlockExplosion(e: BlockExplodeEvent) {
        if (!eventByThis) {
            return
        }

        val blocks = e.blockList()
        for (b: Block in blocks) {
            var doForThisBlock = true
            for (i in plugin.config.getStringList("Protected-Blocks")) {
                if (b.type == Material.matchMaterial(i)) {
                    doForThisBlock = false
                }
            }
            if (doForThisBlock) {
                val x = -2.0f + (Math.random() * 3.0 + 1.0).toFloat()
                val y = -3.0f + (Math.random() * 6.0 + 1.0).toFloat()
                val z = -2.0f + (Math.random() * 3.0 + 1.0).toFloat()

                val fb = b.world.spawnFallingBlock(b.location, b.type, b.data)
                (fb as Entity).velocity = Vector(x, y, z)
                fb.dropItem = false
                b.world.playEffect(b.location, Effect.STEP_SOUND, b.type)
                b.world.playSound(b.location, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 20f, 0f)
                b.type = Material.AIR
            }
        }

        e.isCancelled = true
    }
}