package org.kock;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import org.objenesis.ObjenesisStd;


import static net.bytebuddy.matcher.ElementMatchers.any;


public class KockCreator {

    public <T> T create(Class<T> targetClass) {
        Class<? extends T> mockedClass = new ByteBuddy()
                .subclass(targetClass)
                .method(any())
                .intercept(MethodDelegation.to(InterceptorDelegate.class))
                .defineField("interceptor", KockInterceptorIntermediary.class, Visibility.PUBLIC) // TODO make interceptorPrivate
                .implement(KockInterceptable.class)
                .intercept(FieldAccessor.ofBeanProperty())
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();

        var objenesis = new ObjenesisStd();
        T result = objenesis.newInstance(mockedClass);
        ((KockInterceptable) result).setInterceptor(new KockInterceptorIntermediary());
        return result;
    }
}
