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
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static dev.kingtux.tms.api.UtilsKt.isDefaultBinding;

import com.mojang.blaze3d.platform.InputConstants;

@Environment(EnvType.CLIENT)
@Mixin(KeyMapping.class)
@Debug(export = true)
public abstract class MixinKeyBinding implements IKeyBinding {
    // set it to a NOPMap meaning everything done with this map is ignored. Because setting it to null would cause problems
    // ... even if we remove the put in the KeyBinding constructor. Because maybe in the future this map is used elsewhere or a other mod uses it
    @Shadow
    @Final
    @Mutable
    private static Map<InputConstants.Key, List<KeyMapping>> MAP = NOPMap.nopMap();
    @Unique
    private final BindingModifiers keyModifiers = new BindingModifiers();
    @Shadow
    public InputConstants.Key key;
    @Shadow
    @Final
    public InputConstants.Key defaultKey;
    @Unique
    short nextChildId = 0;
    @Shadow
    private boolean isDown;
    @Shadow
    @Final
    private KeyMapping.Category category;
    @Unique
    private List<KeyMapping> children = null;
    @Unique
    private KeyMapping parent = null;
    @Shadow
    private int clickCount;

    @Inject(method = "click", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputConstants.Key keyCode, CallbackInfo callbackInfo) {
        KeyBindingManager.onKeyPressed(keyCode);
        callbackInfo.cancel();
    }

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputConstants.Key keyCode, boolean pressed, CallbackInfo callbackInfo) {
        KeyBindingManager.setKeyPressed(keyCode, pressed);

        callbackInfo.cancel();
    }

    @Inject(method = "setAll", at = @At("HEAD"), cancellable = true)
    private static void updatePressedStates(CallbackInfo callbackInfo) {
        KeyBindingManager.updatePressedStates();
        callbackInfo.cancel();
    }

    @Inject(method = "resetMapping", at = @At("HEAD"), cancellable = true)
    private static void updateKeysByCode(CallbackInfo callbackInfo) {
        KeyBindingManager.updateKeysByCode();
        callbackInfo.cancel();
    }

    @Inject(method = "releaseAll", at = @At("HEAD"), cancellable = true)
    private static void unpressAll(CallbackInfo callbackInfo) {
        KeyBindingManager.unpressAll();
        callbackInfo.cancel();
    }

    @Shadow
    protected abstract void release();

    @Shadow public abstract boolean isUnbound();

    @Shadow public abstract boolean isDefault();

    @Shadow
    @Final
    private String name;


    @Inject(method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILnet/minecraft/client/KeyMapping$Category;)V", at = @At("RETURN"))
    private void onConstructed(String id, InputConstants.Type type, int defaultCode, KeyMapping.Category category, CallbackInfo callbackInfo) {
        KeyBindingManager.register((KeyMapping) (Object) this);
    }

    /**
     * @author Wyatt J Herkamp
     * @reason This mod takes control of keybinding execution.
     */
    @Overwrite
    private void registerMapping(InputConstants.Key key) {
        KeyBindingManager.register((KeyMapping) (Object) this, key);
    }

    @Inject(method = "getTranslatedKeyMessage", at = @At("TAIL"), cancellable = true)
    public void getLocalizedName(CallbackInfoReturnable<Component> callbackInfoReturnable) {
        Component name = key.getDisplayName();
        Component fullName;
        Font textRenderer = Minecraft.getInstance().font;
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
        } while ((variation = variation.getSmaller()) != null && textRenderer.width(fullName) > 70);

        callbackInfoReturnable.setReturnValue(fullName);
    }

    @Inject(
            method = "matches",
            at = @At("HEAD"),
            cancellable = true
    )
    public void matchesKey(KeyEvent key, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (tms$hasAlternatives()) {
            for (KeyMapping child : children) {
                if (child.matches(key)) {
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
    public void matchesMouse(MouseButtonEvent click, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (children != null && !children.isEmpty()) {
            for (KeyMapping child : children) {
                if (child.matchesMouse(click)) {
                    callbackInfoReturnable.setReturnValue(true);
                }
            }
        }
        if (!keyModifiers.isUnset() && !keyModifiers.equals(TooManyShortcutsCore.INSTANCE.getCurrentModifiers())) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "same", at = @At("RETURN"), cancellable = true)
    public void equals(KeyMapping other, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        IKeyBinding iKeyBinding = (IKeyBinding) other;
        if (!keyModifiers.equals(iKeyBinding.tms$getKeyModifiers())) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "isDefault", at = @At("HEAD"), cancellable = true)
    public void isDefault(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(isDefaultBinding(((KeyMapping) (Object) this)));
    }


    @Inject(
            method = "isDown",
            at = @At("RETURN"),
            cancellable = true
    )
    public void isPressedInjection(CallbackInfoReturnable<Boolean> cir) {
        if (!isDown && children != null && !children.isEmpty()) {
            for (KeyMapping child : children) {
                if (child.isDown()) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(
            method = "release",
            at = @At("RETURN")
    )
    private void resetInjection(CallbackInfo callbackInfo) {
        if (children != null && !children.isEmpty()) {
            for (KeyMapping child : children) {
                child.setDown(false);
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
        return clickCount;
    }

    @Override
    public void tms$incrementTimesPressed() {
        KeyMapping parent = ((IKeyBinding) this).tms$getParent();

        if (parent != null) {
            ((IKeyBinding) parent).tms$incrementTimesPressed();
        }
        clickCount++;
    }

    @Override
    public void tms$setTimesPressed(int timesPressed) {
        this.clickCount = timesPressed;
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
    public KeyMapping tms$getParent() {
        return parent;
    }

    @Override
    public void tms$setParent(KeyMapping binding) {
        parent = binding;
    }

    @Override
    public List<KeyMapping> tms$getAlternatives() {
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
    public void tms$removeAlternative(KeyMapping binding) {
        if (children != null) {
            children.remove(binding);
        }
    }

    @Override
    public void tms$addAlternative(KeyMapping binding) {
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
        return ((IKeyBinding) parent).tms$getAlternatives().indexOf((KeyMapping) (Object) this);
    }

    @Override
    public ConfigBindings tms$toConfig() {
        return new ConfigBindings(this.key.getName(), this.keyModifiers);
    }

    @Override
    public void tms$fromConfig(ConfigBindings configBindings) {
        this.key = InputConstants.getKey(configBindings.getKey());
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
        this.key = this.defaultKey;
        Minecraft.getInstance().options.save();
        if (resetAlternatives && this.tms$hasAlternatives()) {
            for (KeyMapping child : children) {
                ((IKeyBinding) child).tms$resetBinding(true);
            }
        }
    }

    @Override
    public void tms$clearBinding(boolean clearAlternatives) {
        this.keyModifiers.unset();
        this.key = InputConstants.UNKNOWN;
        Minecraft.getInstance().options.save();
        if (clearAlternatives && this.tms$hasAlternatives()) {
            for (KeyMapping child : children) {
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
            parent = this.parent.getName();
        }
        return "KeyBinding{" +
                "pressed=" + isDown +
                ", category='" + category + '\'' +
                ", translationKey='" + name + '\'' +
                ", children=" + children +
                ", nextChildId=" + nextChildId +
                ", parent=" + parent +
                ", timesPressed=" + clickCount +
                ", keyModifiers=" + keyModifiers +
                ", boundKey=" + key +
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
    public InputConstants.Key tms$getBoundKey() {
        return key;
    }

    @Override
    public void tms$setBoundKey(InputConstants.Key key) {
        this.key = key;
    }

    @Override
    public InputConstants.Key tms$getDefaultKey() {
        return defaultKey;
    }

    @Override
    public void tms$reset() {
        this.release();
    }
    @Override
    public boolean tms$canBeReset() {
        return !this.isDefault();
    }
}
