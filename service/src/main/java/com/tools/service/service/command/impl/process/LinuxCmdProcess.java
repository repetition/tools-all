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
public class LinuxCmdProcess {
    private static final Logger log = LoggerFactory.getLogger(LinuxCmdProcess.class);

    public CommandModel serviceStartProcess(ProcessBuilder builder) {

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
                        commandModel.setProcessExcState(ServiceStateEnum.STATED);
                        process.destroy();
                    } else {
                        commandModel.setProcessExcState(ServiceStateEnum.ERROR);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    commandModel.setProcessExcState(ServiceStateEnum.ERROR);
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

    public CommandModel serviceStopProcess(ProcessBuilder builder) {

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
                        commandModel.setProcessExcState(ServiceStateEnum.STOPPED);
                        process.destroy();
                    } else {
                        commandModel.setProcessExcState(ServiceStateEnum.ERROR);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    commandModel.setProcessExcState(ServiceStateEnum.ERROR);
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
             * Active: active (running)  运行
             * Active: inactive (dead) 没有运行
             */
            commandModel.setProcessWaitFor(process.waitFor());
            commandModel.setProcessOutputInfo(Arrays.asList(resultStr));
            if (resultStr.contains("Active: active (running)")) {
                commandModel.setProcessExcState(ServiceStateEnum.STATED);
            } else if (resultStr.contains("Active: inactive (dead)")) {
                commandModel.setProcessExcState(ServiceStateEnum.STOPPED);
            } else if (resultStr.contains("could not be found")) {
                commandModel.setProcessExcState(ServiceStateEnum.NOT_EXIST);
            } else if (resultStr.contains("Active: active (exited)")) {
                commandModel.setProcessExcState(ServiceStateEnum.STOPPED);
            } else {
                commandModel.setProcessExcState(ServiceStateEnum.STOPPED);
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
                List<String> cmdResult = getCmdResult(process.getInputStream());
                commandModel.setProcessWaitFor(process.waitFor());
                log.info(cmdResult.toString());
                if (process.waitFor() == 0) {
                    commandModel.setProcessExcState(true);
                    commandModel.setProcessOutputInfo(Arrays.asList("服务启动成功!"));
                } else if (process.waitFor() == 2) {
                    commandModel.setProcessExcState(true);
                    commandModel.setProcessOutputInfo(Arrays.asList("服务启动成功!"));
                } else {
                    commandModel.setProcessExcState(false);
                    commandModel.setProcessOutputInfo(cmdResult);
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
            if (waitFor == 0) {
                commandModel.setProcessExcState(true);
                commandModel.setProcessOutputInfo(Arrays.asList("服务停止成功!"));
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

    public CommandModel searchPidProcess(ProcessBuilder builder, String targetPort) {
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
            //过滤端口
            filter(cmdResult, targetPort);

            if (cmdResult.size() == 0) {
                commandModel.setProcessExcState("");
            } else {
                //根据空白字符串截取
                String[] split = cmdResult.get(0).split("\\s+");

                String pidProcess = split[split.length - 1];
                //截取pid
                String pidStr = pidProcess.split("/")[0];
                commandModel.setProcessExcState(pidStr);
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

    private void filter(List<String> cmdResult, String targetPort) {
        //一次过滤
        cmdResult.removeIf(s -> {
            String[] split = s.split("\\s+");

            String state = split[split.length - 2];

            if (state.equals("LISTEN")) {
                return false;
            }
            return true;
        });
        //二次过滤
        cmdResult.removeIf(s -> {

            String[] split = s.split("\\s+");

            String address = split[split.length - 4];

            String port = address.substring(address.lastIndexOf(":") + 1, address.length());

            if (port.equals(targetPort)) {
                return false;
            }
            return true;
        });
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
            reader = new InputStreamReader(is, Charset.forName("utf-8"));
            bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                cmdSearchPorts.add(line);
                // System.out.println(line);
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
            InputStreamReader reader = new InputStreamReader(is, Charset.forName("utf-8"));
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

    public CommandModel installRpmProcess(ProcessBuilder processBuilder) {

        ThreadPoolManager poolManager = ThreadPoolManager.getInstance();
        Future<CommandModel> future = poolManager.getExecutor().submit(() -> {

            CommandModel commandModel = new CommandModel();
            Process process = null;
            LocalTime now = LocalTime.now();
            try {
                process = processBuilder.start();
                List<String> cmdResult = getCmdResult(process.getInputStream());
                int waitFor = process.waitFor();
                commandModel.setProcessWaitFor(waitFor);
                commandModel.setProcessOutputInfo(cmdResult);

                switch (waitFor) {
                    case 0:
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
                commandModel.setProcessExcState(false);
                process.destroy();

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
            commandModel = new CommandModel();
            commandModel.setProcessExcState(false);
        }
        return commandModel;


    }

    public CommandModel stopServerProcess(ProcessBuilder processBuilder) {
        ThreadPoolManager poolManager = ThreadPoolManager.getInstance();

        Future<CommandModel> future = poolManager.getExecutor().submit(() -> {
            CommandModel commandModel = new CommandModel();
            Process process = null;
            try {
                process = processBuilder.start();
                List<String> cmdResult = getCmdResult(process.getInputStream());
                commandModel.setProcessWaitFor(process.waitFor());
                commandModel.setProcessOutputInfo(cmdResult);
                if (process.waitFor() == 0) {
                    commandModel.setProcessExcState(true);
                    commandModel.setProcessOutputInfo(Arrays.asList("服务停止成功!"));
                } else {
                    commandModel.setProcessExcState(false);
                    commandModel.setProcessOutputInfo(Arrays.asList("服务停止失败!"));
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

    public CommandModel startServerProcess(ProcessBuilder processBuilder) {
        ThreadPoolManager poolManager = ThreadPoolManager.getInstance();

        Future<CommandModel> future = poolManager.getExecutor().submit(() -> {
            CommandModel commandModel = new CommandModel();
            Process process = null;
            try {
                process = processBuilder.start();
                List<String> cmdResult = getCmdResult(process.getInputStream());
                commandModel.setProcessWaitFor(process.waitFor());
                commandModel.setProcessOutputInfo(cmdResult);
                if (process.waitFor() == 0) {
                    commandModel.setProcessExcState(true);
                    commandModel.setProcessOutputInfo(Arrays.asList("服务启动成功!"));
                } else {
                    commandModel.setProcessExcState(false);
                    commandModel.setProcessOutputInfo(Arrays.asList("服务启动失败!"));
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
}
