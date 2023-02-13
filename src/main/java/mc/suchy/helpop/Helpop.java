package mc.suchy.helpop;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import mc.suchy.helpop.commands.HelpopCommand;
import org.slf4j.Logger;

@Plugin(
        id = "helpop",
        name = "Helpop",
        version = "1.0-SNAPSHOT"
)
public class Helpop {

    private static Logger logger;
    private final ProxyServer server;
    @Inject
    public Helpop (ProxyServer server, Logger logger) {
        this.server = server;
    }
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getCommandManager().register("helpop", new HelpopCommand(this), "pomoc");
    }
    public ProxyServer getServer() {
        return server;
    }

}
