package projson

/**
 * Represents any JSON value.
 *
 * A JSON value can be:
 * - object
 * - array
 * - string
 * - number
 * - boolean
 * - null
 */

sealed interface JsonValue {

    /**
     * Converts the JSON value into valid JSON text.
     *
     * @return textual JSON representation
     */
    fun stringify(): String
}