package dansplugins.simpleskills.nms;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class NMSVersion extends JavaPlugin implements Listener {

    public static String getNMSVersion(){
        String v = Bukkit.getServer().getClass().getPackage().getName();
        return v.substring(v.lastIndexOf('.') + 1);
    }
    public static String formatNMSVersion(String nms){
        switch(nms){
            case "v1_13_R1":
                return "1.13.1";
            case "v1_13_R2":
                return "1.13.2";
            case "v1_14_R1":
                return "1.14.x";
            case "v1_15_R1":
                return "1.15.x";
            case "v1_16_R1":
                return "1.16.1";
            case "v1_16_R2":
                return "1.16.2/1.16.3";
            case "v1_16_R3":
                return "1.16.4/1.16.5";
            case "v1_17_R1":
                return "1.17/1.17.1";
            case "v1_18_R1":
                return "1.18";

        }
        throw new IllegalArgumentException(nms + " isn't a know version");
    }
}
