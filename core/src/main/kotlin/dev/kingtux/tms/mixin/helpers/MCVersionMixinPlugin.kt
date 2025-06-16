package dev.kingtux.tms.mixin.helpers

import net.fabricmc.loader.api.FabricLoader
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class MCVersionMixinPlugin: IMixinConfigPlugin {
    override fun onLoad(p0: String?) {
    }

    override fun getRefMapperConfig(): String? {
        return null;
    }

    override fun shouldApplyMixin(targetClass: String?, mixinTarget: String?): Boolean {
        if (targetClass == null || mixinTarget == null) {
            return false;
        }
        val mixinClass: Class<*>?;
        try {
            mixinClass =         Thread.currentThread().contextClassLoader.loadClass(mixinTarget);

        } catch (e: ClassNotFoundException) {

            return true;
        }
        val supportedMinecraftVersions = mutableListOf<MinecraftVersionSupportRange>()
        for (annotation in mixinClass.annotations){
            if (annotation is MinecraftVersion) {
                annotation.minecraftVersions.forEach {
                    supportedMinecraftVersions.add(
                        MinecraftVersionSupportRange.parse(it)
                    )
                }
            }
        }
        if (supportedMinecraftVersions.isEmpty()) {
            return true;
        }
        val minecraftVersion = MinecraftVersionType.parse(FabricLoader.getInstance().getModContainer("minecraft")
            .get().metadata.version.friendlyString)

        for (supportedVersion in supportedMinecraftVersions) {
            if (supportedVersion.supports(minecraftVersion)) {
                return true;
            }
        }

        return false;
    }

    override fun acceptTargets(
        p0: Set<String?>?,
        p1: Set<String?>?
    ) {
    }

    override fun getMixins(): List<String?>? {
        return null;
    }

    override fun preApply(
        p0: String?,
        p1: ClassNode?,
        p2: String?,
        p3: IMixinInfo?
    ) {

    }

    override fun postApply(
        p0: String?,
        p1: ClassNode?,
        p2: String?,
        p3: IMixinInfo?
    ) {
    }
}