package org.example.onepiece.utils;

public interface ILock {

    boolean tryLock(long timeoutSec);

    void unlock();

}
