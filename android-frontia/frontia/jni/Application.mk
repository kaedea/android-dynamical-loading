STLPORT_FORCE_REBUILD := false
APP_STL := stlport_static
APP_CPPFLAGS += -fno-exceptions
APP_CPPFLAGS += -fno-rtti
GLOBAL_CFLAGS =   -fvisibility=hidden
APP_ABI := armeabi armeabi-v7a x86

APP_PLATFORM := android-9
