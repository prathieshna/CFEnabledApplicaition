package com.iit.prathieshna.cyberforagingframework.aspect;

import android.os.Environment;

import com.iit.prathieshna.cyberforagingframework.internal.StopWatch;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Aspect representing the cross cutting-concern: Method and Constructor Tracing.
 */
@Aspect
public class TraceAspect {

    private static final String POINTCUT_METHOD =
            "execution(@com.iit.prathieshna.cyberforagingframework.annotation.offloadMethod * *(..))";

    private static final String POINTCUT_CONSTRUCTOR =
            "execution(@com.iit.prathieshna.cyberforagingframework.annotation.offloadMethod *.new(..))";

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotatedWithOffloadMethod() {
    }

    @Pointcut(POINTCUT_CONSTRUCTOR)
    public void constructorAnnotatedOffloadMethod() {
    }

    @Around("methodAnnotatedWithOffloadMethod() || constructorAnnotatedOffloadMethod()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        if (Thread.currentThread().getName().equals("main")) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String methodName = methodSignature.getName();
            String className = methodSignature.getDeclaringType().getName();
//            Class returnType = methodSignature.getReturnType();
//            Class[] methodParams = methodSignature.getParameterTypes();
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            List<Callable<Object>> lst = new ArrayList<>();
            lst.add(new InitializeThread("context"));
            List<Future<Object>> tasks = executorService.invokeAll(lst);
            final StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            for (Future<Object> task1 : tasks) {
                if (task1.get() != null &&  Integer.parseInt(task1.get().toString().split(",")[4]) >= 4) {
                    try {
                        ExecutorService executorService1 = Executors.newFixedThreadPool(1);
                        List<Callable<Object>> lst1 = new ArrayList<>();
                        lst1.add(new InitializeThread("com.iit.prathieshna.cfenabledapplication," + joinPoint.getTarget().getClass().getName() + "," + methodName));
                        List<Future<Object>> tasks1 = executorService1.invokeAll(lst1);
                        for (Future<Object> task : tasks1) {
                            if (task.get() != null) {
                                result = task.get().toString();
                            } else {
                                result = joinPoint.proceed();
                            }
                        }
                        executorService1.shutdown();
                    } catch (NullPointerException e) {
                        ExecutorService executorService1 = Executors.newFixedThreadPool(1);
                        List<Callable<Object>> lst1 = new ArrayList<>();
                        lst1.add(new InitializeThread("com.iit.prathieshna.cfenabledapplication," + className + "," + methodName));
                        List<Future<Object>> tasks1 = executorService1.invokeAll(lst1);
                        for (Future<Object> task : tasks1) {
                            if (task.get() != null) {
                                result = task.get().toString();
                            } else {
                                result = joinPoint.proceed();
                            }
                        }
                        executorService1.shutdown();
                    }

                } else {
                    result = joinPoint.proceed();
                }
            }
            executorService.shutdown();
            stopWatch.stop();
            System.out.println(className + " -> " + stopWatch.getTotalTimeMillis() + " ms");
        } else {
            result = joinPoint.proceed();
        }
        return result;
    }

    public class InitializeThread implements Callable<Object> {
        private String data;

        public InitializeThread(String _data) {
            this.data = _data;
        }

        @Override
        public Object call() throws Exception {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(Environment.getExternalStorageDirectory() + File.separator + "surrogate.txt"));
                String[] surrogateInfo = inputStream.readObject().toString().split(",");
                inputStream.close();
                final Socket socket = new Socket(surrogateInfo[0], Integer.parseInt(surrogateInfo[1]));
                // final Socket socket = new Socket("192.168.1.35", 38301);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(this.data);
                // add a shutdown hook to close the socket if system crashes or exists unexpectedly
                Thread closeSocketOnShutdown = new Thread() {
                    public void run() {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Runtime.getRuntime().addShutdownHook(closeSocketOnShutdown);
                return in.readLine();
            } catch (UnknownHostException e) {
                System.err.println("Socket connection problem (Unknown host)" + e);
            } catch (IOException e) {
                System.err.println("Could not initialize I/O on socket " + e);
            }
            return null;
        }
    }
}
