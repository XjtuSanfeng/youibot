/* =========================================================================

DESCRIPTION
   uart

Copyright (c) 2015 by  USTC iFLYTEK, Co,LTD.  All Rights Reserved.
============================================================================ */

/* =========================================================================

                             REVISION

when            who              why
--------        ---------        -------------------------------------------
2015/10/15     kunzhang        Created.
============================================================================ */

/* ------------------------------------------------------------------------
** Includes
** ------------------------------------------------------------------------ */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include <termios.h>
#include <stdlib.h>
#include <pthread.h>
#include <termios.h>
#include <stdarg.h>
#include "uart.h"
#include "entry.h"


/* ------------------------------------------------------------------------
** Macros
** ------------------------------------------------------------------------ */
#define FALSE 0
#define TRUE 1

/* ------------------------------------------------------------------------
** Defines
** ------------------------------------------------------------------------ */

#define DBG_LOG_TRACE( format, ... ) uart_log( format, ## __VA_ARGS__ );uart_log("\n")
#define DBG_LOG_DEBUG( format, ... ) uart_log( format, ## __VA_ARGS__ );uart_log("\n")
#define DBG_LOG_ERROR( format, ... ) uart_log( format, ## __VA_ARGS__ );uart_log("\n")
#define DBG_LOG_WARNING( format, ... ) uart_log( format, ## __VA_ARGS__ );uart_log("\n")

/* ------------------------------------------------------------------------
** Types
** ------------------------------------------------------------------------ */

typedef struct _uartData{
	uart_rec_fn uart_rec_cb;
	int fd;
	pthread_t thread_recv;
	void *thread_status;
	int running;
	void *user_data;
} uartData;
/* ------------------------------------------------------------------------
** Global Variable Definitions
** ------------------------------------------------------------------------ */
                             

/* ------------------------------------------------------------------------
** Function Definitions
** ------------------------------------------------------------------------ */

void* uart_recv(void* arg)
{
	uartData *uart = (uartData *)arg;
	char revbuff[256];
	int revlen = 0;
	while(uart->running){
		memset(revbuff, 0, sizeof(revbuff));

		if((revlen = read(uart->fd,revbuff,sizeof(revbuff))) == -1){
			DBG_LOG_ERROR("uart_recv error:%d", errno);
			continue;
		}
		uart->uart_rec_cb(revbuff, revlen, uart->user_data);
	}
	return NULL;
}



/*******************************************************************
* 名称：            UART_Set
* 功能：            设置串口数据位，停止位和效验位
* 入口参数：        fd         串口文件描述符
*                   speed      串口速度
*                   flow_ctrl  数据流控制
*                   databits   数据位   取值为 7 或者8
*                   stopbits   停止位   取值为 1 或者2
*                   parity     效验类型 取值为N,E,O,,S
*出口参数：         正确返回为1，错误返回为0
*******************************************************************/
int UART_Set(int fd,int speed,int flow_ctrl,int databits,int stopbits,int parity)
{
      int   i;
      int   status;
      int   speed_arr[] = { B115200, B38400, B19200, B9600, B4800, B2400, B1200, B300,
                            B115200, B38400, B19200, B9600, B4800, B2400, B1200, B300 };
      int   name_arr[] = {115200, 38400,  19200,  9600,  4800,  2400,  1200,  300,
                          115200, 38400, 19200,  9600, 4800, 2400, 1200,  300 };
         
    struct termios options;
   
/*
	tcgetattr(fd,&options)得到与fd指向对象的相关参数，并将它们保存于options。
	该函数,还可以测试配置是否正确，该串口是否可用等。
	若调用成功，函数返回值为0，若调用失败，函数返回值为1.
*/

//获取设备属性信息
    if  ( tcgetattr( fd, &options )  !=  0)
       {
          perror("SetupSerial 1");    
          return FALSE;          
       }
  
    //设置串口输入波特率和输出波特率 /i o 入和出
    for ( i= 0;  i < sizeof(speed_arr) / sizeof(int);  i++)
    {
        if  (speed == name_arr[i])
        {       
            cfsetispeed(&options, speed_arr[i]); 
            cfsetospeed(&options, speed_arr[i]);  
        }
    }     
   
    //修改控制模式，保证程序不会占用串口
    options.c_cflag |= CLOCAL;
    //修改控制模式，使得能够从串口中读取输入数据
    options.c_cflag |= CREAD;
  
    //设置数据流控制
    switch(flow_ctrl)
    {
      
       case 0 ://不使用流控制
              options.c_cflag &= ~CRTSCTS;
              break;   
      
       case 1 ://使用硬件流控制
              options.c_cflag |= CRTSCTS;
              break;
       case 2 ://使用软件流控制
              options.c_cflag |= IXON | IXOFF | IXANY;
              break;
    }
    //设置数据位
    options.c_cflag &= ~CSIZE; //屏蔽其他标志位
    switch (databits)
    {  
       case 5 :
              options.c_cflag |= CS5;
              break;
       case 6 :
              options.c_cflag |= CS6;
              break;
       case 7 :    
              options.c_cflag |= CS7;
              break;
       case 8 :    
              options.c_cflag |= CS8;
              break;  
       default :   
              fprintf(stderr,"Unsupported data size/n");
              return FALSE; 
    }
    //设置校验位
    switch (parity)
    {  
       case 'n':
       case 'N': //无奇偶校验位。
                 options.c_cflag &= ~PARENB; 
                 options.c_iflag &= ~INPCK;    
                 break; 
       case 'o':  
       case 'O'://设置为奇校验
                 options.c_cflag |= (PARODD | PARENB); 
                 options.c_iflag |= INPCK;             
                 break; 
       case 'e': 
       case 'E'://设置为偶校验
                 options.c_cflag |= PARENB;       
                 options.c_cflag &= ~PARODD;       
                 options.c_iflag |= INPCK;       
                 break;
       case 's':
       case 'S': //设置为空格
                 options.c_cflag &= ~PARENB;
                 options.c_cflag &= ~CSTOPB;
                 break; 
        default:  
                 fprintf(stderr,"Unsupported parity/n");   
                 return FALSE; 
    } 
    // 设置停止位
    switch (stopbits)
    {  
       case 1:   
                 options.c_cflag &= ~CSTOPB; 
                 break; 
       case 2:   
                 options.c_cflag |= CSTOPB; 
                 break;
       default:   
                 fprintf(stderr,"Unsupported stop bits/n"); 
                 return FALSE;
    }
   
    //修改输出模式，原始数据输出
    options.c_oflag &= ~OPOST;

    options.c_lflag |= ~(ICANON|ECHOE|ECHO|ISIG);
		//(ICANON | ECHO | ECHOE);    

   
    //设置等待时间和最小接收字符
    options.c_cc[VTIME] = 1; /* 读取一个字符等待1*(1/10)s */
    options.c_cc[VMIN] = 1; /* 读取字符的最少个数为1 */
   
    //如果发生数据溢出，接收数据，但是不再读取
    tcflush(fd,TCIFLUSH);
   
    //激活配置 (将修改后的termios数据设置到串口中）
    if (tcsetattr(fd,TCSANOW,&options) != 0)  
    {
       perror("com set error!/n");  
       return FALSE; 
    }
    return TRUE; 
}

