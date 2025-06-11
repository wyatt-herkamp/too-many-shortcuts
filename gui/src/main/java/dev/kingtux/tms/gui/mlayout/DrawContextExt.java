package dev.kingtux.tms.gui.mlayout;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public interface DrawContextExt {

    void tms$drawText(
            TextRenderer renderer,
            Text text,
            int x,
            int y,
            int color,
            boolean shadow
    );

    default void tms$drawText(
            TextRenderer renderer,
            Text text,
            int x,
            int y,
            int color
    ){
        tms$drawText(renderer, text, x, y, color, false);
    }

   default void tms$drawTextWithShadow(TextRenderer textRenderer, Text text, int x, int y, int color){
        tms$drawText(textRenderer, text, x, y, color, true);
   }
}
