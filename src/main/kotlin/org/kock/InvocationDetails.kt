package org.kock

import java.time.LocalDateTime


class InvocationDetails<T>(
    var className: String,
    var methodName: String,
    var arguments: Array<Any>
) {
    val time: LocalDateTime = LocalDateTime.now()!!
}