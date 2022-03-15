package org.kock;

public interface KockInterceptable {
    void setInterceptor(KockInterceptorIntermediary interceptor);
    KockInterceptorIntermediary getInterceptor();
}