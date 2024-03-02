package cn.ksmcbrigade.ct;

import cn.ksmcbrigade.ct.mixin.MinecraftMixin;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod("ct")
@Mod.EventBusSubscriber
public class CustomTimer {

    public static Path path = Paths.get("config/timer.json");
    public static MinecraftMixin MC = (MinecraftMixin)Minecraft.getInstance();
    public static float timerFloat = 20.0F;
    public static Timer defTimer;
    public static Timer timer = new Timer(20.0F,0L);

    public CustomTimer() throws IOException {
        MinecraftForge.EVENT_BUS.register(this);
        init();
    }

    public static void init() throws IOException {
        if(!new File("config/vm/mods").exists()){
            new File("config/vm/mods").mkdirs();
        }
        if(!new File("config/vm/mods/Timer.json").exists()){
            JsonObject object = new JsonObject();
            object.addProperty("name","ct.name");
            object.addProperty("id","ct");
            object.addProperty("main","cn.ksmcbrigade.ct.Manager");
            object.addProperty("function","NONE");
            object.addProperty("function_2","run");
            object.addProperty("gui_main","NONE");
            object.addProperty("gui_function","NONE");
            Files.write(Paths.get("config/vm/mods/Timer.json"),object.toString().getBytes());
        }

        if(!new File(path.toUri()).exists()){
            JsonObject object = new JsonObject();
            object.addProperty("timer",25.0F);
            Files.write(path,object.toString().getBytes());
        }
        defTimer = MC.getTimer();
        timerFloat = JsonParser.parseString(Files.readString(path)).getAsJsonObject().get("timer").getAsFloat();
        timer = new Timer(timerFloat,0L);
    }

    public static void setTimer(float timerValue) {
        timerFloat = timerValue;
        timer = new Timer(timerValue,0L);
        MC.setTimer(timer);
    }

    public static void save() throws IOException {
        JsonObject object = new JsonObject();
        object.addProperty("timer",timerFloat);
        Files.write(path,object.toString().getBytes());
    }

    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        boolean is = is(I18n.get("ct.name"));
        if(!is){
            MC.setTimer(defTimer);
        }
    }

    @SubscribeEvent
    public static void command(RegisterClientCommandsEvent event){
        event.getDispatcher().register(Commands.literal("timer").then(Commands.argument("value", FloatArgumentType.floatArg(0.0F)).executes(context -> {
            try {
                timerFloat = FloatArgumentType.getFloat(context,"value");
                timer = new Timer(timerFloat,0L);
                save();
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            }
        })).executes(context -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendMessage(Component.nullToEmpty("Timer: "+timerFloat),Minecraft.getInstance().player.getUUID());
            }
            return 0;
        }));
    }

    public static boolean is(String name) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName("cn.ksmcbrigade.VM.Utils");
        Class<?>[] parameterTypes = new Class[]{String.class};
        Method method = clazz.getDeclaredMethod("isEnabledMod", parameterTypes);
        method.setAccessible(true);
        Object instance = clazz.getDeclaredConstructor().newInstance();
        Object obj = method.invoke(instance, name);
        if(obj==null){
            return false;
        }
        else{
            return (boolean)obj;
        }
    }
}
