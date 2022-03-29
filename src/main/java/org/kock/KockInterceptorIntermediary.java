package org.kock;

import java.lang.reflect.Method;

public class KockInterceptorIntermediary {
    KockInterceptor kockInterceptor;

    public KockInterceptorIntermediary(Object spy) {
        this.kockInterceptor = new KockInterceptor(spy);
    }

    public KockInterceptor getKockInterceptor() {
        return kockInterceptor;
    }

    public void setKockInterceptor(KockInterceptor kockInterceptor) {
        this.kockInterceptor = kockInterceptor;
    }

    public Object invoke(Object mock, Method method, Object[] args) {
        return kockInterceptor.invoke(mock, method, args);
    }
}
