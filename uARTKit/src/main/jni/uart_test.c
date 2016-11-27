#include <stdio.h>
#include <stdlib.h>
#include <stdio.h>
#include "uart.h"

#define UART_DEVICE "/dev/ttyS2"

void uart_rec(const void *msg, unsigned int msglen, void *user_data)
{
	printf("msg:%s\nlen=%d\n", msg, msglen);

}

int main(int argc, char* argv[])
{
	UART_HANDLE uart_hd;
	int ret = 0;
	printf("uart test\n");
	ret = uart_init(&uart_hd, UART_DEVICE,115200, uart_rec, NULL);
	if (0 != ret)
	{
		printf("uart_init error ret = %d\n", ret);
	}
	while(1) 
	{
		uart_send(uart_hd, "0123456789",  10);
		sleep(3);
	}
		
}
