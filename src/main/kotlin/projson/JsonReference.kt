package projson

/**
 * Represents a JSON reference to another JSON object.
 *
 * Example:
 * { "$ref": "uuid" }
 *
 * @property id identifier of the referenced object
 */
data class JsonReference(val id: String) : JsonValue {

    override fun stringify(): String {
        return "{\"\$ref\":\"$id\"}"
    }
}