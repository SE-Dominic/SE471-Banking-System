package com.se370group1.banking_system.service.command;

import org.springframework.stereotype.Component;

@Component
public class CommandInvoker {

    public boolean executeCommand(Command command) {  // Boolean → boolean

        return command.execute();
    }
}