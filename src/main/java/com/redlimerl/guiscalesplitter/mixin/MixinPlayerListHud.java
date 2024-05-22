package com.redlimerl.guiscalesplitter.mixin;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "collectPlayerEntries", at = @At("HEAD"), cancellable = true)
    public void modifySingleplay(CallbackInfoReturnable<List<PlayerListEntry>> cir) {
        if (this.client.player == null) {
            cir.setReturnValue(Lists.newArrayList(
                    new PlayerListEntry(this.client.getSession().getProfile(), false)
            ));
        }
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isInSingleplayer()Z"))
    public boolean modifySingleplay(MinecraftClient instance, Operation<Boolean> original) {
        if (this.client.player == null) return true;
        return original.call(instance);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getPlayerByUuid(Ljava/util/UUID;)Lnet/minecraft/entity/player/PlayerEntity;"))
    public PlayerEntity modifySingleplay(ClientWorld instance, UUID uuid, Operation<PlayerEntity> original) {
        if (this.client.player == null) return null;
        return original.call(instance, uuid);
    }
}
