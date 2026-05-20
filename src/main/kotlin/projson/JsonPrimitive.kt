package projson

/**
 * Represents a JSON string.
 */
data class JsonString(val value: String) : JsonValue {

    override fun stringify(): String {
        return "\"${escape(value)}\""
    }

    private fun escape(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
    }
}

/**
 * Represents a JSON number.
 */
data class JsonNumber(val value: Number) : JsonValue {

    override fun stringify(): String {
        return value.toString()
    }
}

/**
 * Represents a JSON boolean.
 */
data class JsonBoolean(val value: Boolean) : JsonValue {

    override fun stringify(): String {
        return value.toString()
    }
}

/**
 * Represents JSON null.
 */
object JsonNull : JsonValue {

    override fun stringify(): String {
        return "null"
    }
}