package org.kock

import java.time.LocalDateTime

class InvocationDetails(
    val obj: Any,
    val methodName: String,
    val arguments: Array<Any>
) {
    val time: LocalDateTime = LocalDateTime.now()!!
}
