package projson

/**
 * Indicates that a field should be serialized as a JSON reference.
 *
 * Instead of serializing the full object, ProJson generates:
 * { "$ref": "object-id" }
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Reference