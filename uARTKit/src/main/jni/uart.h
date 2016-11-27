/**
 * @file    cae_app.h
 * 
 * @version 1.0
 * @date    2015/10/15
 * 
 * @see        
 * 
 * History:
 * index    version        date            author        notes
 * 0        1.0            2015/10/15      kunzhang      Create this file
 */

#ifndef __UART_H__
#define __UART_H__

#ifdef __cplusplus
extern "C" {
#endif /* C++ */

/* ------------------------------------------------------------------------
** Types
** ------------------------------------------------------------------------ */
typedef void ( * uart_rec_fn)(const void *data, unsigned int datalen, void *user_data);
typedef void* UART_HANDLE;


/* ------------------------------------------------------------------------
** Function
** ------------------------------------------------------------------------ */
/** 
 * @fn		 
 * @brief	 
 * 
 *   
 * @return	 
 * @param	 
 * @param	 
 * @param	 
 * @see		
 */
 int uart_init(UART_HANDLE *uart_hd, const char *device, int rate,uart_rec_fn uart_rec_cb, void *user_data);


/** 
 * @fn		
 * @brief	
 *  
 * 
 */
int uart_send(UART_HANDLE uart_hd, const char *msg, int msglen);


 /** 
 * @fn		
 * @brief	
 *  
 * 
 
 */
void uart_uninit(UART_HANDLE uart_hd);



#ifdef __cplusplus
} /* extern "C" */	
#endif /* C++ */

#endif /* __UART_H__ */



