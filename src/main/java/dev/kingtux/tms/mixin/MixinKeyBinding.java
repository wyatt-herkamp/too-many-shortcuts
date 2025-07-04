package dev.kingtux.tms.mixin;

import de.siphalor.amecs.KeyBindingManager;
import de.siphalor.amecs.NOPMap;
import dev.kingtux.tms.TooManyShortcutsCore;
import dev.kingtux.tms.api.ModifierPrefixTextProvider;
import dev.kingtux.tms.api.config.ConfigBindings;
import dev.kingtux.tms.api.modifiers.BindingModifiers;
import dev.kingtux.tms.api.modifiers.KeyModifier;
import dev.kingtux.tms.mlayout.IKeyBinding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static dev.kingtux.tms.api.UtilsKt.isDefaultBinding;

@Environment(EnvType.CLIENT)
@Mixin(KeyBinding.class)
@Debug(export = true)
public abstract class MixinKeyBinding implements IKeyBinding {
    // set it to a NOPMap meaning everything done with this map is ignored. Because setting it to null would cause problems
    // ... even if we remove the put in the KeyBinding constructor. Because maybe in the future this map is used elsewhere or a other mod uses it
    @Shadow
    @Final
    @Mutable
    private static Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS = NOPMap.nopMap();
    @Unique
    private final BindingModifiers keyModifiers = new BindingModifiers();
    @Shadow
    public InputUtil.Key boundKey;
    @Shadow
    @Final
    public InputUtil.Key defaultKey;
    @Unique
    short nextChildId = 0;
    @Shadow
    private boolean pressed;
    @Shadow
    @Final
    private String category;
    @Shadow
    @Final
    private String translationKey;
    @Unique
    private List<KeyBinding> children = null;
    @Unique
    private KeyBinding parent = null;
    @Shadow
    private int timesPressed;

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputUtil.Key keyCode, CallbackInfo callbackInfo) {
        KeyBindingManager.onKeyPressed(keyCode);
        callbackInfo.cancel();
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputUtil.Key keyCode, boolean pressed, CallbackInfo callbackInfo) {
        KeyBindingManager.setKeyPressed(keyCode, pressed);

        callbackInfo.cancel();
    }

    @Inject(method = "updatePressedStates", at = @At("HEAD"), cancellable = true)
    private static void updatePressedStates(CallbackInfo callbackInfo) {
        KeyBindingManager.updatePressedStates();
        callbackInfo.cancel();
    }

    @Inject(method = "updateKeysByCode", at = @At("HEAD"), cancellable = true)
    private static void updateKeysByCode(CallbackInfo callbackInfo) {
        KeyBindingManager.updateKeysByCode();
        callbackInfo.cancel();
    }

    @Inject(method = "unpressAll", at = @At("HEAD"), cancellable = true)
    private static void unpressAll(CallbackInfo callbackInfo) {
        KeyBindingManager.unpressAll();
        callbackInfo.cancel();
    }

    @Shadow
    protected abstract void reset();

    @Shadow public abstract boolean isUnbound();

    @Shadow public abstract boolean isDefault();

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At("RETURN"))
    private void onConstructed(String id, InputUtil.Type type, int defaultCode, String category, CallbackInfo callbackInfo) {
        KeyBindingManager.register((KeyBinding) (Object) this);
    }

    @Inject(method = "getBoundKeyLocalizedText", at = @At("TAIL"), cancellable = true)
    public void getLocalizedName(CallbackInfoReturnable<Text> callbackInfoReturnable) {
        Text name = boundKey.getLocalizedText();
        Text fullName;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        ModifierPrefixTextProvider.Variation variation = ModifierPrefixTextProvider.Variation.WIDEST;
        do {
            fullName = name;
            for (KeyModifier keyModifier : KeyModifier.getEntries()) {
                if (keyModifier == null) {
                    continue;
                }

                if (keyModifiers.isSet(keyModifier)) {
                    fullName = keyModifier.getTextProvider().getText(variation).append(fullName);
                }
            }
        } while ((variation = variation.getSmaller()) != null && textRenderer.getWidth(fullName) > 70);

        callbackInfoReturnable.setReturnValue(fullName);
    }

    @Inject(
            method = "matchesKey",
            at = @At("HEAD"),
            cancellable = true
    )
    public void matchesKey(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (tms$hasAlternatives()) {
            for (KeyBinding child : children) {
                if (child.matchesKey(keyCode, scanCode)) {
                    callbackInfoReturnable.setReturnValue(true);
                }
            }
        }
        if (!keyModifiers.isUnset() && !keyModifiers.equals(TooManyShortcutsCore.INSTANCE.getCurrentModifiers())) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(
            method = "matchesMouse",
            at = @At("HEAD"),
            cancellable = true
    )
    public void matchesMouse(int code, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (children != null && !children.isEmpty()) {
            for (KeyBinding child : children) {
                if (child.matchesMouse(code)) {
                    callbackInfoReturnable.setReturnValue(true);
                }
            }
        }
        if (!keyModifiers.isUnset() && !keyModifiers.equals(TooManyShortcutsCore.INSTANCE.getCurrentModifiers())) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "equals", at = @At("RETURN"), cancellable = true)
    public void equals(KeyBinding other, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        IKeyBinding iKeyBinding = (IKeyBinding) other;
        if (!keyModifiers.equals(iKeyBinding.tms$getKeyModifiers())) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "isDefault", at = @At("HEAD"), cancellable = true)
    public void isDefault(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(isDefaultBinding(((KeyBinding) (Object) this)));
    }


    @Inject(
            method = "isPressed",
            at = @At("RETURN"),
            cancellable = true
    )
    public void isPressedInjection(CallbackInfoReturnable<Boolean> cir) {
        if (!pressed && children != null && !children.isEmpty()) {
            for (KeyBinding child : children) {
                if (child.isPressed()) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(
            method = "reset",
            at = @At("RETURN")
    )
    private void resetInjection(CallbackInfo callbackInfo) {
        if (children != null && !children.isEmpty()) {
            for (KeyBinding child : children) {
                child.setPressed(false);
            }
        }
    }

    @Override
    public void tms$setKeyModifiers(BindingModifiers modifiers) {
        this.keyModifiers.set(modifiers);
    }

    @Override
    public BindingModifiers tms$getKeyModifiers() {
        return keyModifiers;
    }

    @Override
    public int tms$getTimesPressed() {
        return timesPressed;
    }

    @Override
    public void tms$incrementTimesPressed() {
        KeyBinding parent = ((IKeyBinding) this).tms$getParent();

        if (parent != null) {
            ((IKeyBinding) parent).tms$incrementTimesPressed();
        }
        timesPressed++;
    }

    @Override
    public void tms$setTimesPressed(int timesPressed) {
        this.timesPressed = timesPressed;
    }


    @Override
    public short tms$getNextChildId() {
        return nextChildId++;
    }

    @Override
    public void tms$setNextChildId(short nextChildId) {
        this.nextChildId = nextChildId;
    }


    @Override
    public KeyBinding tms$getParent() {
        return parent;
    }

    @Override
    public void tms$setParent(KeyBinding binding) {
        parent = binding;
    }

    @Override
    public List<KeyBinding> tms$getAlternatives() {
        return children;
    }

    @Override
    public int tms$getAlternativesCount() {
        if (children == null) {
            return 0;
        } else {
            return children.size();
        }

    }

    @Override
    public void tms$removeAlternative(KeyBinding binding) {
        if (children != null) {
            children.remove(binding);
        }
    }

    @Override
    public void tms$addAlternative(KeyBinding binding) {
        if (children == null) {
            children = new LinkedList<>();
        }
        children.add(binding);
    }

    @Override
    public int tms$getIndexInParent() {
        if (parent == null) {
            return 0;
        }
        return ((IKeyBinding) parent).tms$getAlternatives().indexOf((KeyBinding) (Object) this);
    }

    @Override
    public ConfigBindings tms$toConfig() {
        return new ConfigBindings(this.boundKey.getTranslationKey(), this.keyModifiers);
    }

    @Override
    public void tms$fromConfig(ConfigBindings configBindings) {
        this.boundKey = InputUtil.fromTranslationKey(configBindings.getKey());
        this.keyModifiers.set(configBindings.getModifiers());
    }

    @Override
    public boolean tms$hasAlternatives() {
        if (children == null) {
            return false;
        }
        return !children.isEmpty();
    }

    @Override
    public void tms$resetBinding(boolean resetAlternatives) {
        this.keyModifiers.unset();
        this.boundKey = this.defaultKey;
        MinecraftClient.getInstance().options.write();
        if (resetAlternatives && this.tms$hasAlternatives()) {
            for (KeyBinding child : children) {
                ((IKeyBinding) child).tms$resetBinding(true);
            }
        }
    }

    @Override
    public void tms$clearBinding(boolean clearAlternatives) {
        this.keyModifiers.unset();
        this.boundKey = InputUtil.UNKNOWN_KEY;
        MinecraftClient.getInstance().options.write();
        if (clearAlternatives && this.tms$hasAlternatives()) {
            for (KeyBinding child : children) {
                ((IKeyBinding) child).tms$clearBinding(true);
            }
        }
    }

    @Override
    public String tms$debugString() {
        String parent;
        if (this.parent == null) {
            parent = "null";
        } else if (this.parent instanceof IKeyBinding) {
            parent = ((IKeyBinding) this.parent).tms$debugString();
        } else {
            parent = this.parent.getTranslationKey();
        }
        return "KeyBinding{" +
                "pressed=" + pressed +
                ", category='" + category + '\'' +
                ", translationKey='" + translationKey + '\'' +
                ", children=" + children +
                ", nextChildId=" + nextChildId +
                ", parent=" + parent +
                ", timesPressed=" + timesPressed +
                ", keyModifiers=" + keyModifiers +
                ", boundKey=" + boundKey +
                ", defaultKey=" + defaultKey +
                '}';
    }

    /**
     * Support for mods who don't want to use the keybinding API for Fabric.
     *
     * @return the binding modifiers
     */
    @Unique
    public BindingModifiers amecs$getKeyModifiers() {
        return keyModifiers;
    }

    @Override
    public InputUtil.Key tms$getBoundKey() {
        return boundKey;
    }

    @Override
    public void tms$setBoundKey(InputUtil.Key key) {
        this.boundKey = key;
    }

    @Override
    public InputUtil.Key tms$getDefaultKey() {
        return defaultKey;
    }

    @Override
    public void tms$reset() {
        this.reset();
    }
    @Override
    public boolean tms$canBeReset() {
        return !this.isDefault();
    }
}
