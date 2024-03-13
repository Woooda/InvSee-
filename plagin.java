import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InvSeePlusPlus extends JavaPlugin implements CommandExecutor {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<UUID, Inventory> playerInventoryCache = new HashMap<>();
    private final Map<UUID, Inventory> playerEnderchestCache = new HashMap<>();

    @Override
    public void onEnable() {
        getCommand("invsee").setExecutor(this);
        getCommand("endersee").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("invsee")) {
            if (args.length != 1) {
                player.sendMessage("Usage: /invsee <player>");
                return true;
            }

            String targetName = args[0];
            executorService.execute(() -> openInventory(player, targetName, true));

        } else if (command.getName().equalsIgnoreCase("endersee")) {
            if (args.length != 1) {
                player.sendMessage("Usage: /endersee <player>");
                return true;
            }

            String targetName = args[0];
            executorService.execute(() -> openInventory(player, targetName, false));
        }

        return true;
    }

    private void openInventory(Player player, String targetName, boolean isInventory) {
        Player targetPlayer = getServer().getPlayer(targetName);
        if (targetPlayer == null) {
            player.sendMessage("Player not found!");
            return;
        }

        if (isInventory) {
            Inventory inventory = targetPlayer.getInventory();
            player.openInventory(inventory);
            playerInventoryCache.put(targetPlayer.getUniqueId(), inventory);
        } else {
            Inventory enderChest = targetPlayer.getEnderChest();
            player.openInventory(enderChest);
            playerEnderchestCache.put(targetPlayer.getUniqueId(), enderChest);
        }
    }

    @Override
    public void onDisable() {
        executorService.shutdown();
    }
}
