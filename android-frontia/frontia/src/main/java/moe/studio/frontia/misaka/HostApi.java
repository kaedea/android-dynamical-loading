/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia.misaka;

/**
 * 宿主API的基类，插件通过这些接口调用宿主的API；
 * 插件只能访问API接口，具体实现是透明的，由宿主通过{@link HostApiManager}注册；
 */
public interface HostApi {

}
