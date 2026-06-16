package dev.kingtux.tms.mixin.helpers


// Applied to mixin classes and read at runtime by MCVersionMixinPlugin via
// Class.getAnnotations(), so it must target the class declaration (CLASS), not a
// type-use (TYPE) — the latter is not returned by Class.getAnnotations().
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MinecraftVersion(
    val minecraftVersions: Array<String> = []
)
