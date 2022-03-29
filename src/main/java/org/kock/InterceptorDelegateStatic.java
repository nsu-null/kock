package org.kock;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;

public class InterceptorDelegateStatic {
    //TODO make it look better
    private static final KockInterceptorIntermediary interceptorIntermediary = new KockInterceptorIntermediary(null);

    @RuntimeType
    public static Object interceptStatic(@This Object mock,
                                         @Origin Method invokedMethod,
                                         @AllArguments Object... args) {
        System.out.println("Intermediate interceptor was called");
        return interceptorIntermediary.invoke(mock, invokedMethod, args);
    }
}
