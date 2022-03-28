package org.kock;

import java.lang.reflect.Method;

public class KockInterceptorIntermediary {
    public KockInterceptor getKockInterceptor() {
        return kockInterceptor;
    }

    public void setKockInterceptor(KockInterceptor kockInterceptor) {
        this.kockInterceptor = kockInterceptor;
    }

    KockInterceptor kockInterceptor;

    public KockInterceptorIntermediary() {
        this.kockInterceptor = new KockInterceptor();
    }

    public Object invoke(Object mock, Method method, Object[] args) {
        return kockInterceptor.invoke(mock, method, args);
    }
}
