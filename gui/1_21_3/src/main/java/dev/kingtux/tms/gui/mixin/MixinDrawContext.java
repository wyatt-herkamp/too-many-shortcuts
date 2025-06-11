package dev.kingtux.tms.gui.mixin;

import dev.kingtux.tms.gui.mlayout.DrawContextExt;
import dev.kingtux.tms.mixin.helpers.MinecraftVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@MinecraftVersion(
        minecraftVersions = {"1.21.3"})
@Environment(EnvType.CLIENT)
@Mixin(DrawContext.class)
public abstract class MixinDrawContext implements DrawContextExt {
    @Shadow
    public abstract int drawText(
            TextRenderer renderer,
            Text text,
            int x,
            int y,
            int color,
            boolean shadow
    );
    public void tms$drawText(
            TextRenderer renderer,
            Text text,
            int x,
            int y,
            int color,
            boolean shadow
    ){
        this.drawText(renderer, text, x, y, color, shadow);
    }

}
