package mc.suchy.helpop.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import mc.suchy.helpop.Helpop;
import mc.suchy.helpop.discordwebhook.DiscordWebhook;
import net.kyori.adventure.text.Component;

import javax.xml.crypto.Data;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class HelpopCommand implements SimpleCommand {
    ProxyServer server;
    HashMap<UUID, Long> lastMessage;
    public HelpopCommand(Helpop helpop) {
        this.server = helpop.getServer();
        this.lastMessage = new HashMap<UUID, Long>();
    }

    @Override
    public void execute(Invocation commandInvocation) {
        CommandSource commandSource = commandInvocation.source();
        String[] strings = commandInvocation.arguments();
        Player player = (Player) commandSource;
        int adminsOnline = 0;
        if (this.lastMessage.containsKey(player.getUniqueId())) {
            final long lastMessageTime = this.lastMessage.get(player.getUniqueId());
            final long difference = System.currentTimeMillis() - lastMessageTime;
            if (difference < 300000) {
                final long time = 300000 - difference;
                SimpleDateFormat sdf = new SimpleDateFormat("m.s");
                Date resultdate = new Date(time);
                commandSource.sendMessage(Component.text("§6§lKATO§f§lMC §8» §aNastępną prośbę o pomoc możesz wysłać za §6" + sdf.format(resultdate) + " §aminut."));
                return;
            }
        }
        this.lastMessage.put(player.getUniqueId(), System.currentTimeMillis());


        if (commandSource instanceof Player) {
            if (strings.length >= 1) {
                String msg = "";
                for (int i = 0; i < strings.length; ++i) {
                    msg = String.valueOf(msg) + strings[i] + " ";
                }
                Optional<ServerConnection> currentServer = player.getCurrentServer();
                commandSource.sendMessage(Component.text("§6§lKATO§f§lMC §8» §aTwoje zapytanie zostało wysłane: §6" + msg));
                DiscordWebhook webhook = new DiscordWebhook("https://discordapp.com/api/webhooks/1041368639305154631/mM-vuvLoq2U7UEC5dF4-EHn16xbqCEDlaCigv9QULcMjhPnVKzPvcYauIEIwiIEWYE5t");
                webhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle("**Powiadomienie**")
                        .setDescription("**Gracz potrzebuje pomocy!**")
                        .setColor(Color.ORANGE)
                        .addField("Serwer", currentServer.get().getServerInfo().getName(), true)
                        .addField("Gracz", player.getUsername(), true)
                        .addField("Wiadomość", msg, false)
                        .setThumbnail("https://minotar.net/helm/"+player.getUsername()+"/50"));
                try {
                    webhook.execute(); //Handle exception
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (final Player all : server.getAllPlayers()) {
                    if (all.hasPermission("helpop.notify")) {
                        adminsOnline++;
                        all.sendMessage(Component.text("§8[§7"+ currentServer.get().getServerInfo().getName() + "§8] §c[HELPOP] §7" + player.getUsername() + "§8: §7" + msg));
                    }
                    if(adminsOnline==0){
                        commandSource.sendMessage(Component.text("§6§lKATO§f§lMC §8» §aNie znaleziono dostępnego administratora. Czas oczekiwania na pomoc może być wydłużony."));
                    }
                }
            }
            else {
                commandSource.sendMessage(Component.text("§6§lKATO§f§lMC §8» §aUżyj: §6/helpop <wiadomość>"));
            }
        }
        else {
            commandSource.sendMessage(Component.text("§6§lKATO§f§lMC §8» §aMusisz być graczem!"));
        }
    }
}
