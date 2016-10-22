package com.iit.prathieshna.cfenabledapplication;
import com.iit.prathieshna.cyberforagingframework.annotation.offloadMethod;
/**
 * Created by Prathieshna on 4/20/2016.
 */
public class Util {

    @offloadMethod
    public static String remoteIntensiveTask()
    {
        int n=999999999;
        int count=1;
        int c=1,b=1,a=0;
        while(count<=n)
        {
            c=b+a;
            a=b;
            b=c;
            count++;
        }

         n=999999999;
         count=1;
         c=1;b=1;a=0;
        while(count<=n)
        {
            c=b+a;
            a=b;
            b=c;
            count++;
        }
        return "result " + c;
    }

    @offloadMethod
    public String testNonStaticMethod()
    {
        return "success";
    }

    public static String localIntensiveTask()
    {
        int n=999999999;
        int count=1;
        int c=1,b=1,a=0;
        while(count<=n)
        {
            c=b+a;
            a=b;
            b=c;
            count++;
        }

        n=999999999;
        count=1;
        c=1;b=1;a=0;
        while(count<=n)
        {
            c=b+a;
            a=b;
            b=c;
            count++;
        }
        return "result " + c;
    }
}
