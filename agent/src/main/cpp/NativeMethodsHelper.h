//
// Created by Jakub Háva on 28/11/2016.
//

#ifndef DISTRACE_AGENT_NATIVEMETHODS_H
#define DISTRACE_AGENT_NATIVEMETHODS_H

#include <jni.h>
#include <string>
#include <map>
#include <vector>

namespace Distrace {

    /**
     * This namespace contains native methods we are registering
     */
    class NativeMethodsHelper{
    public:

        /**
         * Get storage type from the arguments
         */
        static jstring getSaverType(JNIEnv *jni, jobject thiz);

        /**
        * List of all native methods we defined
        */
        static std::map<std::string, std::vector<JNINativeMethod>> nativesPerClass;

        /**
         * Loads native methods for provided class
         */
        static void loadNativesFor(JNIEnv* jni, jclass klazz, std::string className);
    };

}


#endif //DISTRACE_AGENT_NATIVEMETHODS_H
