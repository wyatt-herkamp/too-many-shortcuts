import dev.kingtux.tms.mixin.helpers.MinecraftVersionSupportRange
import dev.kingtux.tms.mixin.helpers.MinecraftVersionType
import dev.kingtux.tms.mixin.helpers.SupportMarker
import kotlin.test.Test
import kotlin.test.assertEquals

class MinecraftVersionTesting {
    @Test
    fun parseMinecraftVersionTest() {
        assertEquals(
            MinecraftVersionType(1, 20, 4),
            MinecraftVersionType.parse("1.20.4")
        )
        assertEquals(
            MinecraftVersionType(1, 20, 5),
            MinecraftVersionType.parse("1.20.5")
        )
        assertEquals(
            MinecraftVersionType(1, 21, 0),
            MinecraftVersionType.parse("1.21.0")
        )
        assertEquals(
            MinecraftVersionType(1, 21, 0, 1),
            MinecraftVersionType.parse("1.21.0-beta.1")
        )
        assertEquals(
            MinecraftVersionType(1, 21, 6, 4),
            MinecraftVersionType.parse("1.21.6-beta.4")
        )
    }

    @Test
    fun parseMinecraftVersionRange() {
        assertEquals(
            MinecraftVersionSupportRange(
                SupportMarker.Equal,
                MinecraftVersionType(1, 20, 4)
            ),
            MinecraftVersionSupportRange.parse("=1.20.4")
        )
        assertEquals(
            MinecraftVersionSupportRange(
                SupportMarker.Equal,
                MinecraftVersionType(1, 20, 4)
            ),
            MinecraftVersionSupportRange.parse("1.20.4")
        )
        assertEquals(
            MinecraftVersionSupportRange(
                SupportMarker.GreaterThan,
                MinecraftVersionType(1, 20, 4)
            ),
            MinecraftVersionSupportRange.parse(">1.20.4")
        )
        assertEquals(
            MinecraftVersionSupportRange(
                SupportMarker.LessThan,
                MinecraftVersionType(1, 20, 4)
            ),
            MinecraftVersionSupportRange.parse("<1.20.4")
        )

        assertEquals(
            MinecraftVersionSupportRange(
                SupportMarker.NotEqual,
                MinecraftVersionType(1, 20, 4)
            ),
            MinecraftVersionSupportRange.parse("!=1.20.4")
        )


    }

    @Test
    fun testSupports() {
        val supportsAllOneTwentyOne = MinecraftVersionSupportRange(
            SupportMarker.GreaterThan,
            MinecraftVersionType(1, 21, 4)
        )
        assertEquals(
            true,
            supportsAllOneTwentyOne.supports(MinecraftVersionType(1, 21, 4))
        )
        assertEquals(
            true,
            supportsAllOneTwentyOne.supports(MinecraftVersionType(1, 21, 5))
        )
        assertEquals(
            false,
            supportsAllOneTwentyOne.supports(MinecraftVersionType(1, 21, 3))
        )
    }

    @Test
    fun testDualVersionGate() {
        // The mixin gate partitions cleanly at 26.2: "<26.2" for the legacy (26.1.x)
        // variant and ">26.2" for the modern (26.2+) variant. They must not overlap.
        val legacy = MinecraftVersionSupportRange.parse("<26.2")
        val modern = MinecraftVersionSupportRange.parse(">26.2")

        // Versions are parsed the same way the plugin parses FabricLoader's reported
        // Minecraft version at runtime, so the patch component matches (parse fills an
        // absent patch with 0).

        // 26.1 and its patch releases -> legacy only
        for (v in listOf("26.1", "26.1.2").map { MinecraftVersionType.parse(it) }) {
            assertEquals(true, legacy.supports(v))
            assertEquals(false, modern.supports(v))
        }

        // 26.2 (and later) -> modern only, never legacy
        for (v in listOf("26.2", "26.3").map { MinecraftVersionType.parse(it) }) {
            assertEquals(false, legacy.supports(v))
            assertEquals(true, modern.supports(v))
        }
    }
}