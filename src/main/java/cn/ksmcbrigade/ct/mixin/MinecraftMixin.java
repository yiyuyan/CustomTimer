package cn.ksmcbrigade.ct.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftMixin {
    @Accessor("timer")
    @Final
    Timer getTimer();

    @Accessor("timer")
    @Final
    @Mutable
    void setTimer(Timer timer);
}
