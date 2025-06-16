package dev.kingtux.tms.mixin.helpers

enum class SupportMarker {
    GreaterThan,
    LessThan,
    NotEqual,
    Equal
}

data class MinecraftVersionSupportRange(
    val marker: SupportMarker,
    val version: MinecraftVersionType
) {
    companion object {
        fun parse(version: String): MinecraftVersionSupportRange {
            var version = version.trim()
            val marker = when {
                version.startsWith(">") -> {
                    version = version.removePrefix(">")
                    SupportMarker.GreaterThan
                }

                version.startsWith("<") -> {
                    version = version.removePrefix("<")
                    SupportMarker.LessThan
                }

                version.startsWith("!=") -> {
                    version = version.removePrefix("!=")
                    SupportMarker.NotEqual
                }

                version.startsWith("=") -> {
                    version = version.removePrefix("=")
                    SupportMarker.Equal
                }

                else -> SupportMarker.Equal
            }
            return MinecraftVersionSupportRange(
                marker,
                version = MinecraftVersionType.parse(version)
            )
        }
    }

    fun supports(minecraftVersion: MinecraftVersionType): Boolean {
        return when (marker) {
            SupportMarker.GreaterThan -> minecraftVersion >= version
            SupportMarker.LessThan -> minecraftVersion <= version
            SupportMarker.NotEqual -> minecraftVersion != version
            SupportMarker.Equal -> minecraftVersion == version
        }
    }
}

data class MinecraftVersionType(
    val major: Int,
    val minor: Int,
    val patch: Int? = null,
    val preRelease: Int? = null,
) : Comparable<MinecraftVersionType> {
    companion object {
        fun parse(version: String): MinecraftVersionType {
            val parts = version.split(".")
            val major = parts.getOrNull(0)?.toIntOrNull() ?: 0
            val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val patchString = parts.getOrNull(2)?.lowercase() ?: "0"
            var patch: Int? = null;
            var preRelease: Int? = null;
            if (patchString.contains("-beta")) {
                patch = patchString.removeSuffix("-beta").toIntOrNull()
                preRelease = parts.getOrNull(3)?.toIntOrNull()
            } else {
                patch = patchString.toIntOrNull()
            }

            return MinecraftVersionType(major, minor, patch, preRelease)
        }
    }


    override fun compareTo(other: MinecraftVersionType): Int {
        return when {
            major != other.major -> major - other.major
            minor != other.minor -> minor - other.minor
            patch != null && other.patch != null -> patch - other.patch
            patch != null -> 1 // this version has a patch, the other does not
            other.patch != null -> -1 // the other version has a patch, this does not
            preRelease != null && other.preRelease != null -> preRelease - other.preRelease
            preRelease != null -> 1 // this version has a pre-release, the other does not
            other.preRelease != null -> -1 // the other version has a pre-release, this does not
            else -> 0 // they are equal
        }
    }
}
