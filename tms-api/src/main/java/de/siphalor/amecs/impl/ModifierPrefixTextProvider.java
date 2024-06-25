/*
 * Copyright 2020-2023 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.amecs.impl;

import de.siphalor.amecs.api.KeyModifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

@Environment(EnvType.CLIENT)
public class ModifierPrefixTextProvider {
	private static final Text SUFFIX = Text.literal(" + ");
	private static final Text COMPRESSED_SUFFIX = Text.literal("+");
	private final String translationKey;

	public ModifierPrefixTextProvider(KeyModifier modifier) {
		this(modifier.getTranslationKey());
	}

	public ModifierPrefixTextProvider(String translationKey) {
		this.translationKey = translationKey;
	}

	protected MutableText getBaseText(Variation variation) {
		return MutableText.of(variation.getTranslatableText(translationKey));
	}

	public MutableText getText(Variation variation) {
		MutableText text = getBaseText(variation);
		if (variation == Variation.COMPRESSED) {
			text.append(COMPRESSED_SUFFIX);
		} else {
			text.append(SUFFIX);
		}
		return text;
	}

	public enum Variation {
		COMPRESSED(".tiny"),
		TINY(".tiny"),
		SHORT(".short"),
		NORMAL("");

		// using this array for the values because it is faster than calling values() every time
		public static final Variation[] VALUES = Variation.values();

		public static final Variation WIDEST = NORMAL;
		public static final Variation SMALLEST = COMPRESSED;

		public final String translateKeySuffix;

		Variation(String translateKeySuffix) {
			this.translateKeySuffix = translateKeySuffix;
		}

		public TranslatableTextContent getTranslatableText(String translationKey) {
			return new TranslatableTextContent(translationKey + translateKeySuffix, null, new Object[0]);
		}

		public Variation getNextVariation(int amount) {
			int targetOrdinal = ordinal() + amount;
			if (targetOrdinal < 0 || targetOrdinal >= VALUES.length) {
				return null;
			}
			return VALUES[targetOrdinal];
		}

		public Variation getSmaller() {
			return getNextVariation(-1);
		}
	}
}
