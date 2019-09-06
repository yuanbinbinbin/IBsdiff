# CMake的编译脚本配置文件

# 1. 标注需要支持的CMake最小版本
cmake_minimum_required(VERSION 3.4.1)

#设置生成的so动态库最后输出的路径
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})

file(GLOB bzip_source src/main/cpp/bzip/*.c)

# 2. add_library 定义需要编译的代码库 名称, 类型, 包含的源码
add_library(
             # Sets the name of the library.
             bsdiff-lib
             # Sets the library as a shared library.
             SHARED

             src/main/cpp/native-lib.cpp
              src/main/cpp/bspatch.c
              ${bzip_source})
include_directories(src/main/cpp/bzip)

# 3. find_library 定义当前代码库需要依赖的系统或者第三方库文件
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# 4. target_link_libraries设置最终编译的目标代码库
target_link_libraries( # Specifies the target library.
                       bsdiff-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )