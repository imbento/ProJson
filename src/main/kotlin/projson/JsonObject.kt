package projson

/**
 * Represents a JSON object.
 */
class JsonObject : JsonValue {

    private val properties = linkedMapOf<String, JsonValue>()

    /**
     * Adds or modifies a property.
     */
    fun setProperty(name: String, value: Any?) {
        require(name.isNotBlank()) { "Property name cannot be blank." }
        properties[name] = ProJson().toJson(value)
    }

    /**
     * Adds or modifies a property already converted to JsonValue.
     */
    internal fun setJsonProperty(name: String, value: JsonValue) {
        require(name.isNotBlank()) { "Property name cannot be blank." }
        properties[name] = value
    }

    /**
     * Removes a property.
     */
    fun removeProperty(name: String) {
        properties.remove(name)
    }

    /**
     * Gets a property.
     */
    fun getProperty(name: String): JsonValue? {
        return properties[name]
    }

    /**
     * Returns all properties.
     */
    fun getAll(): Map<String, JsonValue> {
        return properties
    }

    /**
     * Traverses this object and its children.
     */
    fun traverse(action: (JsonValue) -> Unit) {
        action(this)

        for (value in properties.values) {
            when (value) {
                is JsonObject -> value.traverse(action)
                is JsonArray -> value.traverse(action)
                else -> action(value)
            }
        }
    }

    /**
     * Converts JsonObject into valid JSON text.
     *
     * Example:
     *
     * {
     *      "name":"Miguel"
     * }
     *
     * @return JSON representation
     */
    override fun stringify(): String {
        return properties.entries.joinToString(
            prefix = "{",
            postfix = "}",
            separator = ","
        ) { (key, value) ->
            "\"$key\":${value.stringify()}"
        }
    }

    /**
     * Converts object into text
     *
     * @return textual representation
     */
    override fun toString(): String {
        return stringify()
    }
}