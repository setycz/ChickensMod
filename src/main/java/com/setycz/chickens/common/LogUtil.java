package com.setycz.chickens.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.setycz.chickens.ChickensMod;

public class LogUtil
{
	
    private static Logger logger = LogManager.getLogger("Chickens");
    private static File logFile;
    private static PrintWriter logWriter;
    
    public LogUtil(String modID) {
    	logger = LogManager.getLogger(modID);
        logFile = new File("./logs/"+modID +"/"+ modID+"-latest.log");
        setup();
    }
    
    
    public void log(Level level, Object object)
    {
    	if(logWriter == null) {
    		logger.error("Chickens LogUtil is borked");
    		return;
    	}
    	
    	if(level.equals(Level.DEBUG) && !ChickensMod.isDev) return;
    	
    	
        String message = object == null ? "null" : object.toString();
        
        String preLine = new SimpleDateFormat("[HH:mm:ss]").format(new Date()) + " [" + level.name() + "] ";
        
        for(String line : message.split("\\n"))
        {
        	if(line == null || preLine == null) continue;
        	
            logger.log(level, line);
            logWriter.println(preLine + line);
        }
        
        logWriter.flush();
    }
    
    
    public Logger getLogger()
    {
    	return logger;
    }
    
    public <T extends Throwable> T throwing(T thrown)
    {
        return throwing(Level.ERROR, thrown);
    }
    
    public <T extends Throwable> T throwing(Level level, T thrown)
    {
        log(level, ExceptionUtils.getStackTrace(thrown));
        
        return thrown;
    }
    
    public void fatal(Object object)
    {
        log(Level.FATAL, object);
    }

    public void error(Object object)
    {
        log(Level.ERROR, object);
    }

    public void warn(Object object)
    {
        log(Level.WARN, object);
    }

    public void info(Object object)
    {
        log(Level.INFO, object);
    }
    
    public void debug(Object object)
    {
        log(Level.DEBUG, object);
    }
    
    public void trace(Object object)
    {
        log(Level.TRACE, object);
    }
    
    public void setup()
    {
        logFile.getParentFile().mkdirs();
        
        //String baseName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        //int i = 0;
        
        // One-liners for the win
        //for (; (logFile = new File(logDirectory, baseName + "-" + i + ".log")).exists(); i++);
        
        try
        {
            logFile.createNewFile();
            logWriter = new PrintWriter(new FileWriter(logFile));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
