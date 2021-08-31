package devep;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.time.Instant;

public class ScheduleTasks {

    private Plugin plugin;

    public ScheduleTasks(Plugin plugin) {
        this.plugin = plugin;
    }

    public void sendWorldBorderPackets() {

        //int waitBetweenEdgeClosesSeconds = 900;
        int waitBetweenEdgeClosesSeconds = 50;

        Thread t = new Thread(() -> {
            while (true) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("sendWorldBorderPackets() lanzado");
                System.out.println("calculo tiempo: " + Math.abs(Instant.now().getEpochSecond() - WorldBorder.lastEdgeCloses));

                int onlinePlayersCount = Bukkit.getOnlinePlayers().size();
                int edgeCloseLength = 0;


                if (Math.abs(Instant.now().getEpochSecond() - WorldBorder.lastEdgeCloses) >= waitBetweenEdgeClosesSeconds) {

                    WorldBorder.lastEdgeCloses = Instant.now().getEpochSecond();

                    if (onlinePlayersCount <= 5 && WorldBorder.borderDistance > 800) {
                        edgeCloseLength = 350;
                    } else if (onlinePlayersCount <= 10 && WorldBorder.borderDistance > 1500) {
                        edgeCloseLength = 500;
                    } else if (onlinePlayersCount <= 20 && WorldBorder.borderDistance > 2000) {
                        edgeCloseLength = 600;
                    } else if (onlinePlayersCount <= 30 && WorldBorder.borderDistance > 2500) {
                        edgeCloseLength = 800;
                    } else if (onlinePlayersCount <= 50 && WorldBorder.borderDistance > 2900) {
                        edgeCloseLength = 500;
                    } else {
                        edgeCloseLength = 0;
                    }

                    if (edgeCloseLength != 0) {
                        System.out.println("Cerrando el borde por: " + edgeCloseLength);

                        for (int i = 0; i < edgeCloseLength; i++) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                try {
                                    WorldBorder.sendWorldPacket(player, WorldBorder.borderDistance - 1);
                                } catch (Exception ex) {
                                    System.out.println("sendWorldBorderPackets() : " + ex.toString());
                                }

                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        });

        t.start();
    }
}
