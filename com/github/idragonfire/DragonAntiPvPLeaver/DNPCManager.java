package com.github.idragonfire.DragonAntiPvPLeaver;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_4_6.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import de.kumpelblase2.remoteentities.EntityManager;
import de.kumpelblase2.remoteentities.api.DespawnReason;
import de.kumpelblase2.remoteentities.api.RemoteEntityType;
import de.kumpelblase2.remoteentities.api.thinking.DamageBehavior;
import de.kumpelblase2.remoteentities.api.thinking.Mind;
import de.kumpelblase2.remoteentities.entities.RemotePlayer;
import de.kumpelblase2.remoteentities.entities.RemotePlayerEntity;

public class DNPCManager {

    private EntityManager npcManager;
    private HashMap<String, RemotePlayer> playerNPCs;
    private HashSet<Entity> bukkitEntities;
    private DAntiPvPLeaverPlugin plugin;

    public DNPCManager(EntityManager npcManager, DAntiPvPLeaverPlugin plugin) {
        this.npcManager = npcManager;
        this.playerNPCs = new HashMap<String, RemotePlayer>();
        this.bukkitEntities = new HashSet<Entity>();
        this.plugin = plugin;
    }

    public boolean isDragonNPC(Entity entity) {
        return this.bukkitEntities.contains(entity);
    }

    public void despawnPlayerNPC(String npcID) {
        if (this.playerNPCs.containsKey(npcID)) {
            this.playerNPCs.get(npcID).despawn(DespawnReason.CUSTOM);
        }
    }

    public String spawnPlayerNPC(Player player) {
        // TODO: ChatColor for NPC name?
        RemotePlayer remoteEntity = (RemotePlayer) this.npcManager
                .createNamedEntity(RemoteEntityType.Human,
                        player.getLocation(), ChatColor.RED + player.getName());
        Mind mind = remoteEntity.getMind();
        final String npcID = "DragonPlayerNPC_" + player.getName();
        mind.addBehaviour(new DamageBehavior(remoteEntity) {
            @Override
            public void onRemove() {
                System.out.println("npc dead 2");
                super.onRemove();
            }

            @Override
            public void onDamage(EntityDamageEvent event) {
                System.out.println("event");
                DNPCManager.this.plugin.npcFirstTimeAttacked(npcID);
            }
        });

        // remoteEntity.getMind().addActionDesire(new DesirePanic(remoteEntity),
        // remoteEntity.getMind().getHighestActionPriority() + 1);

        RemotePlayerEntity remotePlayerEntity = (RemotePlayerEntity) remoteEntity
                .getHandle();
        Player npcPlayer = (Player) remotePlayerEntity.getRemoteEntity()
                .getBukkitEntity();
        // set armor
        npcPlayer.getEquipment().setArmorContents(
                player.getEquipment().getArmorContents());
        // set inventory
        npcPlayer.getInventory().setContents(
                player.getInventory().getContents());

        // TODO: use no craftbukkit code
        // set correct item in the hand
        int itemindex = ((CraftInventoryPlayer) player.getInventory())
                .getInventory().itemInHandIndex;
        ItemStack oldHand = npcPlayer.getItemInHand();
        npcPlayer.setItemInHand(player.getItemInHand());
        npcPlayer.getInventory().setItem(itemindex, oldHand);

        this.playerNPCs.put(npcID, remoteEntity);
        this.bukkitEntities.add(remoteEntity.getBukkitEntity());

        return npcID;
    }
}