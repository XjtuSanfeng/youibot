# youibot

**1. 通过UART科大讯飞AIUI进行串口通讯**
导入时需注意在build.gradle添加，不然编译即使成功，运行的时候也会失败。
`externalNativeBuild {
         ndkBuild {
             path file("src\\main\\jni\\Android.mk")
         }
     }
`
androidStudio2.2 配置NDK的详细教程如下http://blog.csdn.net/jdh99/article/details/51765441

   
