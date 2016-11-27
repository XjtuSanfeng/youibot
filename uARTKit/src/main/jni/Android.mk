LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS += -DDEBUG

LOCAL_MODULE := uart_helper

LOCAL_SRC_FILES := uart.c entry.c

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_CFLAGS += -DDEBUG
#
#LOCAL_MODULE := uart_test
#
#LOCAL_SRC_FILES := uart.c uart_test.c
#
#LOCAL_LDLIBS := -llog
#
#include $(BUILD_EXECUTABLE)