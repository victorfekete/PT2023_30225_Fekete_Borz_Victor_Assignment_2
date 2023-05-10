package org.BusinessLogic;



import org.Server.Threads;



import java.io.FileWriter;

import java.io.IOException;

import java.io.PrintWriter;

import java.util.concurrent.BlockingQueue;

import java.util.concurrent.PriorityBlockingQueue;



public class ProcessingCustomers {
    private int clients;
    private int queues;
    private int arrivalMin;
    private int arrivalMax;
    private int processMin;
    private int processMax;
    private int SimulationTime;
    private BlockingQueue<Customer> queue;
    private int peakTime = 0;
    private double serviceTime = 0;
    public ProcessingCustomers (int clients, int queues, int arrivalMin, int arrivalMax, int processMin, int processMax, int SimulationTime) {
        this.clients = clients;
        this.queues = queues;
        this.arrivalMin =arrivalMin;
        this.arrivalMax = arrivalMax;
        this.processMin = processMin;
        this.processMax = processMax;
        this.SimulationTime = SimulationTime;
        this.queue= new PriorityBlockingQueue<>(GenerareCoadaAsteptare.generareClienti(clients,arrivalMin, arrivalMax, processMin, processMax));
    }
    public void afisare() {
        FileWriter file= null;
        try {
            file= new FileWriter("Log4.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PrintWriter printWriter = new PrintWriter(fisier);
        int currentTime = 0, t = 1, peakValue = 0;
        Threads[] queue = new Threads[this.queues + 1];
        for (int i = 1; i <= this.queues; i++) {
            queue[i] = new Threads(i);
        }
        for (int i = 1; i <= this.queues; i++) {
            new Thread(queue[i]).start();
        }
        for (Customer i : this.queue)
            this.serviceTime += i.getProcessingTime();
        this.serviceTime /= this.clients;

        while (currentTime < this.SimulationTime && !(this.queue.isEmpty() && t == 0)) {
            t = 0;

            for (int j = 1; j <= this.clients; j++) {
                if (!this.queue.isEmpty())
                    if (this.queue.peek().getArrivalTime() == currentTime) {
                        int position = 0, min = 100000;
                        for (int i = 1; i <= queues; i++) {
                            if (min > queue[i].getWaitingTime()) {
                                min = queue[i].getWaitingTime();
                                position = i;
                            }
                        }
                        queue[position].addClient(this.queue.peek());
                        this.queue.poll();
                    }
            }
            System.out.println("Time: " + currentTime);
            printWriter.println("Time: " + currentTime);
            String print = "Waiting List: Closed";
            int nr = 0;
            if (!this.queue.isEmpty()) {
                print = "Waiting List: ";
                for (Client i : this.queue) {
                    if (nr == 0)
                        print += i.toString();
                    else
                        print += ", " + i.toString();
                    nr++;
                }
            }
            System.out.println(print);
            printWriter.println(print);
            for (int i = 1; i <= this.queues; i++) {
                System.out.println("Queue" + i + ": " + queue[i]);
                printWriter.println("Queue" + i + ": " + queue[i]);
            }
            System.out.println();
            printWriter.println();

            for (int i = 1; i <= this.queues; i++) {
                queue[i].updateQueue();
            }

            for (int i = 1; i <= this.queues; i++) {
                if (queue[i].coadaGoala() > 0)
                    t++;
            }

            int value = 0;

            for (int i = 1; i <= this.queues; i++) {
                value = queue[i].numarClienti();
            }

            if (value > peakValue) {
                peakValue = val;
                this.peakTime = currentTime;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            currentTime++;
        }

        System.out.println("Peak time: " + this.peakTime);
        System.out.println("Average service time: " + this.serviceTime);
        printWriter.println("Peak time: " + this.peakTime);
        printWriter.println("Average service time: " + this.serviceTime);

        try {
            file.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        printWriter.close();
    }
}