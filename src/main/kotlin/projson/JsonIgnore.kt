package projson

/**
 * Indicates that a field should be ignored during JSON generation.
 *
 * Fields annotated with this annotation are not included
 * in the final JSON object.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonIgnore