cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_VERBOSE_MAKEFILE on)

##################################################################
add_library(jnidatalearn SHARED
            com_chihun_learn_jnidemo_JNIDataLearn.cpp
            )

# Include libraries needed for jnilearn lib
target_link_libraries(jnidatalearn
                      android
                      log)

###################################################################
add_library(jniexceptionlearn SHARED
            com_chihun_learn_jnidemo_JNIExceptionLearn.cpp
            )

# Include libraries needed for jnilearn lib
target_link_libraries(jniexceptionlearn
                      android
                      log)