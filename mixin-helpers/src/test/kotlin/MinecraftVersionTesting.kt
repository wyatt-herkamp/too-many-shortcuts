
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
            MinecraftVersionType(1, 21, 6,4),
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
}