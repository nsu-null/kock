package org.kock

import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.FieldAccessor
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers
import org.objenesis.ObjenesisStd

inline fun <reified T> createKock() : T {
    return KockCreator().create(T::class.java)
//    val mockedClass = ByteBuddy()
//        .subclass(T::class.java)
//        .method(ElementMatchers.any())
//        .intercept(MethodDelegation.to(InterceptorDelegate::class.java))
//        .defineField(
//            "interceptor",
//            KockInterceptorIntermediary::class.java,
//            Visibility.PUBLIC
//        ) // TODO make interceptorPrivate
//        .implement(KockInterceptable::class.java)
//        .intercept(FieldAccessor.ofBeanProperty())
//        .make()
//        .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.WRAPPER).loaded
//
//    val objenesis = ObjenesisStd()
//    val result = objenesis.newInstance(mockedClass)
//    (result as KockInterceptable).interceptor = KockInterceptorIntermediary()
//    return result
}
