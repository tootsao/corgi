package cz.wake.corgibot.runnable;

import cz.wake.corgibot.CorgiBot;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import java.util.TimerTask;

public class StatusChanger extends TimerTask {

    private int sInt = 0;

    @Override
    public void run() {
        if (sInt == 0) {
            CorgiBot.getJda().getPresence().setActivity(Activity.playing("c!invite"));
            CorgiBot.getJda().getPresence().setStatus(OnlineStatus.ONLINE);
            sInt++;
        } else if (sInt == 1) {
            CorgiBot.getJda().getPresence().setActivity(Activity.playing("c!help"));
            sInt++;
        } else if (sInt == 2) {
            CorgiBot.getJda().getPresence().setActivity(Activity.watching(CorgiBot.getJda().getUsers().size() + " users"));
            sInt++;
        } else if (sInt == 3) {
            CorgiBot.getJda().getPresence().setActivity(Activity.listening(CorgiBot.getJda().getGuilds().size() + " guilds"));
            sInt = 0;
        }
    }
}
