package com.antarchy.guard;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class DamageHandler {

    private static final String NAMESPACE = "antarchy";
    private static final float MAX_DAMAGE = 10.0F;

    @SubscribeEvent
    public void onIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        Entity target = event.getEntity();

        if (source.getEntity() instanceof Player attacker) {
            limitPlayerAttack(event, source, attacker, target);
        }

        if (target instanceof Player victim) {
            ignoreArmorAgainstOutsiders(event, source, victim);
        }
    }

    private void limitPlayerAttack(LivingIncomingDamageEvent event, DamageSource source, Player attacker, Entity target) {
        ItemStack weapon = resolveWeapon(source, attacker);
        if (!isAntarchyItem(weapon)) {
            return;
        }
        if (isAntarchyEntity(target)) {
            return;
        }
        if (event.getAmount() > MAX_DAMAGE) {
            event.setAmount(MAX_DAMAGE);
        }
    }

    private void ignoreArmorAgainstOutsiders(LivingIncomingDamageEvent event, DamageSource source, Player victim) {
        Entity attacker = source.getEntity() != null ? source.getEntity() : source.getDirectEntity();
        if (attacker == null || isAntarchyEntity(attacker)) {
            return;
        }
        if (!wearsAntarchyArmor(victim)) {
            return;
        }
        event.addReductionModifier(DamageContainer.Reduction.ARMOR, (container, reduction) -> 0.0F);
        event.addReductionModifier(DamageContainer.Reduction.ENCHANTMENTS, (container, reduction) -> 0.0F);
    }

    private ItemStack resolveWeapon(DamageSource source, Player attacker) {
        ItemStack weapon = source.getWeaponItem();
        if (weapon != null) {
            return weapon;
        }
        if (source.getDirectEntity() == attacker) {
            return attacker.getMainHandItem();
        }
        return ItemStack.EMPTY;
    }

    private boolean wearsAntarchyArmor(Player player) {
        for (ItemStack stack : player.getArmorSlots()) {
            if (isAntarchyItem(stack)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAntarchyItem(ItemStack stack) {
        return !stack.isEmpty()
                && NAMESPACE.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace());
    }

    private boolean isAntarchyEntity(Entity entity) {
        return NAMESPACE.equals(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace());
    }
}
