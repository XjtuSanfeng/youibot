#include <entry.h>
#include <uart.h>
#include <string.h>
#include <stddef.h>
#include <malloc.h>
#include <stdlib.h>

static JavaVM* global_vm = NULL;
static JNIEnv* rec_thread_env = NULL;
static jclass connector_class;
static jmethodID connector_recv_method;
static UART_HANDLE uart_hd;
static const char* connector_cls_name = "com/iflytek/aiui/uartkit/core/UARTConnector";
static JNINativeMethod native_methods[] = {
		{"init", "(Ljava/lang/String;I)I", (void*)init},
		{"send", "([B)I", (void*)send},
		{"destroy", "()V", (void*)uninit}
};
#define RECV_BUF_LEN 12
#define MSG_NORMAL_LEN 4
#define MSG_EXTRA_LEN 8
#define PACKET_LEN_BIT 4
#define SYNC_HEAD 0xa5
#define SYNC_HEAD_SECOND 0x01

static int recv_index = 0;
static char recv_buf[RECV_BUF_LEN];
static int big_buf_len = 0;
static int big_buf_index = 0;
static void* big_buf = NULL;


jint JNI_OnLoad(JavaVM* vm, void* reserved){
	global_vm = vm;
	JNIEnv* tempEnv;
	if ((*vm)->GetEnv(vm, &tempEnv, JNI_VERSION_1_6) != JNI_OK) {
		return JNI_ERR;
	}

	jclass cls = (*tempEnv)->FindClass(tempEnv, connector_cls_name);
	if (cls == NULL) {
		return JNI_ERR;
	}

	if ((*tempEnv)->RegisterNatives(tempEnv, cls, native_methods, sizeof(native_methods) / sizeof(native_methods[0])) < 0) {
		return JNI_ERR;
	}

	connector_class = (*tempEnv)->NewGlobalRef(tempEnv, cls);
	connector_recv_method = (*tempEnv)->GetStaticMethodID(tempEnv, connector_class, "onReceive", "([B)V");
	return JNI_VERSION_1_6;
}

void uart_rec(const void *msg, unsigned int msglen, void *user_data)
{
	uart_log("uart recv %d", msglen);
	if(uart_hd == NULL)
	{
		uart_err("uart has already destroy");
		return;
	}

	if(big_buf == NULL && recv_index + msglen >= 2){
		if(recv_index == 0){
			if(((char*)msg)[0] != SYNC_HEAD | ((char*)msg)[1] != SYNC_HEAD_SECOND){
				uart_err("recv data not SYNC HEAD, drop");
				return;
			}
		}else if(recv_index == 1){
			if(recv_buf[0] != SYNC_HEAD | ((char*)msg)[0] != SYNC_HEAD_SECOND){
				uart_err("recv data not SYNC HEAD, drop");
				recv_index = 0;
				return;
			}
		}
	}

	int copy_len;
	if(big_buf != NULL)
	{
		copy_len = big_buf_len - big_buf_index < msglen? big_buf_len - big_buf_index : msglen;
		memcpy(big_buf + big_buf_index, msg, copy_len);
		big_buf_index += copy_len;
		if(big_buf_index < big_buf_len) return;
	}else {
		copy_len = RECV_BUF_LEN - recv_index < msglen? RECV_BUF_LEN - recv_index : msglen;
		memcpy(recv_buf + recv_index, msg, copy_len);
		if((recv_index + copy_len) > PACKET_LEN_BIT) {
			int content_len = recv_buf[PACKET_LEN_BIT] << 8 | recv_buf[PACKET_LEN_BIT - 1];
			if(content_len != MSG_NORMAL_LEN){
				big_buf_index = 0;
				big_buf_len = content_len + MSG_EXTRA_LEN;
				big_buf = malloc(big_buf_len);
				uart_log("uart malloc buf %x len %d", (unsigned int)big_buf, big_buf_len);
				memset(big_buf, big_buf_len, 0);
				memcpy(big_buf, recv_buf, recv_index);
				big_buf_index += recv_index;
				recv_index = 0;
				return uart_rec(msg, msglen, user_data);
			}
		}
		recv_index += copy_len;
		if(recv_index < RECV_BUF_LEN) return;
	}

	if(rec_thread_env == NULL){
		(*global_vm)->AttachCurrentThread(global_vm, &rec_thread_env, NULL);
	}
	jbyteArray rec_data;
	if(big_buf != NULL){
		rec_data = (*rec_thread_env)->NewByteArray(rec_thread_env, big_buf_len);
		(*rec_thread_env)->SetByteArrayRegion(rec_thread_env, rec_data, 0, big_buf_len, big_buf);
		big_buf_len = 0;
		big_buf_index = 0;
		uart_log("uart free buf %x", (unsigned int)big_buf);
		free(big_buf);
		big_buf = NULL;
	}else{
		rec_data = (*rec_thread_env)->NewByteArray(rec_thread_env, RECV_BUF_LEN);
		(*rec_thread_env)->SetByteArrayRegion(rec_thread_env, rec_data, 0, RECV_BUF_LEN, recv_buf);
		recv_index = 0;
	}

	(*rec_thread_env)->CallStaticVoidMethod(rec_thread_env, connector_class, connector_recv_method, rec_data);
	(*rec_thread_env)->DeleteLocalRef(rec_thread_env, rec_data);

	//get more than we want(multi msg in on stream)
	if(copy_len < msglen){
		uart_log("multi msg in one stream left %d byte", msglen - copy_len);
		uart_rec(msg + copy_len, msglen - copy_len, user_data);
	}
}

jint init(JNIEnv* env, jclass cls, jstring device, jint speed)
{
	if(uart_hd != NULL)
	{
		uart_err("uart has already init");
		return 1;
	}
	const char* device_name = (*env)->GetStringUTFChars(env, device, NULL);
	uart_log("device name %s speed %d \n", device_name, speed);
	if(device_name == NULL){
		return -1;
	}
	jint ret = uart_init(&uart_hd, device_name, speed, uart_rec, NULL);
	(*env)->ReleaseStringUTFChars(env, device, device_name);
	return ret;
}

jint send(JNIEnv* env, jclass cls, jbyteArray data)
{
	if(uart_hd == NULL)
	{
		uart_err("uart has already destroyed");
		return 1;
	}
	jsize data_len = (*env)->GetArrayLength(env, data);
	jbyte* send_data = (*env)->GetByteArrayElements(env, data, NULL);
	if(send_data == NULL){
		return -1;
	}
	jint ret = uart_send(uart_hd, send_data, data_len);
	(*env)->ReleaseByteArrayElements(env, data, send_data, 0);
	return ret;
}

void uninit(JNIEnv* env, jclass cls)
{
	//FIXME cancel recv thread
	(*env)->DeleteGlobalRef(env, connector_class);
	uart_uninit(uart_hd);
	uart_hd = NULL;
}

void log_array(char *data, int len)
{
	int i;
	for(i = 0; i < len; i++)
	{
		uart_log("%d ", (int)(data[i]));
	}
}
