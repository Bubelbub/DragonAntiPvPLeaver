package com.github.idragonfire.DragonAntiPvPLeaver.listener;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

import com.github.idragonfire.DragonAntiPvPLeaver.Plugin;
import com.github.idragonfire.DragonAntiPvPLeaver.Plugin.DAMAGE_MODE;

public class DealDamageListener extends DamageTimeListenerInjection {

    public DealDamageListener(HashMap<DAMAGE_MODE, Integer> mode) {
        super(mode);
    }

    @Override
    public void onEntityDamageByEntity(LivingEntity attacker, Entity victim) {
        if (attacker instanceof HumanEntity) {
            super.timeTable.put(((HumanEntity) attacker).getName(),
                    Plugin.checkEntityType(victim, this.mode));
        }
    }
}
