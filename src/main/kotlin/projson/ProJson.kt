package projson
import java.util.IdentityHashMap
import java.util.UUID

/**
 * Main class of the ProJson library.
 *
 * This class is responsible for converting Kotlin values and objects
 * into JSON structures represented internally by JsonValue classes.
 *
 * Supported conversions:
 * - Primitive values (String, Number, Boolean, null)
 * - Collections -> JsonArray
 * - Maps -> JsonObject
 * - Kotlin objects through reflection
 *
 * Additional Phase 2 features:
 * - Object identifiers ($id)
 * - References ($ref)
 * - Field annotations
 *      - @Reference
 *      - @JsonProperty
 *      - @JsonIgnore
 * - Pretty printed JSON output
 */

class ProJson {

    /**
     * Stores generated identifiers for objects.
     *
     * IdentityHashMap compares objects by memory identity,
     * avoiding duplicated ids for the same instance.
     */

    private val objectIds = IdentityHashMap<Any, String>()

    /**
     * Converts any Kotlin value into a JsonValue.
     *
     * Depending on the type, ProJson creates:
     *
     * - JsonNull
     * - JsonString
     * - JsonNumber
     * - JsonBoolean
     * - JsonArray
     * - JsonObject
     *
     * @param value value to convert
     * @return JSON representation of the value
     */
    fun toJson(value: Any?): JsonValue {
        return when (value) {
            null -> JsonNull
            is JsonValue -> value
            is String -> JsonString(value)
            is Number -> JsonNumber(value)
            is Boolean -> JsonBoolean(value)
            is Map<*, *> -> mapToJsonObject(value)
            is Collection<*> -> collectionToJsonArray(value)
            else -> objectToJsonObject(value)
        }
    }

    /**
     * Converts a Kotlin value directly into formatted JSON text.
     *
     * @param value value to serialize
     * @return pretty printed JSON string
     */
    fun toJsonString(value: Any?): String {
        return prettyPrint(toJson(value))
    }

    /**
     * Converts a Kotlin collection into JsonArray.
     *
     * Each element is recursively converted.
     *
     * Example: listOf("a",1,true) becomes:["a",1,true]
     *
     * @param collection collection to convert
     * @return JsonArray representation
     */
    private fun collectionToJsonArray(collection: Collection<*>): JsonArray {
        val array = JsonArray()

        for (element in collection) {
            array.addJson(toJson(element))
        }

        return array
    }

    /**
     * Converts a Kotlin map into JsonObject.
     *
     * JSON objects require string keys.
     *
     * Example:
     * mapOf(
     *      "name" to "Miguel"
     * )
     *
     * becomes:
     *
     * {
     *    "name":"Miguel"
     * }
     *
     * @param map map to convert
     * @return JsonObject representation
     */
    private fun mapToJsonObject(map: Map<*, *>): JsonObject {
        val obj = JsonObject()

        for ((key, value) in map) {
            require(key is String) { "JSON object keys must be strings." }
            obj.setJsonProperty(key, toJson(value))
        }

        return obj
    }

    /**
     * Converts a normal Kotlin object into JsonObject
     * using Java reflection.
     *
     * Reflection allows ProJson to inspect
     * fields automatically at runtime.
     *
     * Additional metadata:
     *
     * - $id
     * - $type
     *
     * Supported annotations:
     *
     * - @Reference
     * - @JsonProperty
     * - @JsonIgnore
     *
     * @param value object to convert
     * @return generated JsonObject
     */
    private fun objectToJsonObject(value: Any): JsonObject {
        val obj = JsonObject()

        obj.setJsonProperty("\$id", JsonString(getOrCreateId(value)))
        obj.setJsonProperty("\$type", JsonString(value.javaClass.simpleName))

        for (field in value.javaClass.declaredFields) {
            field.isAccessible = true

            if (field.isAnnotationPresent(JsonIgnore::class.java)) {
                continue
            }

            val fieldName =
                field.getAnnotation(JsonProperty::class.java)?.name ?: field.name

            val fieldValue = field.get(value)

            if (field.isAnnotationPresent(Reference::class.java)) {
                obj.setJsonProperty(fieldName, toReference(fieldValue))
            } else {
                obj.setJsonProperty(fieldName, toJson(fieldValue))
            }
        }

        return obj
    }

    /**
     * Converts an object into JSON reference(s).
     *
     * Example:
     *
     * {
     *      "$ref":"uuid"
     * }
     *
     * Collections generate multiple references.
     *
     * @param value referenced object
     * @return JsonReference or JsonArray of references
     */
    private fun toReference(value: Any?): JsonValue {
        return when (value) {
            null -> JsonNull

            is Collection<*> -> {
                val array = JsonArray()

                for (element in value) {
                    if (element == null) {
                        array.addJson(JsonNull)
                    } else {
                        array.addJson(JsonReference(getOrCreateId(element)))
                    }
                }

                array
            }

            else -> JsonReference(getOrCreateId(value))
        }
    }

    /**
     * Generates or retrieves a unique object identifier.
     *
     * The same object instance always receives
     * the same UUID.
     *
     * @param value object instance
     * @return object identifier
     */
    private fun getOrCreateId(value: Any): String {
        return objectIds.getOrPut(value) {
            UUID.randomUUID().toString()
        }
    }

    /**
     * Produces human-readable JSON formatting.
     *
     * Adds:
     *
     * - indentation
     * - line breaks
     * - nested formatting
     *
     * @param value JSON value
     * @param indent current indentation level
     * @return formatted JSON string
     */
    private fun prettyPrint(value: JsonValue, indent: String = ""): String {
        val nextIndent = indent + "    "

        return when (value) {
            is JsonObject -> {
                val properties = value.getAll()

                if (properties.isEmpty()) {
                    "{}"
                } else {
                    properties.entries.joinToString(
                        prefix = "{\n",
                        postfix = "\n$indent}",
                        separator = ",\n"
                    ) { (key, v) ->
                        "$nextIndent\"$key\": ${prettyPrint(v, nextIndent)}"
                    }
                }
            }

            is JsonArray -> {
                val elements = value.getAll()

                if (elements.isEmpty()) {
                    "[]"
                } else {
                    elements.joinToString(
                        prefix = "[\n",
                        postfix = "\n$indent]",
                        separator = ",\n"
                    ) {
                        "$nextIndent${prettyPrint(it, nextIndent)}"
                    }
                }
            }

            else -> value.stringify()
        }
    }
}