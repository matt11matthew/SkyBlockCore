package net.cloudescape.skyblock;

import com.cloudescape.CloudCore;
import com.mrpowergamerbr.temmiewebhook.DiscordEmbed;
import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;
import com.mrpowergamerbr.temmiewebhook.embed.AuthorEmbed;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * Created by Matthew E on 4/18/2018.
 */
public enum IslandLogType {
    ISLAND_LOAD("**{0}**'s Island **{1}** loaded"),
    ISLAND_FAIL_TO_UNLOAD("@everyone **{0}**'s Island **{1}** Failed to unloaded"),
    ISLAND_FAIL_TO_LOAD("@everyone **{0}**'s Island **{1}** Failed to load"),
    ISLAND_UNLOAD("**{0}**'s Island **{1}** unloaded"),
    PLAYER_EXECUTED_COMMAND("**{0}** executed {1}"),
    ISLAND_TOP_UPDATE("Island Top Updated \n{0}"),
    FAILED_TO_LOAD_MINIONS("@everyone **{0}**'s Island **{1}** failed to load minions"),
    FAILED_TO_LOAD_SUITS("@everyone **{0}**'s Island **{1}** failed to load minion suits");

    private String message;

    IslandLogType(String message) {
        this.message = message;
    }

    public void send(String... replacers) {

        String newMessage = this.message;
        if (this.message.contains("{0}") && replacers != null && replacers.length > 0) {
            for (int i = 0; i < replacers.length; i++) {
                newMessage = newMessage.replaceAll("\\{" + i + "\\}", replacers[i]);
            }
        }

        String finalNewMessage = newMessage;
        new BukkitRunnable() {

            @Override
            public void run() {

                TemmieWebhook webhook = new TemmieWebhook("https://discordapp.com/api/webhooks/440305385040117771/WmidqfvxjjEVVl7q3AKYxPXj9eQwCtE6wQmBRybl71mXxzGYlmtxJ0mgSi9YxN2q1voe");
                DiscordMessage build = DiscordMessage.builder()
                        .username("Island Logs")
                        .content(" ")
                        .embeds(Arrays.asList(DiscordEmbed.builder().author(AuthorEmbed.builder().name("Island Logs").icon_url("https://purepng.com/public/uploads/large/71502582731v7y8uylzhygvo3zf71tqjtrwkhwdowkysgsdhsq3vr35woaluanwa4zotpkewhamxijlulfxcrilendabjrjtozyfrqwogphaoic.png").build()).description(finalNewMessage).build()))
                        .avatarUrl("https://purepng.com/public/uploads/large/71502582731v7y8uylzhygvo3zf71tqjtrwkhwdowkysgsdhsq3vr35woaluanwa4zotpkewhamxijlulfxcrilendabjrjtozyfrqwogphaoic.png")
                        .build();
                webhook.sendMessage(build);
            }
        }.runTaskAsynchronously(CloudCore.getInstance());

    }
}
