import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

class ProducerConsumer {
    public static void main(String[] args) {
        final int BUFFER_SIZE = 5;
        Buffer buffer = new Buffer(BUFFER_SIZE);

        Thread producer = new Thread(new Producer(buffer));
        Thread consumer = new Thread(new Consumer(buffer));

        producer.start();
        consumer.start();
    }
}

// Buffer class for Producer-Consumer problem
class Buffer {
    private final Queue<Integer> queue;
    private final int capacity;
    private final Semaphore mutex;
    private final Semaphore empty;
    private final Semaphore full;

    public Buffer(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.mutex = new Semaphore(1);
        this.empty = new Semaphore(capacity);
        this.full = new Semaphore(0);
    }

    public void produce(int value) throws InterruptedException {
        empty.acquire();  // Decrease empty count
        mutex.acquire();  // Lock

        queue.add(value);
        System.out.println("Produced: " + value);

        mutex.release();  // Unlock
        full.release();   // Increase full count
    }

    public int consume() throws InterruptedException {
        full.acquire();   // Decrease full count
        mutex.acquire();  // Lock

        int value = queue.poll();
        System.out.println("Consumed: " + value);

        mutex.release();  // Unlock
        empty.release();  // Increase empty count
        return value;
    }
}

// Producer class
class Producer implements Runnable {
    private final Buffer buffer;

    public Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            try {
                buffer.produce(i);
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

// Consumer class
class Consumer implements Runnable {
    private final Buffer buffer;

    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            try {
                buffer.consume();
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

// Dining Philosophers problem
class DiningPhilosophers {
    public static void main(String[] args) {
        final int NUM_PHILOSOPHERS = 5;
        Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
        Semaphore[] forks = new Semaphore[NUM_PHILOSOPHERS];

        // Initialize the forks
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new Semaphore(1);
        }

        // Create philosophers
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i] = new Philosopher(i, forks[i], forks[(i + 1) % NUM_PHILOSOPHERS]);
            new Thread(philosophers[i]).start();
        }
    }
}

// Philosopher class
class Philosopher implements Runnable {
    private final int id;
    private final Semaphore leftFork;
    private final Semaphore rightFork;

    public Philosopher(int id, Semaphore leftFork, Semaphore rightFork) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    @Override
    public void run() {
        try {
            while (true) {
                think();
                eat();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " is thinking.");
        Thread.sleep((int) (Math.random() * 1000));
    }

    private void eat() throws InterruptedException {
        leftFork.acquire();  // Pick up left fork
        rightFork.acquire(); // Pick up right fork

        System.out.println("Philosopher " + id + " is eating.");
        Thread.sleep((int) (Math.random() * 1000));

        rightFork.release(); // Put down right fork
        leftFork.release();  // Put down left fork
    }
}
