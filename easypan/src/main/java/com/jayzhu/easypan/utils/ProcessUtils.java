package com.jayzhu.easypan.utils;

import com.jayzhu.easypan.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月10日 10:06:33
 * @packageName com.jayzhu.easypan.utils
 * @className ProcessUtils
 * @describe TODO
 */
@Slf4j
public class ProcessUtils {
    public static String executeCommand(String cmd, boolean outPrintLog) throws BusinessException {
        if (StringTools.isEmpty(cmd)) {
            log.error("--- 指令执行失败，应为要执行的ffmpeg指令为空 ---");
            return null;
        }
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);

            PrintStream errorStream = new PrintStream(process.getErrorStream());
            PrintStream inputStream = new PrintStream(process.getInputStream());
            errorStream.start();
            inputStream.start();

            process.waitFor();
            String result = errorStream.stringBuffer.append(inputStream.stringBuffer + "\n").toString();
            if (outPrintLog) {
                log.info("执行命令:{},已执行完毕，执行结果:{}", cmd, result);
            } else {
                log.info("执行命令:{},已执行完毕", cmd);
            }
            return result;
        } catch (Exception e) {
            log.error("视频转换失败", e);
            throw new BusinessException("视频转换失败");
        } finally {
            if (null != process) {
                ProcessKiller ffmpegKiller = new ProcessKiller(process);
                runtime.addShutdownHook(ffmpegKiller);
            }
        }
    }


    private static class ProcessKiller extends Thread {
        private Process process;

        public ProcessKiller(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            this.process.destroy();
        }
    }

    static class PrintStream extends Thread {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer();

        public PrintStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try {
                if (inputStream == null) {
                    return;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
            } catch (Exception e) {
                log.error("读取输入流出错，{}", e.getMessage());
            } finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    log.error("关闭流出错", e);
                }
            }
        }
    }
}
