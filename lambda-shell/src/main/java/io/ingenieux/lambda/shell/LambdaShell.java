package io.ingenieux.lambda.shell;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.ingenieux.lambada.runtime.LambadaFunction;

/**
 * Represents a Shell Interface to AWS Lambda
 */
public class LambdaShell {
    public static void main(String[] args) throws Exception {
        final Map<String, List<String>> result = new LambdaShell().runCommands(Collections.singletonList("ps -aux"));

        System.err.println(result);
    }

    @LambadaFunction(name="lb_runCommand", memorySize = 512, timeout = 300)
    public Map<String, List<String>> runCommands(List<String> inCommands) throws Exception {
        Map<String, List<String>> result = new LinkedHashMap<>();

        for (String e : inCommands) {
            List<String> commandResult = runCommand(e);

            result.put(e, commandResult);
        }

        return result;
    }

    public List<String> runCommand(String command) throws Exception {
        String[] args = command.split("\\s+");

        if (command.startsWith("!aws s3 cp")) {
            // !aws s3 cp <sourceFile> <targetPath>
            String sourceFile = args[3];

            String targetPath = args[4];

            copyFile(sourceFile, targetPath);
        } else {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();

            runCommandArray(os, args);

            return Arrays.asList(new String(os.toByteArray()).split("\n"));
        }

        return null;
    }

    private void copyFile(String sourceFile, String targetPath) {
        AmazonS3 s3Client = new AmazonS3Client();

        AmazonS3URI uri = new AmazonS3URI(targetPath);

        String key = uri.getKey();

        String bucketName = uri.getBucket();

        s3Client.putObject(new PutObjectRequest(bucketName, key, new File(sourceFile)));
    }


    private static void runCommandArray(OutputStream os, String... args) throws Exception {
        PrintWriter pw = new PrintWriter(os, true);

        File tempPath = File.createTempFile("tmp-", ".sh");

        IOUtils.write(StringUtils.join(args, " "), new FileOutputStream(tempPath));

        List<String> processArgs = new ArrayList<>(Arrays.asList("/bin/bash", "-x", tempPath.getAbsolutePath()));

        ProcessBuilder psBuilder = new ProcessBuilder(processArgs).//
                redirectErrorStream(true);//

        final Process process = psBuilder.start();

        final Thread t = new Thread(() -> {
            try {
                IOUtils.copy(process.getInputStream(), os);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        t.start();

        process.waitFor();

        t.join();

        int resultCode = process.exitValue();
    }
}
