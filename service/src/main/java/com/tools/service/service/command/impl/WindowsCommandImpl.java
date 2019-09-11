package com.tools.service.service.command.impl;

import com.tools.service.model.CommandModel;
import com.tools.service.service.command.ICommand;
import com.tools.service.service.command.impl.process.WindowsCmdProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WindowsCommandImpl implements ICommand {
    private static final Logger log = LoggerFactory.getLogger(WindowsCommandImpl.class);

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
        cmd.add("cmd.exe");
        cmd.add("/c");
        cmd.add("netstat");
        cmd.add("-ano");
        cmd.add("|");
        cmd.add("findstr");
        cmd.add(port);
        processBuilder.command(cmd);
        System.out.println(cmd.toString());

        return new WindowsCmdProcess().searchPidProcess(processBuilder);
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
        WindowsCmdProcess cmdProcess = new WindowsCmdProcess();
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
        WindowsCmdProcess cmdProcess = new WindowsCmdProcess();
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
        WindowsCmdProcess cmdProcess = new WindowsCmdProcess();
        CommandModel commandModel = cmdProcess.serviceProcess(builder);
        return commandModel;
    }

    @Override
    public CommandModel cmdKillPid(String pidStr) {

        List<String> cmdTaskKill = new ArrayList<>();
        // taskkill -F -PID 15792
        //  cmdTaskKill.add("cmd.exe");
        //  cmdTaskKill.add("/c");
        cmdTaskKill.add("taskkill");
        cmdTaskKill.add("/F");
        cmdTaskKill.add("/PID");
        cmdTaskKill.add(pidStr);
        System.out.println(cmdTaskKill.toString());
        ProcessBuilder processBuilder = newProcessBuilder();
        processBuilder.redirectErrorStream(true);
        processBuilder.command(cmdTaskKill);
        WindowsCmdProcess cmdProcess = new WindowsCmdProcess();
        return cmdProcess.pidKillProcess(processBuilder);
    }

    @Override
    public CommandModel exportZip(String rootWarPath, String unRootWarPath) {
        ProcessBuilder processBuilder = newProcessBuilder();
        processBuilder.redirectErrorStream(true);
        List<String> command = new ArrayList<>();
        command.add(System.getProperty("HaoZip.path") + "\\HaoZipC.exe");
        command.add("x");
        command.add(rootWarPath);
        command.add("-o" + unRootWarPath);
        command.add("-aoa");
        command.add("-y");

        System.out.println(Arrays.asList(command));
        processBuilder.command(command);
        WindowsCmdProcess cmdProcess = new WindowsCmdProcess();
        return cmdProcess.exportZIPProcess(processBuilder);
    }

    @Override
    public CommandModel cmdDeleteFiles(String filePath) {

        ProcessBuilder processBuilder = newProcessBuilder();
        processBuilder.redirectErrorStream(true);
        List<String> command = new ArrayList<>();
        command.add("cmd.exe");
        command.add("/c");
        command.add("rmdir");
        command.add("/s");
        command.add("/q");
        command.add(filePath);
        processBuilder.command(command);
        return new WindowsCmdProcess().deleteFileProcess(processBuilder);
    }

    @Override
    public CommandModel cmdQueryServiceStatus(String serviceName) {

        ProcessBuilder builder = newProcessBuilder();
        builder.redirectErrorStream(true);

        List<String> cmdServiceState = new ArrayList<>();
        cmdServiceState.add("cmd.exe");
        cmdServiceState.add("/c");
        cmdServiceState.add("sc");
        cmdServiceState.add("query");
        cmdServiceState.add(serviceName);
        builder.command(cmdServiceState);
        WindowsCmdProcess cmdProcess = new WindowsCmdProcess();
        CommandModel commandModel = cmdProcess.serviceStatusProcess(builder);
        return commandModel;
    }



    /**
     * 设置开机启动(使用Windows计划任务进行实现)
     *SCHTASKS /Create /SC ONSTART /TN tools_onStart  /TR E:\Tools\Tools.exe  /RL HIGHEST
     */
    @Override
    public  CommandModel cmdSetBootStartBySchtasks() {
        ProcessBuilder builder = newProcessBuilder();
        builder.redirectErrorStream(true);
        List<String> cmdSetBootStart = new ArrayList<>();
        cmdSetBootStart.add("SCHTASKS");
        cmdSetBootStart.add("/Create");
        cmdSetBootStart.add("/SC");
        cmdSetBootStart.add("ONLOGON");//任务类型(登录后执行)
        cmdSetBootStart.add("/TN");
        cmdSetBootStart.add("Tools_onStart");//任务名字
        cmdSetBootStart.add("/TR");
        cmdSetBootStart.add(System.getProperty("dir.base") + "\\Tools.exe");//任务执行的程序路径
        cmdSetBootStart.add("/RL");
        cmdSetBootStart.add("HIGHEST");//以最高权限运行

        builder.command(cmdSetBootStart);
        // Process process = builder.command("cmd.exe", "/c", "start", batPath).start();
        WindowsCmdProcess cmdProcess = new WindowsCmdProcess();
        return cmdProcess.bootStartProcess(builder);
    }

    /**
     * 取消开机启动(使用Windows计划任务进行实现)
     * SCHTASKS /Delete /TN * /F
     */
    @Override
    public  CommandModel cmdCancelBootStartBySchtasks() {
        ProcessBuilder builder = newProcessBuilder();
        builder.redirectErrorStream(true);
        List<String> cmdCancelBootStart = new ArrayList<>();

        cmdCancelBootStart.add("SCHTASKS");
        cmdCancelBootStart.add("/Delete");
        cmdCancelBootStart.add("/TN");
        cmdCancelBootStart.add("Tools_onStart");//任务名字
        cmdCancelBootStart.add("/F");//强制删除

        builder.command(cmdCancelBootStart);
        WindowsCmdProcess cmdProcess = new WindowsCmdProcess();
        return cmdProcess.bootStartProcess(builder);
    }


    /**
     * 截取字符串
     *
     * @param path 返回文件上一级路径
     */
    public  String subString(String path) {
        int index = path.lastIndexOf("\\");
        String substring = path.substring(0, index);
        return substring;
    }

}
