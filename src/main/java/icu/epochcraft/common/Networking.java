package icu.epochcraft.common;

import net.fabricmc.fabric.api.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

/**
 * 网络通道定义
 */
public class Networking {
    public static final Identifier OPEN_GUI_PACKET = new Identifier("qadvancement", "open_gui");

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(OPEN_GUI_PACKET, (client, handler, buf, responseSender) -> {
            int count = buf.readInt();
            String[] ids = new String[count];
            String[] titles = new String[count];
            String[] descriptions = new String[count];
            String[] categories = new String[count];
            String[] icons = new String[count];
            boolean[] completed = new boolean[count];
            for (int i = 0; i < count; i++) {
                ids[i] = buf.readString(32767);
                titles[i] = buf.readString(32767);
                descriptions[i] = buf.readString(32767);
                categories[i] = buf.readString(32767);
                icons[i] = buf.readString(32767);
                completed[i] = buf.readBoolean();
            }
            client.execute(() -> QAdvancementClient.openScreen(ids, titles, descriptions, categories, icons, completed));
        });
    }
}