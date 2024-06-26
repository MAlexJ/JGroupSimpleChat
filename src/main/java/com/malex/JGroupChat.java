package com.malex;

import com.malex.receiver.ReceiverImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import lombok.extern.java.Log;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.ObjectMessage;

@Log
public class JGroupChat {

  private static final String LOGICAL_CHANEL_NAME = "J_GROUP_CHAT";
  private static final String CLUSTER_NAME = "J_CLUSTER_NAME";

  private static boolean running = true;

  public static void main(String[] args) throws Exception {
    var channel = init();
    processInput(channel);
    channel.close();
  }

  private static JChannel init() throws Exception {
    // 1. set up XML transport configuration
    var channel = new JChannel("src/main/resources/udp.xml");

    // 2. config logical name for the channel.
    channel.setName(LOGICAL_CHANEL_NAME + "_" + UUID.randomUUID());

    // 3. set up cluster name
    channel.connect(CLUSTER_NAME);

    // 4. receiver
    channel.setReceiver(new ReceiverImpl());

    return channel;
  }

  /** Loop on console input until we see 'x' to exit */
  private static void processInput(JChannel channel) {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    while (running) {
      try {
        // Get a destination, <enter> means broadcast
        Address destination = null;
        System.out.print("Enter a destination: ");
        System.out.flush();
        String destinationName = in.readLine().toLowerCase();

        if (destinationName.equals("x")) {
          running = false;
          continue;
        }

        if (destinationName.equals("r")) {
          Address currentAddress = channel.getAddress();
          System.out.println("current: " + currentAddress);
          getAddress(channel).stream()
              .filter(address -> !address.equals(currentAddress))
              .forEach(address -> System.out.println("local: " + address));
          continue;
        }

        if (!destinationName.isEmpty()) {
          destination = getAddress(destinationName, channel);
          if (destination == null) {
            System.out.print("Destination '" + destinationName + "' not found!");
            continue;
          }
        }

        // Accept a string to send
        System.out.print("Enter a message: ");
        System.out.flush();
        String line = in.readLine().toLowerCase();
        sendMessage(destination, line, channel);
      } catch (IOException ioe) {
        running = false;
      }
    }
    System.out.println("Exiting.");
  }

  /**
   * Send message from here
   *
   * @param destination the destination
   * @param messageString the message
   */
  private static void sendMessage(Address destination, String messageString, JChannel channel) {
    try {
      System.out.println("Sending " + messageString + " to " + destination);
      ObjectMessage message = new ObjectMessage(destination, messageString);
      channel.send(message);
    } catch (Exception exception) {
      System.err.println("Exception sending message: " + exception.getMessage());
    }
  }

  private static Address getAddress(String name, JChannel channel) {
    return getAddress(channel).stream()
        .filter(address -> name.equalsIgnoreCase(address.toString()))
        .findFirst()
        .orElse(null);
  }

  private static List<Address> getAddress(JChannel channel) {
    return channel.view().getMembers();
  }
}
