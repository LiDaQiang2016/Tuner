cmake_minimum_required(VERSION 3.22.1)

project("tunner")

add_library( tunner
             SHARED
             tunner.cpp mpm.cpp)

target_link_libraries(tunner log)
