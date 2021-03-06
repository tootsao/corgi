package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Bigmoji implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if(args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed().setTitle(I18n.getLoc(gw, "internal.general.help-command") + " - bigmoji :question:")
                    .setDescription(getDescription() + "\n\n**Použití**\n" +
                            getHelp().replace("%", gw.getPrefix())).build()).queue();
        } else {
            String str = args[0];
            if (str.matches("<.*:.*:\\d+>")) {
                String id = str.replaceAll("<.*:.*:(\\d+)>", "$1");
                Long longId = Long.valueOf(id);
                Emote emote = channel.getJDA().getEmoteById(longId);
                if (emote != null) {
                    channel.sendMessage(MessageUtils.getEmbed().setImage(emote.getImageUrl()).build()).queue();
                } else {
                    MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.bigmoji.only-server-emoji"), channel);
                }
            } else {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.bigmoji.invalid-emoji-format"), channel);
            }
        }
    }

    @Override
    public String getCommand() {
        return "bigmoji";
    }

    @Override
    public String getDescription() {
        return "Generování velkých emoji v chatu!";
    }

    @Override
    public String getHelp() {
        return "%bigmoji <regex|text> - K odeslání velkého emoji!";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"animoji"};
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }
}
