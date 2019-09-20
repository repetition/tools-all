package com.tools.gui.process;

public class ProcessManager {

    private static FileBrowserProcess fileBrowserProcess = null;

    public static FileBrowserProcess getFileBrowserProcess(){
        if (fileBrowserProcess == null) {
            fileBrowserProcess = new FileBrowserProcess();
        }
        return fileBrowserProcess;
    }
}
