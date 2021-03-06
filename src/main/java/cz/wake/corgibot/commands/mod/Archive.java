package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

@SinceCorgi(version = "1.0")
public class Archive implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        try {
            if (!PermissionUtil.checkPermission(member, Permission.MESSAGE_HISTORY) || !PermissionUtil.checkPermission(member, Permission.MESSAGE_READ)) {
                MessageUtils.sendAutoDeletedMessage("Můžeš archivovat pouze channely do kterých vidíš!", 10000, channel);
                return;
            }
            if (!PermissionUtil.checkPermission(member.getGuild().getSelfMember(), Permission.MESSAGE_HISTORY)) {
                MessageUtils.sendAutoDeletedMessage("Nemám dostatečná práva k přečtení zpráv! Právo: `MESSAGE_HISTORY`", 20000, channel);
                return;
            }

            long numposts = Long.parseLong(args[0]);

            if (numposts > 100) {
                MessageUtils.sendAutoDeletedMessage("Nelze vygenerovat log s víc jak 100 zprávy, kvůli limitu Discord API", 20000, channel);
                return;
            }

            MessageEmbed logMess = MessageUtils.getEmbed(Constants.GREEN).setDescription("Generuji log, čekej prosím...").build();
            channel.sendMessage(logMess).queue();

            TextChannel tx = member.getGuild().getTextChannelById(channel.getId());
            MessageHistory mh;

            mh = new MessageHistory(channel);

            RestAction<List<Message>> messages = mh.retrievePast((int) numposts);
            StringBuilder builder = new StringBuilder("-- Archiv kanálu: [" + tx.getName() + "] --\n\n");
            for (int i = messages.complete().size() - 1; i >= 0; i--) {
                Message m = messages.complete().get(i);
                builder.append("[").append(m.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("] ");
                builder.append(m.getAuthor().getName()).append(" : ");
                builder.append(m.getContentRaw()).append(m.getAttachments().size() > 0 ? " " + m.getAttachments().get(0).getUrl() : "").append("\n");
            }

            MessageEmbed mess = MessageUtils.getEmbed(Constants.GREEN).setTitle("Vygenerovaný log soubor").setDescription("Zasílám vygenerovaný log soubor s " + numposts + " zprávami.\n" +
                    "**Odkaz**: " + MessageUtils.hastebin(builder.toString())).build();
            channel.sendMessage(mess).queue();
        } catch (ArrayIndexOutOfBoundsException ax) {
            MessageUtils.sendAutoDeletedMessage("Musíš zadat počet řádků! Př. `" + gw.getPrefix() + "archive 10`", 20000, channel);
        } catch (Exception e) {
            CorgiBot.LOGGER.error("Chyba při provádení příkazu .archive !", e);
        }

    }

    @Override
    public String getCommand() {
        return "archive";
    }

    @Override
    public String getDescription() {
        return "Archivování zpráv na HasteBin.";
    }

    @Override
    public String getHelp() {
        return "%archive <počet-zpráv>";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.MANAGE_CHANNEL};
    }
}
