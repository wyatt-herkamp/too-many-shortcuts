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
package dev.kingtux.tms.api

import dev.kingtux.tms.api.modifiers.KeyModifier
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents


@Environment(EnvType.CLIENT)
class ModifierPrefixTextProvider(private val translationKey: String) {
    constructor(modifiers: KeyModifier) : this(modifiers.translationKey)

    fun getBaseText(variation: Variation): MutableComponent {
        return MutableComponent.create(variation.getTranslatableText(translationKey))
    }

    fun getText(variation: Variation): MutableComponent {
        val text = getBaseText(variation)
        if (variation == Variation.COMPRESSED) {
            text.append(COMPRESSED_SUFFIX)
        } else {
            text.append(SUFFIX)
        }
        return text
    }

    enum class Variation(private val translateKeySuffix: String) {
        COMPRESSED(".tiny"),
        TINY(".tiny"),
        SHORT(".short"),
        NORMAL("");

        fun getTranslatableText(translationKey: String): TranslatableContents {
            return TranslatableContents(translationKey + translateKeySuffix, null, emptyArray())
        }

        fun getNextVariation(amount: Int): Variation? {
            val targetOrdinal = ordinal + amount
            if (targetOrdinal < 0 || targetOrdinal >= VALUES.size) {
                return null
            }
            return VALUES[targetOrdinal]
        }

        val smaller: Variation?
            get() = getNextVariation(-1)

        companion object {
            // using this array for the values because it is faster than calling values() every time
            val VALUES: Array<Variation> = entries.toTypedArray()

            @JvmField
            val WIDEST: Variation = NORMAL
            val SMALLEST: Variation = COMPRESSED
        }
    }

    companion object {
        private val SUFFIX: Component = Component.literal(" + ")
        private val COMPRESSED_SUFFIX: Component = Component.literal("+")
    }
}
