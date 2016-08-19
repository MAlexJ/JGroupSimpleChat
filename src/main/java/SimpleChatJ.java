import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class SimpleChatJ {

    public static void main(String[] args) throws Exception {
        JChannel channel = new JChannel("UDP(bind_addr=127.0.0.1)");
        channel.setReceiver(new ReceiverAdapter() {

            @Override
            public void receive(Message m) {
                System.out.println("  >>> " + m.getObject());
            }

        });
        channel.connect("MyCluster"); // Подключаемся к группе

        /**<br/>         * Цикл обработки команд с консоли<br/>         */
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {

            String line = in.readLine();

            if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                break;
            }

            channel.send(new Message(null, null, line));
        }

        channel.close(); // Отключаемся от группы по завершению работы
    }
}
