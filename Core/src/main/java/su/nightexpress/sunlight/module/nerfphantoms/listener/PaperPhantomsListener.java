package su.nightexpress.sunlight.module.nerfphantoms.listener;

import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.nerfphantoms.PhantomsModule;
import su.nightexpress.sunlight.module.nerfphantoms.PhantomsProperties;
import su.nightexpress.sunlight.user.SunUser;

public class PaperPhantomsListener extends AbstractListener<SunLightPlugin> {

    private final PhantomsModule module;

    public PaperPhantomsListener(@NotNull SunLightPlugin plugin, @NotNull PhantomsModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPhantomPreSpawn(PhantomPreSpawnEvent event) {
        if (event.getSpawningEntity() instanceof Player player) {
            SunUser user = this.plugin.getUserManager().getOrFetch(player);
            if (user.getPropertyOrDefault(PhantomsProperties.ANTI_PHANTOM)) {
                event.setCancelled(true);
            }
        }
    }
}
