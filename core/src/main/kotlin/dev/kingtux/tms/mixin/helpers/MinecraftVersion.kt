package dev.kingtux.tms.mixin.helpers


@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class MinecraftVersion(
    val minecraftVersions: Array<String> = []
)
