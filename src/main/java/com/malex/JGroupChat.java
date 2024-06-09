package com.malex;

import com.malex.receiver.ReceiverImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.java.Log;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.ObjectMessage;
import org.jgroups.View;

@Log
public class JGroupChat {

  private static final String CHANEL_NAME = "J_GROUP_CHAT";

  private static boolean running = true;

  private static Address address;

  public static void main(String[] args) throws Exception {
    var channel = init();
    //    handle(channel);
    processInput(channel);
    channel.close();
  }

  private static JChannel init() throws Exception {
    //  implements Closeable
    JChannel channel = new JChannel("src/main/resources/udp.xml");
    // 1.
    channel.setName(CHANEL_NAME + "_" + UUID.randomUUID());

    // 2.
    channel.connect("CLUSTER_NAME");

    // 4.
    channel.setReceiver(new ReceiverImpl());

    //    // 5. Start state transfer
    //    channel.getState(null, 0);

    address = channel.getAddress();

    return channel;
  }

  private static void handle(JChannel channel) {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      try {
        System.out.print("> ");
        System.out.flush();
        String line = in.readLine().toLowerCase();
        if (line.startsWith("quit") || line.startsWith("exit")) {
          break;
        }
        line = " max: " + line;
        ObjectMessage objectMessage = new ObjectMessage();
        objectMessage.setObject(line);
        channel.send(objectMessage);
      } catch (Exception ignored) {
        // ignore
      }
    }
  }

  /** Loop on console input until we see 'x' to exit */
  private static void processInput(JChannel channel) throws Exception {
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
        } else if (!destinationName.isEmpty()) {
          destination = getAddress(destinationName, channel).orElse(address);
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
      running = false;
    }
  }

  private static Optional<Address> getAddress(String name, JChannel channel) {
    View view = channel.view();
    if (view == null) {
      return Optional.of(address);
    }
    return view.getMembers().stream()
        .filter(address -> name.equals(address.toString()))
        .findFirst();
  }
}
