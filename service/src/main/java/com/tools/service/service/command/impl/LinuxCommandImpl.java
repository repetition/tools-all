package com.tools.service.service.command.impl;

import com.tools.service.model.CommandModel;
import com.tools.service.service.command.ICommand;
import com.tools.service.service.command.impl.process.LiunxCmdProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinuxCommandImpl implements ICommand {
    private static final Logger log = LoggerFactory.getLogger(LinuxCommandImpl.class);

    /**
     * 获取cmd命令实例 <BR/>
     * @return ProcessBuilder 获取cmd命令实例
     */
    public  ProcessBuilder newProcessBuilder() {
        return new ProcessBuilder();
    }


    @Override
    public CommandModel cmdSearchPid(String port) {

        ProcessBuilder processBuilder = newProcessBuilder();
        processBuilder.redirectErrorStream(true);
        List<String> cmd = new ArrayList<>();
        cmd.add("sh");
        cmd.add("-c");
        cmd.add("netstat -tunlp | grep " +port);
        processBuilder.command(cmd);
        System.out.println(cmd.toString());

        return new LiunxCmdProcess().searchPidProcess(processBuilder);
    }

    @Override
    public CommandModel cmdStartBat(String batPath) {
        log.info("exec bat:"+batPath);
        List<String> cmdStartBat = new ArrayList<>();
        cmdStartBat.add("cmd.exe");
        cmdStartBat.add("/c");
        //start 会堵塞线程
        // cmdStartBat.add("start");
        cmdStartBat.add(batPath);

        ProcessBuilder builder = newProcessBuilder();
        //执行需要环境变量的bat时，需切将工作路径切换到bat所在目录
        builder.directory(new File(subString(batPath)));
        //将错误输出到出入留
        builder.redirectErrorStream(true);
        log.info("bat:" + batPath);
        //  builder.command("cmd.exe", "/c", "start", "/b", batPath);
        builder.command(cmdStartBat);
        LiunxCmdProcess cmdProcess = new LiunxCmdProcess();
        return cmdProcess.batProcess(builder);
    }

    @Override
    public CommandModel cmdStartService(String serviceName) {

        ProcessBuilder builder = newProcessBuilder();
        //执行需要环境变量的bat时，需切将工作路径切换到bat所在目录
        builder.redirectErrorStream(true);
        log.info("serviceName:" + serviceName);
        List<String> cmdNetStart = new ArrayList<>();
        cmdNetStart.add("cmd.exe");
        cmdNetStart.add("/c");
        cmdNetStart.add("net");
        cmdNetStart.add("start");
        cmdNetStart.add(serviceName);
        builder.command(cmdNetStart);
        LiunxCmdProcess cmdProcess = new LiunxCmdProcess();
        CommandModel commandModel = cmdProcess.serviceProcess(builder);

        return commandModel;
    }

    @Override
    public CommandModel cmdStopService(String serviceName) {

        ProcessBuilder builder = newProcessBuilder();
        builder.redirectErrorStream(true);
        log.info("serviceName:" + serviceName);
        List<String> cmdNetStop = new ArrayList<>();
        // cmdNetStop.add("cmd.exe");
        // cmdNetStop.add("/c");
        cmdNetStop.add("net");
        cmdNetStop.add("stop");
        cmdNetStop.add(serviceName);
        builder.command(cmdNetStop);
        // Process process = builder.command("cmd.exe", "/c", "start", batPath).start();
        LiunxCmdProcess cmdProcess = new LiunxCmdProcess();
        CommandModel commandModel = cmdProcess.serviceProcess(builder);
        return commandModel;
    }

    @Override
    public CommandModel cmdKillPid(String pidStr) {

        List<String> cmdTaskKill = new ArrayList<>();
        cmdTaskKill.add("kill");
        cmdTaskKill.add("-9");
        cmdTaskKill.add(pidStr);
        System.out.println(cmdTaskKill.toString());
        ProcessBuilder processBuilder = newProcessBuilder();
        processBuilder.redirectErrorStream(true);
        processBuilder.command(cmdTaskKill);
        LiunxCmdProcess cmdProcess = new LiunxCmdProcess();
        return cmdProcess.pidKillProcess(processBuilder);
    }

    @Override
    public CommandModel exportZip(String rootWarPath, String unRootWarPath) {
        ProcessBuilder processBuilder = newProcessBuilder();
        processBuilder.redirectErrorStream(true);
        List<String> command = new ArrayList<>();
        command.add("unzip");
        command.add("-o");//不提示的情况下覆盖文件
        command.add(rootWarPath);
        command.add("-d");//解压到指定目录
        command.add(unRootWarPath);

        System.out.println(Arrays.asList(command));
        processBuilder.command(command);
        LiunxCmdProcess cmdProcess = new LiunxCmdProcess();
        return cmdProcess.exportZIPProcess(processBuilder);
    }

    @Override
    public CommandModel cmdDeleteFiles(String filePath) {

        ProcessBuilder processBuilder = newProcessBuilder();
        processBuilder.redirectErrorStream(true);
        List<String> command = new ArrayList<>();
        command.add("rm");
        command.add("-rf");
        command.add(filePath);
        processBuilder.command(command);
        return new LiunxCmdProcess().deleteFileProcess(processBuilder);
    }

    /**
     * linux安装软件
     * @param rpmPath 软件路径
     * @return
     */
    public CommandModel cmdInstallRpm(String rpmPath) {

        ProcessBuilder processBuilder = newProcessBuilder();
        processBuilder.redirectErrorStream(true);
        List<String> command = new ArrayList<>();
        command.add("rpm");
        command.add("-ivh");
        command.add(rpmPath);
        processBuilder.command(command);
        return new LiunxCmdProcess().installRpmProcess(processBuilder);
    }

    /**
     * linux检查软件是否安装
     * @param command 命令
     * @return
     */
    public CommandModel cmdCheckRpm(List<String> command) {

        ProcessBuilder processBuilder = newProcessBuilder();
        processBuilder.redirectErrorStream(true);
        processBuilder.command(command);
        return new LiunxCmdProcess().installRpmProcess(processBuilder);
    }

    @Override
    public CommandModel cmdQueryServiceStatus(String serviceName) {


        return null;
    }

    @Override
    public CommandModel cmdCancelBootStartBySchtasks() {
        return null;
    }

    @Override
    public CommandModel cmdSetBootStartBySchtasks() {
        return null;
    }

    /**
     * 截取字符串
     *
     * @param path 返回文件上一级路径
     */
    public  String subString(String path) {
        int index = path.lastIndexOf("/");
        String substring = path.substring(0, index);
        return substring;
    }

}
