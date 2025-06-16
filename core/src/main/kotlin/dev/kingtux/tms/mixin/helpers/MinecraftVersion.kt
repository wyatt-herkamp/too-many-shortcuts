package dev.kingtux.tms.mixin.helpers

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy


@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class MinecraftVersion(
    val minecraftVersions: Array<String> = []
)
