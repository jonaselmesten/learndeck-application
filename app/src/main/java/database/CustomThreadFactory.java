package database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadFactory;

/**<h1>Custom Thread Factory</h1>
 * Thread factory with named threads
 * and each new thread will have a number starting from 1.
 * @author Jonas Elmesten
 */
public class CustomThreadFactory implements ThreadFactory {

    private final static Logger logger = LogManager.getLogger(CustomThreadFactory.class);
    private final String THREAD_NAME;

    private int threadCounter = 0;

    public CustomThreadFactory(String threadName) {
        this.THREAD_NAME = threadName;
    }

    @Override
    public Thread newThread(Runnable runnable) {

        logger.debug("New thread created - " + THREAD_NAME + " - ID:" + threadCounter);

        threadCounter++;
        return new Thread(runnable, THREAD_NAME + "-Thread - ID:" + threadCounter);
    }
}
