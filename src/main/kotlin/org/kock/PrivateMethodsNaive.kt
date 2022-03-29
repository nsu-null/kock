package org.kock

inline fun <reified T> T.callPrivateByName(methodName: String, vararg args: Any?): Any? {
    val method = T::class.java.getDeclaredMethod(methodName, *args.map { it?.javaClass }.toTypedArray())
    method.isAccessible = true
    return method.invoke(this, *args.toList().toTypedArray())
}
