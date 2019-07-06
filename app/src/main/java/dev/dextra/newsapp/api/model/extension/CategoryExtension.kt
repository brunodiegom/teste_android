package dev.dextra.newsapp.api.model.extension

import dev.dextra.newsapp.api.model.enums.Category
import dev.dextra.newsapp.api.model.enums.Category.ALL

/**
 * Parses the [Category] input to value used on server request.
 */
fun Category.getValue(): String? =if (this != ALL) this.name.toLowerCase() else null