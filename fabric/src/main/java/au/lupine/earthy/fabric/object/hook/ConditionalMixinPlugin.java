package au.lupine.earthy.fabric.object.hook;

import au.lupine.earthy.fabric.EarthyFabric;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ConditionalMixinPlugin implements IMixinConfigPlugin {

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        switch (mixinClassName) {
            case "au.lupine.earthy.fabric.mixin.xaeros.HudModMixin",
                 "au.lupine.earthy.fabric.mixin.xaeros.RadarStateUpdateMixin" -> {
                return FabricLoader.getInstance().isModLoaded("xaerominimap");
            }
            default -> {
                return true;
            }
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
        EarthyFabric.logInfo("Mixin Plugin " + this.getClass().getSimpleName() + " loaded");
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
