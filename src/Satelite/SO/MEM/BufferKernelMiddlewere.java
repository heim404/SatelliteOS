package Satelite.SO.MEM;

import java.util.ArrayList;
import java.util.List;

public class BufferKernelMiddlewere {
    public static class Node {
        public String task;
        int priority;

        Node(String task, int priority) {
            this.task = task;
            this.priority = priority;
        }
    }

    public final List<Node> heap = new ArrayList<>();
    private final Object monitor = new Object();
    private static final int MAX_SIZE = 5;
    private boolean hasTasks = false;
    private boolean isInsertWaiting = false;

    public void insert(String task, int priority) {
        synchronized (monitor) {
            while (heap.size() == MAX_SIZE) { // Buffer cheio, aguarda até que haja espaço
                try {
                    isInsertWaiting = true;
                    monitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    isInsertWaiting = false;
                }
            }

            heap.add(new Node(task, priority));

            int idx = heap.size() - 1;
            while (idx != 0) {
                int parentIdx = (idx - 1) / 2;
                if (heap.get(parentIdx).priority < heap.get(idx).priority) {
                    swap(parentIdx, idx);
                    idx = parentIdx;
                } else {
                    break;
                }
            }
            System.out.println("New Message [" + task + "] Inserted Into Buffer");
            hasTasks = true;
            monitor.notifyAll();
        }
    }

    public String extractMax() {
        synchronized (monitor) {
            while (!hasTasks) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            Node maxNode = heap.get(0);
            heap.set(0, heap.get(heap.size() - 1));
            heap.remove(heap.size() - 1);

            int idx = 0;
            while (idx < heap.size()) {
                int leftChildIdx = idx * 2 + 1;
                int rightChildIdx = idx * 2 + 2;
                int largerChildIdx = leftChildIdx;

                if (rightChildIdx < heap.size() && heap.get(rightChildIdx).priority > heap.get(leftChildIdx).priority) {
                    largerChildIdx = rightChildIdx;
                }

                if (largerChildIdx < heap.size() && heap.get(largerChildIdx).priority > heap.get(idx).priority) {
                    swap(largerChildIdx, idx);
                    idx = largerChildIdx;
                } else {
                    break;
                }
            }

            if (heap.isEmpty()) {
                hasTasks = false;
            }

            if (isInsertWaiting) {
                monitor.notify();
            }

            return maxNode.task;
        }
    }

    private void swap(int i, int j) {
        Node temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

//    Notifica a thread do kernel se o buffer tem ou não tarefas

    public void waitForTasks() {
        synchronized (monitor) {
            while (!hasTasks) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}