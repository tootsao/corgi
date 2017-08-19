package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.HttpUtils;
import cz.wake.corgibot.utils.LoadingProperties;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

public class Game implements ICommand {


    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(args.length < 1){

        } else {
            LoadingProperties properties = new LoadingProperties();
            String mashapeKey = properties.getMashapeGameKey();

            if (mashapeKey == null) {
                MessageUtils.sendErrorMessage("MashapeKey je prázdný!", channel);
                return;
            }

            try {

                String query = StringUtils.join(args, " ");

                String url = new URIBuilder("https://igdbcom-internet-game-database-v1.p.mashape.com/games/")
                        .addParameter("fields", "name,summary,rating,cover.url")
                        .addParameter("limit", String.valueOf(1))
                        .addParameter("search", query)
                        .toString();

                Request request = new Request.Builder()
                        .url(url)
                        .header("X-Mashape-Key", mashapeKey)
                        .header("Accept", "application/json")
                        .build();

                HttpUtils.CLIENT.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        call.cancel();
                        MessageUtils.sendErrorMessage("Chyba v API! Opakuj akci později.", channel);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        ResponseBody body = response.body();
                        if (body == null) return;

                        JSONArray jsa = new JSONArray(new JSONTokener(body.byteStream()));

                        if (jsa.length() == 0) {
                            MessageUtils.sendErrorMessage("Nebyla nalezena žádná hra s tímto názvem.", channel);
                            return;
                        }

                        JSONObject jso = jsa.getJSONObject(0);

                        String title = jso.optString("name");
                        String score = jso.optString("rating");
                        String desc = jso.optString("summary");
                        String thumb = "https:" + jso.optJSONObject("cover").optString("url");

                        channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle(title).setThumbnail(thumb).addField("Score", score, true)
                                .addField("Popis", desc, false).build()).queue();

                        response.close();
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getCommand() {
        return "game";
    }

    @Override
    public String getDescription() {
        return "Získání informace o požadované hře.";
    }

    @Override
    public String getHelp() {
        return ".game <nazev>";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.GUILD;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}