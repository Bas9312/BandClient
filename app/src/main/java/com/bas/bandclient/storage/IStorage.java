package com.bas.bandclient.storage;

import java.lang.reflect.Type;

/**
 * Created by Владимир on 10.05.2017.
 */

public interface IStorage {

    void put(String key, Object obj);
    <T> T get(String key, Class<T> clz);
    <T> T get(String key, Type clz);
    void remove(String key);
    void clear();
}
