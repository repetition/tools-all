package com.tools.agent.process;


import org.junit.Test;

public class FileBrowserProcessTest {

    @Test
    public void processCommand() {

        FileBrowserProcess fileBrowserProcess = new FileBrowserProcess();

        fileBrowserProcess.processCommand(null,null);
    }
}
