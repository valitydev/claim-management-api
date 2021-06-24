package com.rbkmoney.claimmanagementapi.util

import com.rbkmoney.claimmanagementapi.controller.AuthorizedOperation

object ReflectionUtils {

    fun getOperationId(): String {
        val operationAnnotation = AuthorizedOperation::class.java
        val operationMethod = StackWalker.getInstance()
            .walk { frame ->
                frame.map { it.toMethod() }
                    .filter { it.isAnnotationPresent(operationAnnotation) }
                    .findFirst()
            }.orElseThrow { RuntimeException("Can't resolve bouncer operation") }
        return operationMethod.name
    }

    private fun StackWalker.StackFrame.toMethod() =
        Class.forName(this.className)
            .declaredMethods
            .find { it.name == this.methodName }!!
            .also { it.trySetAccessible() }
}
