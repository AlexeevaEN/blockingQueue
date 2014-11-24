package bq;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class Producer
        implements Runnable {

    private BlockingQueue<Integer> drop;
    InArray ia;

    public Producer(BlockingQueue<Integer> d, InArray ia) {
        this.drop = d;
        this.ia = ia;
    }

    public void run() {
        while (true) {
            try {
                drop.put(ia.getInt());
            } catch (InterruptedException intEx) {
                break;
            }
        }
    }
}

class Consumer
        implements Runnable {

    private BlockingQueue<Integer> drop;
    OutArray oa;

    public Consumer(BlockingQueue<Integer> d, OutArray oa) {
        this.drop = d;
        this.oa = oa;
    }

    public void run() {
        while (true) {
            try {
                oa.putInt(drop.poll(1, TimeUnit.SECONDS));
            } catch (InterruptedException ex) {
                Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex){            
                break;
            }
        }
    }
}

class InArray {

    int index = 0;
    int arrLenght = 10;
    int[] inputArr = new int[arrLenght];

    public InArray() {
        Random rnd = new Random();
        for (int i = 0; i < arrLenght; i++) {
            inputArr[i] = rnd.nextInt(1000);
        }
    }

    public int getInt() throws InterruptedException {
        int result;
        synchronized (this) {
            if (index < arrLenght) {
                result = inputArr[index++];
            } else {
                throw new InterruptedException();
            }
        }
        return result;
    }

    void print() {
        System.out.println(Arrays.toString(inputArr));
    }
}

class OutArray {

    int index = 0;
    int arrLenght = 10;
    int[] inputArr = new int[arrLenght];

    public void putInt(int num) {
        synchronized (this) {
            inputArr[index++] = num;
        }
    }

    void print() {
        System.out.println(Arrays.toString(inputArr));
    }
}

public class Bq {

    public static void main(String[] args) throws InterruptedException {
        InArray ia = new InArray();
        OutArray oa = new OutArray();
        BlockingQueue<Integer> drop = new ArrayBlockingQueue(1, true);
        for (int i = 0; i < 10; i++) {
            (new Thread(new Producer(drop, ia))).start();
            (new Thread(new Consumer(drop, oa))).start();
        }
        Thread.sleep(3000);
        oa.print();
    }
}