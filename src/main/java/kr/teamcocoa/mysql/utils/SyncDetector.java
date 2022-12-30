package kr.teamcocoa.mysql.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;

import java.text.MessageFormat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SyncDetector {

    private static Thread bukkitThread;
    private static String packageName;
    private static String version;

    static {
        try {
             packageName = Bukkit.getServer().getClass().getPackage().getName();
             version = packageName.substring(packageName.lastIndexOf('.') + 1);
             Class minecraftServer = Class.forName(MessageFormat.format("net.minecraft.server.{0}.{1}", version, "MinecraftServer"));
             Object serverInstance = minecraftServer.getMethod("getServer").invoke(null);
             bukkitThread = (Thread) serverInstance.getClass().getField("serverThread").get(serverInstance);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void catchSynchronousCall() {
        if(Thread.currentThread() == bukkitThread) {
            throw new IllegalStateException("[MySQL] Synchronous Called!");
        }
    }

}
