package cz.cuni.mff.d3s.distrace.examples;

/**
 * Abstract Task
 */
public class Task extends Thread {

    String taskName;
    Task(String taskName) {
        this.taskName = taskName;
    }

    public void run() {}
}
