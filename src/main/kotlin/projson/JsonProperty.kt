package projson

/**
 * Customizes the name of a field in the generated JSON object.
 *
 * Example:
 * @JsonProperty("desc")
 * val description: String
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonProperty(val name: String)