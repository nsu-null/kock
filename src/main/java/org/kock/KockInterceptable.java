package org.kock;

public interface KockInterceptable {
    KockInterceptorIntermediary getInterceptor();

    void setInterceptor(KockInterceptorIntermediary interceptor);
}