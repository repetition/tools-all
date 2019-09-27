package com.tools.gui.process;

public class ProcessManager {

    private static FileBrowserProcess fileBrowserProcess = null;

    public static FileBrowserProcess getFileBrowserProcess(){
        if (fileBrowserProcess == null) {
            fileBrowserProcess = new FileBrowserProcess();
        }
        return fileBrowserProcess;
    }
    private static FileUploadProcess fileUploadProcess = null;

    public static FileUploadProcess getFileUploadProcess(){
        if (fileUploadProcess == null) {
            fileUploadProcess = new FileUploadProcess();
        }
        return fileUploadProcess;
    }

    private static SyncConfigProcess syncConfigProcess = null;

    public static SyncConfigProcess getSyncConfigProcess(){
        if (syncConfigProcess == null) {
            syncConfigProcess = new SyncConfigProcess();
        }
        return syncConfigProcess;
    }

    private static DeployProcess deployProcess = null;

    public static DeployProcess getDeployProcess(){
        if (deployProcess == null) {
            deployProcess = new DeployProcess();
        }
        return deployProcess;
    }
}
