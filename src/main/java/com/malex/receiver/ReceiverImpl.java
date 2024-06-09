package com.malex.receiver;

import java.util.List;
import lombok.extern.java.Log;
import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;

@Log
public class ReceiverImpl implements Receiver {

  private View lastView;

  @Override
  public void receive(Message msg) {
    log.severe(msg.toString());
  }

  @Override
  public void viewAccepted(View newView) {

    // Save view if this is the first
    if (lastView == null) {
      System.out.println("Received initial view:");
      newView.forEach(System.out::println);
    } else {
      // Compare to last view
      System.out.println("Received new view.");

      List<Address> newMembers = View.newMembers(lastView, newView);
      System.out.println("New members: ");
      newMembers.forEach(System.out::println);

      List<Address> exMembers = View.leftMembers(lastView, newView);
      System.out.println("Exited members:");
      exMembers.forEach(System.out::println);
    }
    lastView = newView;
  }
}
