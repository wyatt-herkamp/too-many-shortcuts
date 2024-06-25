/*
 * Copyright 2021 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.siphalor.amecs.mixin;

import de.siphalor.nmuk.impl.IKeyBinding;
import de.siphalor.nmuk.impl.NMUKKeyBindingHelper;
import dev.kingtux.tms.TooManyShortcuts;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mixin(value = GameOptions.class, priority = 800)
public class MixinGameOptions {
	@Unique
	private File nmukOptionsFile;
	@Unique
	private KeyBinding[] tempKeysAll;

	@Mutable
	@Shadow
	@Final
	public KeyBinding[] allKeys;

	// Prevent nmuk keybindings from getting saved to the Vanilla options file
	@Inject(
			method = "accept",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;allKeys:[Lnet/minecraft/client/option/KeyBinding;")
	)
	public void removeNMUKBindings(CallbackInfo ci) {
		tempKeysAll = allKeys;
		allKeys = Arrays.stream(allKeys).filter(binding -> !((IKeyBinding) binding).nmuk_isAlternative()).toArray(KeyBinding[]::new);
	}

	@Inject(
			method = "accept",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/sound/SoundCategory;values()[Lnet/minecraft/sound/SoundCategory;")
	)
	public void resetAllKeys(CallbackInfo ci) {
		allKeys = tempKeysAll;
	}

	@Inject(
			method = "write",
			at = @At("RETURN")
	)
	public void save(CallbackInfo ci) {
		try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(nmukOptionsFile), StandardCharsets.UTF_8))) {
			for (KeyBinding binding : allKeys) {
				if (((IKeyBinding) binding).nmuk_isAlternative()) {
					printWriter.println("key_" + binding.getTranslationKey() + ":" + binding.getBoundKeyTranslationKey());
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Inject(
			method = "load",
			at = @At("RETURN")
	)
	public void load(CallbackInfo ci) {
		if (nmukOptionsFile == null) {
			nmukOptionsFile = new File(MinecraftClient.getInstance().runDirectory, "options." + TooManyShortcuts.MOD_ID + ".txt");
		}

		if (!nmukOptionsFile.exists()) {
			return;
		}
		Map<String, KeyBinding> keyBindings = KeyBindingAccessor.getKeysById();
		Object2IntMap<KeyBinding> alternativeCountMap = new Object2IntOpenHashMap<>();
		Queue<KeyBinding> newAlternatives = new ConcurrentLinkedQueue<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(nmukOptionsFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					int stringIndex = line.lastIndexOf(':');
					if (stringIndex <= 0) {
						TooManyShortcuts.INSTANCE.log(Level.WARN, "Invalid nmuk options line: " + line);
						continue;
					}
					String id = line.substring(0, stringIndex);
					String keyId = line.substring(stringIndex + 1);
					if (!id.startsWith("key_")) {
						TooManyShortcuts.INSTANCE.log(Level.WARN, "Invalid nmuk options entry: " + id);
						continue;
					}
					id = id.substring(4);
					stringIndex = id.indexOf('%');
					if (stringIndex <= 0) {
						TooManyShortcuts.INSTANCE.log(Level.WARN, "Nmuk entry is missing an alternative id");
						continue;
					}
					short altId = Short.parseShort(id.substring(stringIndex + 1));
					id = id.substring(0, stringIndex);
					InputUtil.Key boundKey = InputUtil.fromTranslationKey(keyId);
					//noinspection ConstantConditions
					KeyBinding base = keyBindings.get(id);
					if (base != null) {
						int index = alternativeCountMap.getOrDefault(base, 0);
						List<KeyBinding> children = ((IKeyBinding) base).nmuk_getAlternatives();
						((IKeyBinding) base).nmuk_setNextChildId(altId);
						if (children == null) {
							KeyBinding alternative = NMUKKeyBindingHelper.createAlternativeKeyBinding(base);
							alternative.setBoundKey(boundKey);
							newAlternatives.add(alternative);
						} else {
							if (index < children.size()) {
								children.get(index).setBoundKey(boundKey);
							} else {
								KeyBinding alternative = NMUKKeyBindingHelper.createAlternativeKeyBinding(base);
								alternative.setBoundKey(boundKey);
								newAlternatives.add(alternative);
							}
						}
						alternativeCountMap.putIfAbsent(base, index + 1);
					}
				} catch (Throwable e) {
					TooManyShortcuts.INSTANCE.log(Level.ERROR, "Encountered an issue whilst loading nmuk options file!");
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		int newCount, oldCount;
		for (KeyBinding binding : allKeys) {
			newCount = alternativeCountMap.getOrDefault(binding, 0);
			oldCount = ((IKeyBinding) binding).nmuk_getAlternativesCount();
			if (oldCount > newCount) {
				List<KeyBinding> alternatives = ((IKeyBinding) binding).nmuk_getAlternatives();
				alternatives.subList(newCount, oldCount).clear();
			}
		}
		NMUKKeyBindingHelper.registerKeyBindings((GameOptions) (Object) this, newAlternatives);
	}
}
