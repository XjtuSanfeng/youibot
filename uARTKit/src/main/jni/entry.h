#include <jni.h>
#include <android/log.h>

#ifdef DEBUG
#define uart_log(...) __android_log_print(ANDROID_LOG_WARN, "UART", ##__VA_ARGS__)
#else
#define uart_log(...)
#endif
#define uart_err(...) __android_log_print(ANDROID_LOG_ERROR, "UART", ##__VA_ARGS__)
void uart_rec(const void *msg, unsigned int msglen, void *user_data);
jint init(JNIEnv* env, jclass cls, jstring device, jint speed);
jint send(JNIEnv* env, jclass cls, jbyteArray data);
void uninit(JNIEnv* env, jclass cls);
void log_array(char *data, int len);
