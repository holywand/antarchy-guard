package com.antarchy.guard;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(AntarchyGuard.MOD_ID)
public final class AntarchyGuard {

    public static final String MOD_ID = "antarchyguard";

    public AntarchyGuard() {
        NeoForge.EVENT_BUS.register(new DamageHandler());
    }
}
