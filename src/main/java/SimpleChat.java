import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SimpleChat extends ReceiverAdapter {

    private JChannel channel;

    public static void main(String[] args) throws Exception {
        new SimpleChat().start();
    }

    @Override
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view + "\n");
    }

    @Override
    public void receive(Message msg) {
        String line = msg.getObject().toString();
        System.out.println(line);
    }

    private void start() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("ChatCluster");
        channel.getState(null, 10000);
        eventLoop();
        channel.close();
    }


    private void eventLoop() {
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
                Message msg = new Message(null, null, line);
                channel.send(msg);
            } catch (Exception ignored) {
                //ignore
            }
        }
    }

}
