package cn.ksmcbrigade.ct;

import net.minecraft.world.entity.player.Player;

public class Manager {
    public static void run(Player player) {
        if(CustomTimer.MC.getTimer()!=CustomTimer.timer){
            CustomTimer.setTimer(CustomTimer.timerFloat);
        }
    }
}
