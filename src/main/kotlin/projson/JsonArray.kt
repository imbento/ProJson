package projson

/**
 * Represents a JSON array.
 */
class JsonArray : JsonValue {

    private val elements = mutableListOf<JsonValue>()

    /**
     * Adds a value to the array.
     */
    fun add(value: Any?) {
        elements.add(ProJson().toJson(value))
    }

    /**
     * Adds an already converted JsonValue.
     */
    internal fun addJson(value: JsonValue) {
        elements.add(value)
    }

    /**
     * Removes an element by index.
     */
    fun removeAt(index: Int) {
        elements.removeAt(index)
    }

    /**
     * Modifies an element by index.
     */
    fun modifyAt(index: Int, value: Any?) {
        require(index in elements.indices) { "Invalid index: $index" }
        elements[index] = ProJson().toJson(value)
    }

    /**
     * Gets an element by index.
     */
    fun get(index: Int): JsonValue {
        return elements[index]
    }

    /**
     * Returns all elements.
     */
    fun getAll(): List<JsonValue> {
        return elements
    }

    /**
     * Traverses this array and its children.
     */
    fun traverse(action: (JsonValue) -> Unit) {
        action(this)

        for (value in elements) {
            when (value) {
                is JsonObject -> value.traverse(action)
                is JsonArray -> value.traverse(action)
                else -> action(value)
            }
        }
    }


    /**
     * Converts JsonArray into
     * valid JSON text.
     *
     * Example:
     *
     * [
     *      "Java",
     *      "Kotlin"
     * ]
     *
     * @return JSON representation
     */
    override fun stringify(): String {
        return elements.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ","
        ) { it.stringify() }
    }

    /**
     * Converts object into text.
     *
     * @return textual representation
     */
    override fun toString(): String {
        return stringify()
    }
}