package com.se370group1.banking_system.service.command;

import org.springframework.stereotype.Component;

@Component
public class CommandInvoker {

    public Boolean executeCommand(Command command) {
        return command.execute();
    }
}
