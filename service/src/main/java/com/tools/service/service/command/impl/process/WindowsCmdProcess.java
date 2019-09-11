package com.tools.service.service.command.impl.process;

import com.tools.service.constant.ServiceStateEnum;
import com.tools.service.model.CommandModel;
import com.tools.commons.thread.ThreadPoolManager;
import com.tools.service.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 命令输入流业务处理类
 */
public class WindowsCmdProcess {
    private static final Logger log = LoggerFactory.getLogger(WindowsCmdProcess.class);

    public CommandModel serviceProcess(ProcessBuilder builder) {

        ThreadPoolManager poolManager = ThreadPoolManager.getInstance();
        Future<CommandModel> future = poolManager.getExecutor().submit(new Callable<CommandModel>() {
            Process process = null;

            @Override
            public CommandModel call() throws Exception {
                CommandModel commandModel = new CommandModel();
                try {
                    process = builder.start();
                    List<String> cmdResult = getCmdResult(process.getInputStream());
                    commandModel.setProcessWaitFor(process.waitFor());
                    commandModel.setProcessOutputInfo(cmdResult);
                    //执行任务成功 返回值为 0
                    if (process.waitFor() == 0) {
                        process.destroy();
                        //服务启动
                        if (contains(cmdResult, "服务已经启动成功")) {
                            commandModel.setProcessExcState(ServiceStateEnum.STATED);
                            return commandModel;
                        }
                        //服务停止成功
                        if (contains(cmdResult, "服务已成功停止")) {
                            commandModel.setProcessExcState(ServiceStateEnum.STOPPED);
                            return commandModel;
                        }
                        //服务无法停止，但是服务也是停止成功了
                        if (contains(cmdResult, "服务无法停止")) {
                            commandModel.setProcessExcState(ServiceStateEnum.ERROR);
                            return commandModel;
                        }
                    }
                    //服务已经启动或者服务已经停止，服务名不存在返回值为2
                    if (process.waitFor() == 2) {
                        //服务不存在
                        if (contains(cmdResult, "服务名无效")) {
                            commandModel.setProcessExcState(ServiceStateEnum.NOT_EXIST);
                            return commandModel;
                        }
                        //服务没有启动
                        if (contains(cmdResult, "没有启动")) {
                            commandModel.setProcessExcState(ServiceStateEnum.STOPPED);
                            return commandModel;
                        }
                        //服务已经启动
                        if (contains(cmdResult, "请求的服务已经启动")) {
                            commandModel.setProcessExcState(ServiceStateEnum.STATED);
                            return commandModel;
                        }
                        process.destroy();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    commandModel.setProcessExcState(ServiceStateEnum.STOPPED);
                }
                return commandModel;
            }
        });

        while (true) {
            if (future.isDone()) {
                CommandModel commandModel = null;
                try {
                    commandModel = future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return commandModel;
            }
        }
    }


    public CommandModel serviceStatusProcess(ProcessBuilder builder) {
        CommandModel commandModel = new CommandModel();
        try {
            Process process = builder.start();
            String resultStr = getCmdResultToString(process.getInputStream());
            //STATE              :(.+?)\s(.+?)\s   \s匹配空格  (.+?) 两次匹配
            /**
             * SERVICE_NAME: ThinkWinCRRed5
             *         TYPE               : 10  WIN32_OWN_PROCESS
             *         STATE              : 4  RUNNING
             *                                 (STOPPABLE, NOT_PAUSABLE, ACCEPTS_SHUTDOWN)
             *         WIN32_EXIT_CODE    : 0  (0x0)
             *         SERVICE_EXIT_CODE  : 0  (0x0)
             *         CHECKPOINT         : 0x0
             *         WAIT_HINT          : 0x0
             */
            List<String> matchers = matcherAll("STATE              :(.+?)\\s+(.+?)\\s", resultStr);

            commandModel.setProcessOutputInfo(Arrays.asList(resultStr));
            commandModel.setProcessWaitFor(process.waitFor());
            //==null 服务不存在
            if (matchers == null) {
                commandModel.setProcessExcState(ServiceStateEnum.NOT_EXIST);
            } else {
                String status = matchers.get(2);
                switch (status) {
                    case "RUNNING":
                        commandModel.setProcessExcState(ServiceStateEnum.STATED);
                        break;
                    case "STOP_PENDING":
                        commandModel.setProcessExcState(ServiceStateEnum.STOP_PENDING);
                        break;
                    case "STOPPED":
                        commandModel.setProcessExcState(ServiceStateEnum.STOPPED);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            commandModel.setProcessExcState(ServiceStateEnum.ERROR);
        } catch (InterruptedException e) {
            e.printStackTrace();
            commandModel.setProcessExcState(ServiceStateEnum.ERROR);
        }
        return commandModel;
    }

    public CommandModel bootStartProcess(ProcessBuilder builder) {

        ThreadPoolManager poolManager = ThreadPoolManager.getInstance();
        Future<CommandModel> future = poolManager.getExecutor().submit(() -> {

            CommandModel commandModel = new CommandModel();
            Process process = null;
            try {
                process = builder.start();
                List<String> cmdResult = getCmdResult(process.getInputStream());
                commandModel.setProcessWaitFor(process.waitFor());
                commandModel.setProcessOutputInfo(cmdResult);
                if (process.waitFor() == 0) {
                    commandModel.setProcessExcState(true);
                }
                if (process.waitFor() == 2) {
                    commandModel.setProcessExcState(true);
                }
                if (process.waitFor() == 1) {
                    if (contains(cmdResult, "系统找不到指定的注册表项或值")) {
                        commandModel.setProcessExcState(true);
                    }
                }
                process.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return commandModel;
        });


        CommandModel commandModel;
        while (true) {
            if (future.isDone()) {
                try {
                    commandModel = future.get();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return commandModel;
    }

    public CommandModel batProcess(ProcessBuilder builder) {

        ThreadPoolManager poolManager = ThreadPoolManager.getInstance();

        Future<CommandModel> future = poolManager.getExecutor().submit(() -> {
            CommandModel commandModel = new CommandModel();
            Process process = null;
            try {
                process = builder.start();
                commandModel.setProcessWaitFor(process.waitFor());
                if (process.waitFor() == 0) {
                    commandModel.setProcessExcState(true);
                    commandModel.setProcessOutputInfo(Arrays.asList("服务启动成功!"));
                }
                if (process.waitFor() == 2) {
                    commandModel.setProcessExcState(true);
                    commandModel.setProcessOutputInfo(Arrays.asList("服务启动成功!"));
                }
                process.destroy();
            } catch (IOException e) {
                e.printStackTrace();
                commandModel.setProcessExcState(false);
                commandModel.setProcessOutputInfo(Arrays.asList(e.getMessage()));
            }
            return commandModel;
        });

        CommandModel commandModel;
        while (true) {
            if (future.isDone()) {
                try {
                    commandModel = future.get();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }
        return commandModel;
    }

    /**
     * 处理ZIP的解压
     *
     * @param builder 解压实例
     * @return
     */
    public CommandModel exportZIPProcess(ProcessBuilder builder) {

        ThreadPoolManager poolManager = ThreadPoolManager.getInstance();

        Future<CommandModel> future = poolManager.getExecutor().submit(() -> {
            CommandModel commandModel = new CommandModel();
            Process process;
            LocalTime now = LocalTime.now();
            try {
                process = builder.start();
                //读取流
                //  String cmdResultToString = getCmdResultToString(process.getInputStream());
                List<String> cmdResult = getCmdResult(process.getInputStream());
                int waitFor = process.waitFor();
                commandModel.setProcessWaitFor(waitFor);
                //等于0  执行成功
                if (waitFor == 0) {
                    commandModel.setProcessExcState(true);
                    //cmdResult.subList(cmdResult.size()-6,cmdResult.size()) 解压成功后,截取最后一句输出
                    commandModel.setProcessOutputInfo(cmdResult.subList(cmdResult.size() - 6, cmdResult.size()));
                } else {
                    commandModel.setProcessExcState(false);
                    commandModel.setProcessOutputInfo(cmdResult);
                }
                process.destroy();
                cmdResult = null;
            } catch (IOException e) {
                e.printStackTrace();
                commandModel.setProcessExcState(false);
                commandModel.setProcessOutputInfo(Arrays.asList(e.getMessage()));
            }
            LocalTime end = LocalTime.now();
            Duration between = Duration.between(now, end);
            long seconds = between.getSeconds();
            commandModel.setProcessEexcTime(seconds + "s");
            return commandModel;
        });

        CommandModel commandModel;
        while (true) {
            if (future.isDone()) {
                try {
                    commandModel = future.get();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }
        return commandModel;
    }

    /**
     * 结束进程处理方法
     *
     * @param builder 命令实例
     * @return
     */
    public CommandModel pidKillProcess(ProcessBuilder builder) {

        ThreadPoolManager poolManager = ThreadPoolManager.getInstance();
        Future<CommandModel> future = poolManager.getExecutor().submit(() -> {

            CommandModel commandModel = new CommandModel();
            LocalTime now = LocalTime.now();

            Process process = builder.start();
            List<String> cmdResult = getCmdResult(process.getInputStream());
            commandModel.setProcessOutputInfo(cmdResult);
            int waitFor = process.waitFor();
            commandModel.setProcessWaitFor(waitFor);
            if (waitFor == 0 && contains(cmdResult, "成功: 已终止")) {
                commandModel.setProcessExcState(true);
            } else {
                commandModel.setProcessExcState(false);
            }

            LocalTime end = LocalTime.now();
            Duration between = Duration.between(now, end);
            long seconds = between.getSeconds();
            commandModel.setProcessEexcTime(seconds + "s");
            return commandModel;
        });

        CommandModel commandModel;
        while (true) {
            if (future.isDone()) {
                try {
                    commandModel = future.get();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }
        return commandModel;
    }

    /**
     * 文件删除处理
     *
     * @param builder
     * @return
     */
    public CommandModel deleteFileProcess(ProcessBuilder builder) {

        ThreadPoolManager poolManager = ThreadPoolManager.getInstance();
        Future<CommandModel> future = poolManager.getExecutor().submit(() -> {

            CommandModel commandModel = new CommandModel();
            LocalTime now = LocalTime.now();
            try {
                Process process = builder.start();
                List<String> cmdResult = getCmdResult(process.getInputStream());
                int waitFor = process.waitFor();
                commandModel.setProcessWaitFor(waitFor);
                commandModel.setProcessOutputInfo(cmdResult);

                switch (waitFor) {
                    case 0:
                        if (cmdResult.size() >= 1 && cmdResult.get(0).contains("另一个程序正在使用此文件")) {
                            commandModel.setProcessExcState(false);
                        } else {
                            commandModel.setProcessExcState(true);
                        }
                        break;
                    case 2:
                        if (cmdResult.contains("系统找不到指定的文件。")) {
                            commandModel.setProcessExcState(true);
                        }
                        if (cmdResult.contains("系统找不到指定的路径")) {
                            commandModel.setProcessExcState(true);
                        }
                        break;

                    case 267:
                        commandModel.setProcessExcState(true);
                        break;
                    case 145:
                        //cmd 返回 目录不是空的异常处理
                        commandModel.setProcessExcState(true);
                        break;

                    default:
                        //其他状态的统一处理失败,进行状态捕获
                        commandModel.setProcessExcState(false);
                }

                process.destroy();

                LocalTime end = LocalTime.now();
                Duration between = Duration.between(now, end);
                long seconds = between.getSeconds();
                commandModel.setProcessEexcTime(seconds + "s");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return commandModel;
        });

        CommandModel commandModel = null;
        while (true) {
            if (future.isDone()) {
                try {
                    commandModel = future.get();
                    log.info(commandModel.toString());
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        if (commandModel == null) {

        }
        return commandModel;
    }

    public CommandModel searchPidProcess(ProcessBuilder builder) {
        ThreadPoolManager poolManager = ThreadPoolManager.getInstance();
        Future<CommandModel> future = poolManager.getExecutor().submit(() -> {

            CommandModel commandModel = new CommandModel();
            Process process = builder.start();
            log.info("process.waitFor()");
            List<String> cmdResult = getCmdResult(process.getInputStream());
            commandModel.setProcessOutputInfo(cmdResult);
            log.info("cmdResult.size()" + cmdResult.size());
            if (cmdResult.size() == 0) {
                commandModel.setProcessExcState("");
                return commandModel;
            }
            commandModel.setProcessWaitFor(process.waitFor());
            // Pattern pattern = Pattern.compile("LISTENING       (.+)");
            //Pattern pattern = Pattern.compile("LISTENING\\s+(.\\d+)");
            List<String> strings = matcherAll("LISTENING       (.+)", cmdResult.get(0));

            if (strings.size() >= 2) {
                commandModel.setProcessExcState(strings.get(1));
            } else {
                commandModel.setProcessExcState("");
            }
            return commandModel;
        });
        CommandModel commandModel;
        while (true) {
            if (future.isDone()) {
                try {
                    log.info("future.isDone()");
                    commandModel = future.get();
                    log.info(commandModel.toString());
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return commandModel;
    }

    public void cmdOutput(InputStream is) {
        ThreadPoolManager poolManager = ThreadPoolManager.getInstance();

        poolManager.execute(() -> {

            InputStream inputStream = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                isr = new InputStreamReader(is, "gbk");
                br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(inputStream);
                IOUtils.close(isr);
                IOUtils.close(br);
            }
        });
    }

    /**
     * 获取命令执行后的出处信息
     *
     * @param is 输入流
     * @return
     */
    public List<String> getCmdResult(InputStream is) {

        List<String> cmdSearchPorts = new ArrayList<>();
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new InputStreamReader(is, Charset.forName("gbk"));
            bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                cmdSearchPorts.add(line);
                //System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(reader);
            IOUtils.close(bufferedReader);
        }
        return cmdSearchPorts;
    }

    /**
     * 集合里面是否包含相关字符串
     *
     * @param list    集合
     * @param contain 字符串
     * @return 包含 true 不包含 false
     */
    public boolean contains(List<String> list, String contain) {
        if (list.size() <= 0) {
            return false;
        }
        for (String str : list) {
            boolean contains = str.contains(contain);
            if (contains) {
                return contains;
            }
        }
        return false;
    }

    /**
     * 获取命令行返回的数据,以String形式返回
     *
     * @param is 输入流
     * @return
     */
    public String getCmdResultToString(InputStream is) {
        try {
            InputStreamReader reader = new InputStreamReader(is, Charset.forName("gbk"));
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
                System.out.println(line);
            }
            is.close();
            reader.close();
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取所有匹配内容
     *
     * @param regex  匹配正则
     * @param source 匹配的字符串
     * @return List方式返回所有匹配的字符串 null没有匹配
     */
    public List<String> matcherAll(String regex, String source) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        List<String> matchers = new ArrayList<>();
        if (!matcher.find()) {
            return matchers;
        }
        for (int i = 0; i <= matcher.groupCount(); i++) {
            matchers.add(matcher.group(i));
        }
        return matchers;
    }
}
