/*
 * This file is part of Bluetooth Low Energy Sniffer for Java (BLES4J).
 *
 *     BLES4J is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     BLES4J is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with BLES4J.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.upenn.cis.precise.bles4j.develop;

import java.util.concurrent.*;

/**
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class TestMultithreading {
    public static ConcurrentLinkedDeque<Item> deque = new ConcurrentLinkedDeque<Item>();
    public static void main(String[] args) {
        Thread producerThread = new Thread(new ItemProducer());
        Thread consumerThread = new Thread(new ItemConsumer());
        producerThread.start();
        consumerThread.start();
    }
}

class Item {
    private String description;
    private int itemId;

    public String getDescription() {
        return description;
    }

    public int getItemId() {
        return itemId;
    }

    public Item() {
        this.description = "Default Item";
        this.itemId = 0;
    }

    public Item(String description, int itemId) {
        this.description = description;
        this.itemId = itemId;
    }
}

class ItemProducer implements Runnable {
    public void run() {
        String itemName = "";
        int itemId = 0;
        try {
            for (int i = 1; i < 8; i++) {
                itemName = "Item" + i;
                itemId = i;
                TestMultithreading.deque.add(new Item(itemName, itemId));
                System.out.println("New Item Added:" + itemName + " " + itemId);
                Thread.currentThread().sleep(10);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

class ItemConsumer implements Runnable {
    public void run() {
        try {
            Thread.currentThread().sleep(20);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        Item item;
        while ((item = TestMultithreading.deque.pollFirst()) != null) {
            if (item == null) {
            } else {
                generateOrder(item);
            }
        }
    }

    private void generateOrder(Item item) {
        System.out.println(item.getDescription());
        System.out.println(item.getItemId());
        try {
            Thread.currentThread().sleep(20);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
