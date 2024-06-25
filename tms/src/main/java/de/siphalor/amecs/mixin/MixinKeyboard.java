package de.siphalor.amecs.mixin;

import dev.kingtux.tms.TooManyShortcuts;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Keyboard.class)
public class MixinKeyboard {
	@ModifyVariable(
			method = "onKey",
			argsOnly = true,
			ordinal = 0,
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J")
	)
	public int modifyPressedKey(int key, long window, int key_, int scancode) {
		if (TooManyShortcuts.INSTANCE.getEscapeKeyBinding().matchesKey(key, scancode)) {
			return GLFW.GLFW_KEY_ESCAPE;
		}
		return key;
	}
}
