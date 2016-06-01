//
// Created by Jakub Háva on 01/06/16.
//

#ifndef DISTRACE_AGENT_CORE_AGENTPARAMETERS_H
#define DISTRACE_AGENT_CORE_AGENTPARAMETERS_H

#include <string>
#include <map>

namespace Distrace {

    /**
     * This class represents arguments which can be passed to the native agent
     */
    class AgentArgs {

    public:
        static const std::string ARG_INSTRUMENTOR_JAR;
        static const std::string ARG_SOCKET_ADDRESS;
        static const std::string ARG_LOG_LEVEL;
        static const std::string ARG_LOG_DIR;

        /** get the internal arguments map */
        std::map<std::string, std::string> getArgsMap();

        /**
         * Get argument value and fail if the argument value is not set.
         */
        std::string get_arg_value(std::string arg_name);

        /**
         * Check if the arguments is set
         */
        bool is_arg_set(std::string arg_name);

        /**
         * Parse the arguments and store the parsed result in this class' internal map
         *
         * This method takes two arguments -  options string to be parsed and err_msg which is filled
         * with the error message in case of problem during the parsing
         */
        int parse_args(std::string options, std::string &err_msg);


    private:
        /** Internal arguments holder where key = arg name, value = arg value */
        std::map<std::string, std::string> args;

        /** Validates if argument log_level has correct format in case it is set */
        int validate_log_level(std::string &err_msg);

        /**
         * Check for mandatory arguments and in case of error fills err_msg with the error message which
         * can be further logged out
         */
        int check_for_mandatory_args(std::string &err_msg);
    };

}

#endif //DISTRACE_AGENT_CORE_AGENTPARAMETERS_H