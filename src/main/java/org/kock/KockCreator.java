package org.kock;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.*;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import org.objenesis.ObjenesisStd;


import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.*;


public class KockCreator {

    public <T> T create(Class<T> targetClass, T spy) {
        ByteBuddyAgent.install();
        Class<? extends T> mockedClass = new ByteBuddy()
                .subclass(targetClass)
                .method(any())
                .intercept(MethodDelegation.to(InterceptorDelegate.class))
                .defineField("interceptor", KockInterceptorIntermediary.class, Visibility.PUBLIC) // TODO make interceptorPrivate
                .implement(KockInterceptable.class)
                .intercept(FieldAccessor.ofBeanProperty())
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        var objenesis = new ObjenesisStd();
        T result = objenesis.newInstance(mockedClass);
        ((KockInterceptable) result).setInterceptor(new KockInterceptorIntermediary(spy));
        return result;
    }

    public <T, V> void mockFinal(Class<T> targetClass, String method, V value) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Class<? extends T> mockedClass = new ByteBuddy()
                .redefine(targetClass)
                .method(named(method))
                .intercept(FixedValue.value(value))
                .make()
                .load(getClass().getClassLoader(), ClassReloadingStrategy.fromInstalledAgent())
                .getLoaded();


    }
}
