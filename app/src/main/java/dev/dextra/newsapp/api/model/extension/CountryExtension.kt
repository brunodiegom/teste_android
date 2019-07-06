package dev.dextra.newsapp.api.model.extension

import dev.dextra.newsapp.api.model.enums.Country
import dev.dextra.newsapp.api.model.enums.Country.ALL

/**
 * Parses the [Country] input to value used on server request.
 */
fun Country.getSourceValue(): String? =if (this != ALL) this.name.toLowerCase() else null
