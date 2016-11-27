# youibot
robot
1. UART利用科大讯飞的接口
导入时需注意在build.gradle添加
externalNativeBuild {
        ndkBuild {
            path file("src\\main\\jni\\Android.mk")
        }
    }
