package com.tools.gui.process;

import com.tools.socket.bean.FileItemInfo;
import com.tools.socket.bean.Command;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FileBrowserProcess extends ProcessBase {
    private static final Logger log = LoggerFactory.getLogger(FileBrowserProcess.class);

    @Override
    protected void processCommand(Command command, ChannelHandlerContext ctx) {

        int commandCode = command.getCommandCode();
        CommandMethodEnum commandMethodEnum = CommandMethodEnum.getEnum(commandCode);
        switch (commandMethodEnum) {
            case GET_FILE_DIRECTORY:
                fileBrowser(command, ctx);
                break;
        }
    }

    @Override
    protected void error() {
        super.error();
        onFileBrowserSyncListener.onError();
    }

    private void fileBrowser(Command command, ChannelHandlerContext ctx) {
        List<FileItemInfo> fileItemInfoList = (List<FileItemInfo>) command.getContent();
        onFileBrowserSyncListener.onDirectoryUpdate(fileItemInfoList);
    }

    private OnFileBrowserSyncListener onFileBrowserSyncListener;

    public void setOnFileBrowserSyncListener(OnFileBrowserSyncListener onFileBrowserSyncListener) {
        this.onFileBrowserSyncListener = onFileBrowserSyncListener;
    }

    public interface OnFileBrowserSyncListener {
        void onDirectoryUpdate(List<FileItemInfo> fileItemInfoList);
        void onError();
    }
}