//uart初始化
int uart_init(UART_HANDLE *uart_hd, const char *device,int rate, uart_rec_fn uart_rec_cb, void *user_data)
{
	int ret = 0;
	uartData *uart = NULL;
	void * unused;
	pthread_attr_t attr;
	DBG_LOG_TRACE("uart_init(%s,%x,%x)[in]", device, uart_rec_cb, user_data);
	//句柄检查
	if (NULL == device || NULL == uart_rec_cb || NULL == uart_hd){
		DBG_LOG_ERROR("NULL == device || NULL == uart_read_cb");
		return -1;
	}
	//申请结构体
	uart = (uartData*)malloc(sizeof(uartData));
	if (NULL == uart){
		ret = -1;
		goto error;
	}
	memset(uart, 0, sizeof(uartData));
	uart->uart_rec_cb = uart_rec_cb;
	uart->user_data = user_data;
	//打开设备
	uart->fd = open(device,O_RDWR);
	if(uart->fd < 0){
		DBG_LOG_ERROR("open failed: %s", device);
		ret = -1;
		goto error;
	}
	//设定属性
	if (UART_Set(uart->fd, rate, 0, 8, 1, 'N') == FALSE){   
		ret = -1;                                                  
        goto error;
    }
	uart->running = 1;
	
	//创建线程，接收数据
	pthread_attr_init(&attr);
    pthread_attr_setdetachstate(&attr,PTHREAD_CREATE_DETACHED);//分离属性
	ret = pthread_create(&(uart->thread_recv), &attr,(void *)uart_recv, uart);
	if(0 != ret){
		DBG_LOG_ERROR("uart recv thread create failed!\n");
		ret = -1;
		goto error;
	}
	*uart_hd = uart;

	goto exit;
error:
	if (NULL != uart){
		uart->running = 0;
		if (uart->fd > 0){
			close(uart->fd);
		}
		if (NULL != uart->thread_recv){
			pthread_join(uart->thread_recv, &unused);
		}
		free(uart);
	}

exit:
	DBG_LOG_TRACE("uart_init()%d[out]", ret);
	return ret;
}

int uart_send(UART_HANDLE uart_hd, const char *msg, int msglen)
{
	int ret = 0;
	int sended_len = 0;
	uartData *uart = (uartData *)uart_hd;
	if (NULL == uart_hd){
		return -1;
	}
	if(0){
		int i = 0;
		for(i=0;i<msglen;i++)
		{
			DBG_LOG_DEBUG("%x ",msg[i]);
		}
		DBG_LOG_DEBUG("---send\n");
	}
	//反复发送，直到全部发完
	while(sended_len != msglen){
		int retlen = 0;
		retlen = write(uart->fd, msg + sended_len, msglen - sended_len);
		DBG_LOG_DEBUG("uart write msg, len:%d\n", retlen);
		if (retlen < 0){
			DBG_LOG_ERROR("uart_send failed! ret=%d\n", ret);
			return -1;
		}
		sended_len += retlen;
	}
	//DBG_LOG_TRACE("uart_send()%d[out]", ret);
	return ret;
}

void uart_uninit(UART_HANDLE uart_hd)
{
	void * unused;
	DBG_LOG_TRACE("uart_uninit(%x)[in]", uart_hd);
	if (NULL != uart_hd){
		uartData *uart = (uartData *)uart_hd;
		//关闭运行状态
		uart->running = 0;
		if (uart->fd > 0){
			//关闭设备
			close(uart->fd);
		}
		//关闭接收线程
		printf("before join\n");
//		if (NULL != uart->thread_recv){
//			pthread_cancel(uart->thread_recv);
//		}
		printf("after join\n");
		//释放结构体
		free(uart);
	}
	DBG_LOG_TRACE("uart_uninit()[out]");
}
